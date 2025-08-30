package com.farmatodo.challenge.adapters.out.persistence.cart;

import com.farmatodo.challenge.application.cart.port.out.LoadSaveCartPort;
import com.farmatodo.challenge.application.orders.port.out.LoadCartPort;
import com.farmatodo.challenge.domain.cart.model.*; import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class CartPersistenceAdapter implements LoadSaveCartPort, LoadCartPort {

  private final SpringDataCartRepository repo;
  public CartPersistenceAdapter(SpringDataCartRepository repo){ this.repo=repo; }

  /**
   * Loads a cart by its customer id. If the cart does not exist, returns an
   * empty Optional.
   *
   * @param customerId the id of the customer whose cart should be loaded
   * @return the loaded cart, or an empty Optional if the cart does not exist
   */
  @Override public Optional<Cart> loadByCustomer(UUID customerId){
    return repo.findByCustomerId(customerId).map(this::toDomain);
  }

  /**
   * Loads a cart by its id. If the cart does not exist, returns an empty
   * Optional.
   *
   * @param id the id of the cart to load
   * @return the loaded cart, or an empty Optional if the cart does not exist
   */
  @Override public Optional<Cart> loadById(UUID id){
    return repo.findById(id).map(this::toDomain);
  }

  /**
   * Saves the given cart to the database. If the cart already exists, updates
   * its items. Otherwise, creates a new one.
   *
   * @param c the cart to save
   * @return the saved cart
   */
  @Override public Cart save(Cart c){
    var e = repo.findById(c.getId()).orElseGet(()->{ var x=new CartJpaEntity(); x.setId(c.getId()); return x; });
    e.setCustomerId(c.getCustomerId());
    e.getItems().clear();
    for (var it: c.getItems()){
      var ci = new CartItemJpaEntity(); ci.setCart(e); ci.setSku(it.sku()); ci.setQty(it.qty()); e.getItems().add(ci);
    }
    return toDomain(repo.save(e));
  }

  /**
   * Converts a CartJpaEntity to a Cart, given by its items
   *
   * @param e the JPA entity to convert
   * @return a Cart domain object
   */
  private Cart toDomain(CartJpaEntity e){
    var items = e.getItems().stream().map(i -> new CartItem(i.getSku(), i.getQty())).toList();
    return new Cart(e.getId(), e.getCustomerId(), items);
  }
}
