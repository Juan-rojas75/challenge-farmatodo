package com.farmatodo.challenge.domain.orders.model;

import java.time.Instant; import java.util.*;

import lombok.Data;
@Data
public class Order {
  private final UUID id; private final UUID customerId; private final UUID cartId;
  private final String shippingAddress; private final long amountInCents;
  private OrderStatus status; private final Instant createdAt;
  public Order(UUID id, UUID customerId, UUID cartId, String shippingAddress, long amountInCents){
    this.id=id; this.customerId=customerId; this.cartId=cartId; this.shippingAddress=shippingAddress;
    this.amountInCents=amountInCents; this.status=OrderStatus.CREATED; this.createdAt=Instant.now();
  }
  public UUID getId(){return id;} public OrderStatus getStatus(){return status;} public void markPaid(){status=OrderStatus.PAID;} public void markFailed(){status=OrderStatus.FAILED;}
  public long getAmountInCents(){return amountInCents;}
}
