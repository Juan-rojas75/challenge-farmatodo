package com.farmatodo.challenge.application.cart.service;

import com.farmatodo.challenge.application.cart.port.in.AddToCartUseCase;
import com.farmatodo.challenge.application.cart.port.out.LoadSaveCartPort;
import com.farmatodo.challenge.domain.cart.model.Cart;
import com.farmatodo.challenge.domain.cart.model.CartItem;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AddToCartServiceTest {

  @Test
  void crea_carrito_si_no_existe_y_consolida_items() {
    var port = mock(LoadSaveCartPort.class);
    var svc = new AddToCartService(port);

    var customerId = UUID.randomUUID();
    when(port.loadByCustomer(customerId)).thenReturn(Optional.empty());
    when(port.save(any())).thenAnswer(i -> i.getArgument(0));

    var cmd = new AddToCartUseCase.Command(customerId, List.of(
        new AddToCartUseCase.Command.Item("SKU-1", 2),
        new AddToCartUseCase.Command.Item("SKU-1", 1),
        new AddToCartUseCase.Command.Item("SKU-2", 3)
    ));
    svc.addOrCreate(cmd);

    ArgumentCaptor<Cart> cap = ArgumentCaptor.forClass(Cart.class);
    verify(port).save(cap.capture());
    var saved = cap.getValue();

    assertThat(saved.getCustomerId()).isEqualTo(customerId);
    assertThat(saved.getItems()).extracting(CartItem::sku, CartItem::qty)
        .containsExactlyInAnyOrder(
            org.assertj.core.groups.Tuple.tuple("SKU-1", 3),
            org.assertj.core.groups.Tuple.tuple("SKU-2", 3)
        );
  }

  @Test
  void actualiza_carrito_existente() {
    var port = mock(LoadSaveCartPort.class);
    var svc = new AddToCartService(port);

    var cartId = UUID.randomUUID();
    var customerId = UUID.randomUUID();
    var existing = new Cart(cartId, customerId, List.of(new CartItem("SKU-1", 1)));
    when(port.loadByCustomer(customerId)).thenReturn(Optional.of(existing));
    when(port.save(any())).thenAnswer(i -> i.getArgument(0));

    var cmd = new AddToCartUseCase.Command(customerId, List.of(
        new AddToCartUseCase.Command.Item("SKU-1", 2),
        new AddToCartUseCase.Command.Item("SKU-2", 1)
    ));
    svc.addOrCreate(cmd);

    ArgumentCaptor<Cart> cap = ArgumentCaptor.forClass(Cart.class);
    verify(port).save(cap.capture());
    var saved = cap.getValue();

    assertThat(saved.getId()).isEqualTo(cartId);
    assertThat(saved.getItems()).extracting(CartItem::sku, CartItem::qty)
        .containsExactlyInAnyOrder(
            org.assertj.core.groups.Tuple.tuple("SKU-1", 3),
            org.assertj.core.groups.Tuple.tuple("SKU-2", 1)
        );
  }
}
