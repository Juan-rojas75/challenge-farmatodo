package com.farmatodo.challenge.adapters.out.persistence.cart;

import com.farmatodo.challenge.domain.cart.model.Cart;
import com.farmatodo.challenge.domain.cart.model.CartItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CartPersistenceAdapterIT {

  @Autowired
  SpringDataCartRepository repo;

  private CartPersistenceAdapter newAdapter() {
    return new CartPersistenceAdapter(repo);
  }

  @Test
  void save_inserta_nuevo_carrito_con_items() {
    var adapter = newAdapter();

    var cartId = UUID.randomUUID();
    var customerId = UUID.randomUUID();
    var cart = new Cart(cartId, customerId, List.of(
        new CartItem("SKU-1", 2),
        new CartItem("SKU-2", 1)
    ));

    var saved = adapter.save(cart);

    assertThat(saved.getId()).isEqualTo(cartId);
    assertThat(saved.getCustomerId()).isEqualTo(customerId);
    assertThat(saved.getItems())
        .extracting(CartItem::sku, CartItem::qty)
        .containsExactlyInAnyOrder(
            org.assertj.core.groups.Tuple.tuple("SKU-1", 2),
            org.assertj.core.groups.Tuple.tuple("SKU-2", 1)
        );

    var e = repo.findById(cartId).orElseThrow();
    assertThat(e.getItems()).hasSize(2);
  }

  @Test
  void save_actualiza_reemplazando_items() {
    var adapter = newAdapter();

    var cartId = UUID.randomUUID();
    var customerId = UUID.randomUUID();

    // Base en BD (sin items aún)
    var base = new CartJpaEntity();
    base.setId(cartId);
    base.setCustomerId(customerId);
    repo.save(base);

    // Primera versión: A(1), B(2)
    var v1 = new Cart(cartId, customerId, List.of(
        new CartItem("A", 1),
        new CartItem("B", 2)
    ));
    adapter.save(v1);

    // Segunda versión (reemplaza): A(5), C(1)  -> B desaparece
    var v2 = new Cart(cartId, customerId, List.of(
        new CartItem("A", 5),
        new CartItem("C", 1)
    ));
    adapter.save(v2);

    var e = repo.findById(cartId).orElseThrow();
    assertThat(e.getItems())
        .extracting(CartItemJpaEntity::getSku, CartItemJpaEntity::getQty)
        .containsExactlyInAnyOrder(
            org.assertj.core.groups.Tuple.tuple("A", 5),
            org.assertj.core.groups.Tuple.tuple("C", 1)
        );
  }

  @Test
  void loadById_mapea_a_dominio() {
    var adapter = newAdapter();

    var cartId = UUID.randomUUID();
    var customerId = UUID.randomUUID();

    var e = new CartJpaEntity();
    e.setId(cartId);
    e.setCustomerId(customerId);

    var i = new CartItemJpaEntity();
    i.setCart(e);
    i.setSku("SKU-X");
    i.setQty(3);
    e.getItems().add(i);

    repo.save(e);

    var opt = adapter.loadById(cartId);
    assertThat(opt).isPresent();
    var c = opt.get();
    assertThat(c.getId()).isEqualTo(cartId);
    assertThat(c.getCustomerId()).isEqualTo(customerId);
    assertThat(c.getItems()).extracting(CartItem::sku, CartItem::qty)
        .containsExactly(org.assertj.core.groups.Tuple.tuple("SKU-X", 3));
  }

  @Test
  void loadByCustomer_encuentra_por_customerId() {
    var adapter = newAdapter();

    var cartId = UUID.randomUUID();
    var customerId = UUID.randomUUID();

    var e = new CartJpaEntity();
    e.setId(cartId);
    e.setCustomerId(customerId);
    repo.save(e);

    var opt = adapter.loadByCustomer(customerId);
    assertThat(opt).isPresent();
    assertThat(opt.get().getId()).isEqualTo(cartId);
  }

  @Test
  void loadByCustomer_vacio_si_no_existe() {
    var adapter = newAdapter();
    var opt = adapter.loadByCustomer(UUID.randomUUID());
    assertThat(opt).isEmpty();
  }
}
