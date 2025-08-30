package com.farmatodo.challenge.adapters.out.persistence.customers;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface SpringDataCustomerRepository extends JpaRepository<CustomerJpaEntity, UUID> {
  Optional<CustomerJpaEntity> findByEmail(String email);
  Optional<CustomerJpaEntity> findByPhone(String phone);
}