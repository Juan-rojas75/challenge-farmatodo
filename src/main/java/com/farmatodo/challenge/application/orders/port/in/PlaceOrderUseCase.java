package com.farmatodo.challenge.application.orders.port.in;

import com.farmatodo.challenge.domain.orders.model.OrderStatus;
import java.util.UUID;

public interface PlaceOrderUseCase {
  record Command(UUID customerId, UUID cartId, String shippingAddress,
                 String token, Integer retryCount, Double rejectProbability) {}
  record Result(UUID id, OrderStatus status) {}
  Result place(Command cmd);
}
