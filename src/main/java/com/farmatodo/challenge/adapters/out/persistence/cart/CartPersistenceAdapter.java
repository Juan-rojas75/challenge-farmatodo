package com.farmatodo.challenge.adapters.out.persistence.cart;

import com.farmatodo.challenge.application.cart.port.out.LoadSaveCartPort;
import com.farmatodo.challenge.application.orders.port.out.LoadCartPort;
import com.farmatodo.challenge.domain.cart.model.*; import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class CartPersistenceAdapter implements LoadSaveCartPort, LoadCartPort {

  private final SpringDataCartRepository repo;
  public CartPersistenceAdapter(SpringDataCartRepository repo){ this.repo=repo; }

  @Override public Optional<Cart> loadByCustomer(UUID customerId){
    return repo.findByCustomerId(customerId).map(this::toDomain);
  }

  @Override public Optional<Cart> loadById(UUID id){
    return repo.findById(id).map(this::toDomain);
  }

  @Override public Cart save(Cart c){
    var e = repo.findById(c.getId()).orElseGet(()->{ var x=new CartJpaEntity(); x.setId(c.getId()); return x; });
    e.setCustomerId(c.getCustomerId());
    e.getItems().clear();
    for (var it: c.getItems()){
      var ci = new CartItemJpaEntity(); ci.setCart(e); ci.setSku(it.sku()); ci.setQty(it.qty()); e.getItems().add(ci);
    }
    return toDomain(repo.save(e));
  }

  private Cart toDomain(CartJpaEntity e){
    var items = e.getItems().stream().map(i -> new CartItem(i.getSku(), i.getQty())).toList();
    return new Cart(e.getId(), e.getCustomerId(), items);
  }
}
