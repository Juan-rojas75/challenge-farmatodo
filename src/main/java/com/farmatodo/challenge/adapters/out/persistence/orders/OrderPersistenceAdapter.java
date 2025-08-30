package com.farmatodo.challenge.adapters.out.persistence.orders;

import com.farmatodo.challenge.application.orders.port.out.SaveOrderPort;
import com.farmatodo.challenge.domain.orders.model.*; import org.springframework.stereotype.Component;

@Component
class OrderPersistenceAdapter implements SaveOrderPort {
  private final SpringDataOrderRepository repo;
  OrderPersistenceAdapter(SpringDataOrderRepository repo){ this.repo=repo; }

  /**
   * Saves the given order to the database. If the order already exists, updates
   * its properties. Otherwise, creates a new one.
   *
   * @param o the order to save
   * @return the saved order
   */
  @Override public Order save(Order o){
    var e = repo.findById(o.getId()).orElseGet(()->{ var x=new OrderJpaEntity(); x.setId(o.getId()); x.setCreatedAt(java.time.Instant.now()); return x; });
    e.setCustomerId(o.getCustomerId()); e.setCartId(o.getCartId()); e.setShippingAddress(o.getShippingAddress());
    e.setAmountInCents(o.getAmountInCents()); e.setStatus(o.getStatus().name());
    repo.save(e); return o;
  }
}