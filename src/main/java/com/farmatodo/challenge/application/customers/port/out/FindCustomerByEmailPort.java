package com.farmatodo.challenge.application.customers.port.out;

import com.farmatodo.challenge.domain.customers.model.Customer;
import java.util.Optional;

public interface FindCustomerByEmailPort {
  Optional<Customer> findByEmail(String email);
}