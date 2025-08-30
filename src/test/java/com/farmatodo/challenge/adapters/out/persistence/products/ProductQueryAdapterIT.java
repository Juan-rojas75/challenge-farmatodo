package com.farmatodo.challenge.adapters.out.persistence.products;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductQueryAdapterIT {

  @Autowired SpringDataProductRepository repo;

  @Test
  void retorna_optional_por_sku() {
    var e = new ProductJpaEntity();
    e.setId(java.util.UUID.randomUUID());
    e.setSku("SKU-999"); e.setName("X"); e.setStock(5); e.setPrice(1000.0);
    repo.save(e);

    var adapter = new ProductQueryAdapter(repo);
    var opt = adapter.findBySku("SKU-999");
    assertThat(opt).isPresent();
    assertThat(opt.get().price()).isEqualTo(1000.0);
  }
}
