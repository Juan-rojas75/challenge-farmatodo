package com.farmatodo.challenge.adapters.out.persistence.products;


import com.farmatodo.challenge.application.products.port.out.LoadProductsPort;
import com.farmatodo.challenge.application.products.port.out.SaveProductPort;
import com.farmatodo.challenge.domain.products.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
class ProductPersistenceAdapter implements LoadProductsPort, SaveProductPort {

  private final SpringDataProductRepository repo;

  ProductPersistenceAdapter(SpringDataProductRepository repo) { this.repo = repo; }

  @Override
  public List<Product> searchByNameAndStock(String q, int minStock) {
    return repo.findTop50ByNameContainingIgnoreCaseAndStockGreaterThanEqualOrderByNameAsc(q, minStock)
        .stream()
        .map(e -> new Product(e.getId(), e.getSku(), e.getName(), e.getStock(), e.getPrice()))
        .toList();
  }

  @Override
  public Product save(Product c) {
    ProductJpaEntity e = new ProductJpaEntity();
    e.setId(c.getId() != null ? c.getId() : UUID.randomUUID());
    e.setName(c.getName()); e.setSku(c.getSku()); e.setPrice(c.getPrice()); e.setStock(c.getStock());
    e = repo.save(e);
    return new Product(e.getId(), e.getName(), e.getSku(), e.getStock(), e.getPrice());
  }
}
