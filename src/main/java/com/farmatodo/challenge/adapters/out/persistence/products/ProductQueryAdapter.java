package com.farmatodo.challenge.adapters.out.persistence.products;

import com.farmatodo.challenge.application.orders.port.out.LoadProductBySkuPort;

import org.springframework.stereotype.Component;

@Component
class ProductQueryAdapter implements LoadProductBySkuPort {
  private final SpringDataProductRepository repo;
  ProductQueryAdapter(SpringDataProductRepository repo){ this.repo=repo; }
  /**
   * Finds a product by SKU.
   *
   * @param sku The SKU.
   * @return The product if found or empty if not.
   */
  @Override public java.util.Optional<ProductView> findBySku(String sku){
    return repo.findBySku(sku).map(e -> new ProductView(e.getSku(), e.getPrice(), e.getStock()));
  }
}