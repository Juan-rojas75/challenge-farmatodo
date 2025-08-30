package com.farmatodo.challenge.application.orders.port.out;

import java.util.Optional;
import java.util.UUID;
public interface FindCustomerEmailPort {
  Optional<String> findEmailById(UUID customerId);
}
