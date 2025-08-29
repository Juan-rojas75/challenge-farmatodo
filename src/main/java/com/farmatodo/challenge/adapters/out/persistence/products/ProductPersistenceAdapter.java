package com.farmatodo.challenge.adapters.out.persistence.products;


import com.farmatodo.challenge.application.products.port.out.LoadProductsPort;
import com.farmatodo.challenge.domain.products.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class ProductPersistenceAdapter implements LoadProductsPort {

  private final SpringDataProductRepository repo;

  ProductPersistenceAdapter(SpringDataProductRepository repo) { this.repo = repo; }

  @Override
  public List<Product> searchByNameAndStock(String q, int minStock) {
    return repo.findTop50ByNameContainingIgnoreCaseAndStockGreaterThanEqualOrderByNameAsc(q, minStock)
        .stream()
        .map(e -> new Product(e.getId(), e.getSku(), e.getName(), e.getStock(), e.getPrice()))
        .toList();
  }
}
