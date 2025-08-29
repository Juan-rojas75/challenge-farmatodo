package com.farmatodo.challenge.application.products.port.out;


import com.farmatodo.challenge.domain.products.model.Product;
import java.util.List;

public interface LoadProductsPort {
  List<Product> searchByNameAndStock(String q, int minStock);
}