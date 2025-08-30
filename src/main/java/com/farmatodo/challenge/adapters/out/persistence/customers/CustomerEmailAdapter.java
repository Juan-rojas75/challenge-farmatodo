package com.farmatodo.challenge.adapters.out.persistence.customers;

import com.farmatodo.challenge.application.orders.port.out.FindCustomerEmailPort;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
class CustomerEmailAdapter implements FindCustomerEmailPort {
  private final SpringDataCustomerRepository repo;
  CustomerEmailAdapter(SpringDataCustomerRepository repo){ this.repo = repo; }

  @Override
  public Optional<String> findEmailById(UUID id) {
    return repo.findById(id).map(CustomerJpaEntity::getEmail);
  }
}