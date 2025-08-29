package com.farmatodo.challenge.application.orders.port.out;

import java.util.Optional;
public interface LoadProductBySkuPort {
  Optional<ProductView> findBySku(String sku);
  record ProductView(String sku, double price, int stock) {}
}
