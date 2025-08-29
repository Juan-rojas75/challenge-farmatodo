package com.farmatodo.challenge.adapters.out.persistence.orders;

import com.farmatodo.challenge.application.orders.port.out.SaveOrderPort;
import com.farmatodo.challenge.domain.orders.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderPersistenceAdapterIT {

  @Autowired SpringDataOrderRepository repo;

  SaveOrderPort newAdapter(){ return new OrderPersistenceAdapter(repo); }

  @Test
  void guarda_y_actualiza_estado() {
    var adapter = newAdapter();
    var order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "Addr", 12345);
    adapter.save(order); // CREATED

    order.markFailed();
    adapter.save(order);

    var e = repo.findById(order.getId()).orElseThrow();
    assertThat(e.getStatus()).isEqualTo("FAILED");
  }
}
