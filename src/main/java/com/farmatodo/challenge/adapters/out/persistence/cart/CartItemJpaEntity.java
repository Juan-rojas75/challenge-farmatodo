package com.farmatodo.challenge.adapters.out.persistence.cart;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Entity @Table(name = "cart_items")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CartItemJpaEntity {

  @Id @GeneratedValue
  @EqualsAndHashCode.Include
  @ToString.Include
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cart_id", nullable = false)
  @ToString.Exclude
  private CartJpaEntity cart;

  @Column(nullable = false, length = 80)
  private String sku;

  @Column(nullable = false)
  private int qty;
}