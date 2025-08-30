package com.farmatodo.challenge.application.orders.port.out;

import java.util.Optional;
import java.util.UUID;

import com.farmatodo.challenge.domain.orders.model.Order;

public interface LoadOrderPort {
  Optional<Order> searchById(UUID id);
}