package com.farmatodo.challenge.adapters.out.persistence.cart;

import org.springframework.data.jpa.repository.*; import java.util.*;

public interface SpringDataCartRepository extends JpaRepository<CartJpaEntity, UUID> {
  Optional<CartJpaEntity> findByCustomerId(UUID customerId);
}