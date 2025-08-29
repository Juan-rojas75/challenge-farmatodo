package com.farmatodo.challenge.application.customers.port.out;

import java.util.Optional;

import com.farmatodo.challenge.domain.customers.model.Customer;

public interface FindCustomerByPhonePort {
    Optional<Customer> findByPhone(String phone);
}
