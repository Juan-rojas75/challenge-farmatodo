package com.farmatodo.challenge.adapters.out.persistence.customers;

import com.farmatodo.challenge.application.customers.port.out.FindCustomerByEmailPort;
import com.farmatodo.challenge.application.customers.port.out.FindCustomerByPhonePort;
import com.farmatodo.challenge.application.customers.port.out.SaveCustomerPort;
import com.farmatodo.challenge.domain.customers.model.Customer;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class CustomerPersistenceAdapter implements SaveCustomerPort, FindCustomerByEmailPort, FindCustomerByPhonePort {

  private final SpringDataCustomerRepository repo;

  CustomerPersistenceAdapter(SpringDataCustomerRepository repo) { this.repo = repo; }

  @Override
  public Customer save(Customer c) {
    CustomerJpaEntity e = new CustomerJpaEntity();
    e.setId(c.getId() != null ? c.getId() : UUID.randomUUID());
    e.setName(c.getName()); e.setEmail(c.getEmail()); e.setPhone(c.getPhone()); e.setAddress(c.getAddress());
    e = repo.save(e);
    return new Customer(e.getId(), e.getName(), e.getEmail(), e.getPhone(), e.getAddress());
  }

  @Override
  public Optional<Customer> findByEmail(String email) {
    return repo.findByEmail(email).map(e -> new Customer(e.getId(), e.getName(), e.getEmail(), e.getPhone(), e.getAddress()));
  }
  
  @Override
  public Optional<Customer> findByPhone(String phone) {
    return repo.findByPhone(phone).map(e -> new Customer(e.getId(), e.getName(), e.getEmail(), e.getPhone(), e.getAddress()));
  }
}