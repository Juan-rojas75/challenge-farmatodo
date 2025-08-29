package com.farmatodo.challenge.adapters.out.persistence.orders;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity @Table(name="orders")
public class OrderJpaEntity {
  @Id
  @Column(columnDefinition = "uuid")
  private java.util.UUID id;

  @Column(nullable=false, columnDefinition = "uuid")
  private java.util.UUID customerId;

  @Column(nullable=false, columnDefinition = "uuid")
  private java.util.UUID cartId;

  @Column(nullable=false) private long amountInCents;
  @Column(nullable=false, length=20) private String status;
  @Column(nullable=false) private java.time.Instant createdAt;
  private String shippingAddress;
}
