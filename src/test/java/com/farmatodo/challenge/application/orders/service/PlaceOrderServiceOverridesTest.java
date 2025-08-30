package com.farmatodo.challenge.application.orders.service;

import com.farmatodo.challenge.application.logs.port.out.EventLoggerPort;
import com.farmatodo.challenge.application.orders.port.in.PlaceOrderUseCase;
import com.farmatodo.challenge.application.orders.port.out.*;
import com.farmatodo.challenge.bootstrap.config.PaymentsProperties;
import com.farmatodo.challenge.domain.cart.model.Cart;
import com.farmatodo.challenge.domain.cart.model.CartItem;
import com.farmatodo.challenge.domain.orders.model.OrderStatus;
import org.junit.jupiter.api.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PlaceOrderServiceOverridesTest {

  LoadCartPort loadCart = mock(LoadCartPort.class);
  LoadProductBySkuPort loadProd = mock(LoadProductBySkuPort.class);
  SaveOrderPort saveOrder = mock(SaveOrderPort.class);
  LoadOrderPort loadOrder = mock(LoadOrderPort.class);
  ChargePaymentPort payments = mock(ChargePaymentPort.class);
  SendEmailPort mail = mock(SendEmailPort.class);
  FindCustomerEmailPort findEmail = mock(FindCustomerEmailPort.class);
  PaymentsProperties props = new PaymentsProperties();
  PlaceOrderService svc;
  EventLoggerPort eventLogger = mock(EventLoggerPort.class);

  @BeforeEach void init(){
    props.setRetryCount(0); props.setRejectProbability(1.0);
    svc = new PlaceOrderService(loadCart, loadProd, saveOrder, loadOrder, payments, mail, findEmail, props, eventLogger);
  }

  @Test
  void request_override_funciona() {
    var cartId = UUID.randomUUID(); var custId = UUID.randomUUID();
    var cart = new Cart(cartId, custId, List.of(new CartItem("SKU-1",1)));
    when(loadCart.loadById(cartId)).thenReturn(Optional.of(cart));
    when(loadProd.findBySku("SKU-1")).thenReturn(Optional.of(new LoadProductBySkuPort.ProductView("SKU-1", 10.0, 99)));
    when(saveOrder.save(any())).thenAnswer(i -> i.getArgument(0));
    when(findEmail.findEmailById(custId)).thenReturn(Optional.of("user@mail.com"));
    when(payments.charge(any(), anyLong(), eq(0.0))).thenReturn(true); // override prob=0.0

    var res = svc.place(new PlaceOrderUseCase.Command(
        custId, cartId, "addr", "tok", 0, 0.0));

    assertThat(res.status()).isEqualTo(OrderStatus.PAID);
    verify(mail, times(1)).send(eq("user@mail.com"), contains("Pago exitoso"), any());
  }
}
