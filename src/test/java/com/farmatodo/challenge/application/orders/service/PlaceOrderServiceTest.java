package com.farmatodo.challenge.application.orders.service;

import com.farmatodo.challenge.application.logs.port.out.EventLoggerPort;
import com.farmatodo.challenge.application.orders.port.in.PlaceOrderUseCase;
import com.farmatodo.challenge.application.orders.port.out.*;
import com.farmatodo.challenge.bootstrap.config.PaymentsProperties;
import com.farmatodo.challenge.domain.cart.model.Cart;
import com.farmatodo.challenge.domain.cart.model.CartItem;
import com.farmatodo.challenge.domain.orders.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceOrderServiceTest {

  private LoadCartPort loadCart;
  private LoadProductBySkuPort loadProd;
  private SaveOrderPort saveOrder;
  private LoadOrderPort search;
  private ChargePaymentPort payments;
  private SendEmailPort mail;
  private FindCustomerEmailPort findEmail;
  private PaymentsProperties props;
  private PlaceOrderService svc;
  private EventLoggerPort eventLogger;

  @BeforeEach
  void setUp() {
    loadCart = mock(LoadCartPort.class);
    loadProd = mock(LoadProductBySkuPort.class);
    saveOrder = mock(SaveOrderPort.class);
    payments = mock(ChargePaymentPort.class);
    mail = mock(SendEmailPort.class);
    findEmail = mock(FindCustomerEmailPort.class);
    props = new PaymentsProperties();
    props.setRetryCount(2);
    props.setRejectProbability(0.0);
    eventLogger = mock(EventLoggerPort.class);
    svc = new PlaceOrderService(loadCart, loadProd, saveOrder, search, payments, mail, findEmail, props, eventLogger);
  }

  @Test
  void paga_ok_y_no_envia_correo_fallo() {
    var cartId = UUID.randomUUID();
    var custId = UUID.randomUUID();
    var cart = new Cart(cartId, custId, List.of(new CartItem("SKU-1", 1)));

    when(loadCart.loadById(cartId)).thenReturn(Optional.of(cart));
    when(loadProd.findBySku("SKU-1")).thenReturn(Optional.of(new LoadProductBySkuPort.ProductView("SKU-1", 10.0, 99)));
    when(saveOrder.save(any())).thenAnswer(i -> i.getArgument(0));
    when(payments.charge(any(), anyLong(), anyDouble())).thenReturn(true);
    when(findEmail.findEmailById(custId)).thenReturn(Optional.of("user@mail.com")); // <- importante

    var res = svc.place(new PlaceOrderUseCase.Command(
        custId, cartId, "addr", "tok", null, null));

    assertThat(res.status()).isEqualTo(OrderStatus.PAID);

    verify(mail, never()).send(eq("user@mail.com"), contains("Pago fallido"), any());

    verify(mail, times(1)).send(eq("user@mail.com"), contains("Pago exitoso"), any());
  }


  @Test
  void paga_ok_y_envia_correo_exito() {
    var cartId = UUID.randomUUID();
    var custId = UUID.randomUUID();
    var cart = new Cart(cartId, custId, List.of(new CartItem("SKU-1", 2)));

    when(loadCart.loadById(cartId)).thenReturn(Optional.of(cart));
    when(loadProd.findBySku("SKU-1")).thenReturn(Optional.of(new LoadProductBySkuPort.ProductView("SKU-1", 10.0, 99)));
    when(saveOrder.save(any())).thenAnswer(i -> i.getArgument(0));
    when(payments.charge(any(), anyLong(), anyDouble())).thenReturn(true);
    when(findEmail.findEmailById(custId)).thenReturn(Optional.of("user@mail.com"));

    var res = svc.place(new PlaceOrderUseCase.Command(
        custId, cartId, "addr", "tok", null, null));

    assertThat(res.status()).isEqualTo(OrderStatus.PAID);
    verify(mail).send(eq("user@mail.com"), contains("Pago exitoso"), any());
    verify(mail, never()).send(eq("user@mail.com"), contains("Pago fallido"), any());
  }

}
