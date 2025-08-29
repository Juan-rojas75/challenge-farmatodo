package com.farmatodo.challenge.adapters.out.persistence.products;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity @Table(name="products")
public class ProductJpaEntity {
  @Id private UUID id;
  @Column(nullable=false, unique=true) private String sku;
  @Column(nullable=false) private String name;
  @Column(nullable=false) private int stock;
  @Column(nullable=false) private double price;
}
