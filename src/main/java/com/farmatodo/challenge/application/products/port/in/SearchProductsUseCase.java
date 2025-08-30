package com.farmatodo.challenge.application.products.port.in;


import com.farmatodo.challenge.domain.products.model.Product;
import java.util.List;

public interface SearchProductsUseCase {
  record Query(String q, Integer minStock, String requestedBy) {}
  List<Product> search(Query query);
}