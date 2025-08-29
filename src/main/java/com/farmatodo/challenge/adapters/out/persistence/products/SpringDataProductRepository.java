package com.farmatodo.challenge.adapters.out.persistence.products;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;


public interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, UUID> {
  List<ProductJpaEntity> findTop50ByNameContainingIgnoreCaseAndStockGreaterThanEqualOrderByNameAsc(String name, int stock);
  Optional<ProductJpaEntity> findBySku(String sku);
}