package com.farmatodo.challenge.application.orders.service;

import com.farmatodo.challenge.application.logs.port.out.EventLoggerPort;
import com.farmatodo.challenge.application.orders.port.in.PlaceOrderUseCase;
import com.farmatodo.challenge.application.orders.port.out.*;
import com.farmatodo.challenge.bootstrap.config.PaymentsProperties;
import com.farmatodo.challenge.domain.cart.model.Cart;
import com.farmatodo.challenge.domain.orders.model.*;
import com.farmatodo.challenge.shared.exception.ResourceNotFoundException;
import com.farmatodo.challenge.shared.logging.TxContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional
public class PlaceOrderService implements PlaceOrderUseCase {

  private final LoadCartPort loadCart;
  private final LoadProductBySkuPort loadProd;
  private final SaveOrderPort saveOrder;
  private final ChargePaymentPort payments;
  private final SendEmailPort mail;
  private final FindCustomerEmailPort findEmail;
  private final PaymentsProperties props;
  private final EventLoggerPort eventLogger;


  public PlaceOrderService(LoadCartPort loadCart, LoadProductBySkuPort loadProd, SaveOrderPort saveOrder,
                           ChargePaymentPort payments, SendEmailPort mail,
                           FindCustomerEmailPort findEmail, PaymentsProperties props, EventLoggerPort eventLogger) {
    this.loadCart = loadCart; this.loadProd = loadProd; this.saveOrder = saveOrder;
    this.payments = payments; this.mail = mail; this.findEmail = findEmail; this.props = props ; this.eventLogger = eventLogger;
  }

  @Override
  public Result place(Command cmd) {
    Cart cart = loadCart.loadById(cmd.cartId())
        .orElseThrow(() -> new ResourceNotFoundException("Carrito no existe"));
    if (cart.isEmpty()) throw new IllegalArgumentException("Carrito vacío");

    long total = computeTotal(cart);
    var order = new Order(UUID.randomUUID(), cmd.customerId(), cmd.cartId(), cmd.shippingAddress(), total);
    order = saveOrder.save(order);

    int max = cmd.retryCount() != null ? cmd.retryCount() : props.getRetryCount();
    double p = cmd.rejectProbability() != null ? cmd.rejectProbability() : props.getRejectProbability();

    boolean ok = false;
    for (int i = 0; i <= max; i++) {
      ok = payments.charge(cmd.token(), total, p);
      if (ok) { order.markPaid(); break; }
    }

    if (!ok) {
      order.markFailed();
      String to = findEmail.findEmailById(cmd.customerId()).orElse("noreply@example.com");
      mail.send(to, "Pago fallido", "Tu pago fue rechazado. Intenta nuevamente.");
    }

    String to = findEmail.findEmailById(cmd.customerId()).orElse("noreply@example.com");

    if (ok) {
       eventLogger.log("PAYMENT_SUCCESS", TxContext.get(), """
        {"orderId":"%s"}
      """.formatted(order.getId()));
      order.markPaid();
      // Usa el adapter con plantilla
      if (mail instanceof com.farmatodo.challenge.adapters.out.email.SpringMailAdapter m) {
        m.sendTemplate(to, "Pago exitoso",
            "mail/payment-success", Map.of(
              "name", "Cliente",                 // si tienes nombre, úsalo
              "orderId", order.getId(),
              "amount", String.format("%,.2f", total/100.0)
            ));
      } else {
        mail.send(to, "Pago exitoso",
            "<b>Pago exitoso</b> del pedido " + order.getId());
      }
    } else {
        eventLogger.log("PAYMENT_FAILED", TxContext.get(), """
          {"orderId":"%s"}
        """.formatted(order.getId()));
      order.markFailed();
      if (mail instanceof com.farmatodo.challenge.adapters.out.email.SpringMailAdapter m) {
        m.sendTemplate(to, "Pago rechazado",
            "mail/payment-failed", Map.of(
              "name", "Cliente",
              "orderId", order.getId()
            ));
      } else {
        mail.send(to, "Pago rechazado",
            "Tu pago del pedido " + order.getId() + " fue rechazado.");
      }
    }

    order = saveOrder.save(order);
    eventLogger.log("ORDER_CREATED", TxContext.get(), """
      {"orderId":"%s","customerId":"%s","amount":%d}
    """.formatted(order.getId(), cmd.customerId(), total));
    return new Result(order.getId(), order.getStatus());
  }

  private long computeTotal(Cart cart) {
    long sum = 0;
    for (var it : cart.getItems()) {
      var pv = loadProd.findBySku(it.sku())
          .orElseThrow(() -> new ResourceNotFoundException("SKU no existe: " + it.sku()));
      sum += Math.round(pv.price() * 100) * it.qty();
    }
    return sum;
  }
}