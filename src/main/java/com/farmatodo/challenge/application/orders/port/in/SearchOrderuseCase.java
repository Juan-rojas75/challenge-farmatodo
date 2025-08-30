package com.farmatodo.challenge.application.orders.port.in;

import java.util.Optional;
import java.util.UUID;

import com.farmatodo.challenge.domain.orders.model.Order;

public interface SearchOrderuseCase {
    Optional<Order> search(UUID query);
}