package com.farmatodo.challenge.adapters.out.persistence.customers;

import com.farmatodo.challenge.domain.customers.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerPersistenceAdapterIT {

  @Autowired
  SpringDataCustomerRepository repo;

  CustomerPersistenceAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new CustomerPersistenceAdapter(repo);
  }

  @Test
  void save_genera_id_si_es_null_y_persiste() {
    var in = new Customer(null, "Ana", "ana@ex.com", "3001234567", "Calle 1");
    var out = adapter.save(in);

    assertThat(out.getId()).isNotNull();
    var e = repo.findById(out.getId()).orElseThrow();
    assertThat(e.getName()).isEqualTo("Ana");
    assertThat(e.getEmail()).isEqualTo("ana@ex.com");
    assertThat(e.getPhone()).isEqualTo("3001234567");
    assertThat(e.getAddress()).isEqualTo("Calle 1");
  }

  @Test
  void save_respeta_id_existente() {
    var id = UUID.randomUUID();
    var in = new Customer(id, "Bob", "bob@ex.com", "3010000000", "Calle 2");
    var out = adapter.save(in);

    assertThat(out.getId()).isEqualTo(id);
    assertThat(repo.findById(id)).isPresent();
  }

  @Test
  void findByEmail_retorna_customer() {
    var e = new CustomerJpaEntity();
    e.setId(UUID.randomUUID());
    e.setName("Carla");
    e.setEmail("carla@ex.com");
    e.setPhone("3021111111");
    e.setAddress("Calle 3");
    repo.save(e);

    var opt = adapter.findByEmail("carla@ex.com");
    assertThat(opt).isPresent();
    assertThat(opt.get().getName()).isEqualTo("Carla");
    assertThat(opt.get().getPhone()).isEqualTo("3021111111");
  }

  @Test
  void findByPhone_retorna_customer() {
    var e = new CustomerJpaEntity();
    e.setId(UUID.randomUUID());
    e.setName("Diego");
    e.setEmail("diego@ex.com");
    e.setPhone("3032222222");
    e.setAddress("Calle 4");
    repo.save(e);

    var opt = adapter.findByPhone("3032222222");
    assertThat(opt).isPresent();
    assertThat(opt.get().getEmail()).isEqualTo("diego@ex.com");
  }

  @Test
  void findByPhone_vacio_si_no_existe() {
    var opt = adapter.findByPhone("0000000000");
    assertThat(opt).isEmpty();
  }
}
