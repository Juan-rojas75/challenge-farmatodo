package com.farmatodo.challenge.application.customers.port.out;

import com.farmatodo.challenge.domain.customers.model.Customer;

public interface SaveCustomerPort {
  Customer save(Customer customer);
}