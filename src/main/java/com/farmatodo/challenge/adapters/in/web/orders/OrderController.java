package com.farmatodo.challenge.adapters.in.web.orders;

import com.farmatodo.challenge.application.orders.port.in.PlaceOrderUseCase;
import com.farmatodo.challenge.adapters.in.web.orders.request.CreateOrderRequest;
import com.farmatodo.challenge.adapters.in.web.orders.response.OrderResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {
  private final PlaceOrderUseCase place;
  public OrderController(PlaceOrderUseCase place){ this.place = place; }

  @PostMapping
  public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest r){
    var res = place.place(new PlaceOrderUseCase.Command(
        UUID.fromString(r.customerId()), UUID.fromString(r.cartId()),
        r.shippingAddress(), r.token(), r.retryCount(), r.rejectProbability()
    ));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new OrderResponse(res.id().toString(), res.status().name()));
  }
}