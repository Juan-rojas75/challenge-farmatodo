package com.farmatodo.challenge.adapters.out.persistence.searchlog;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity @Table(name="product_search_log")
public class SearchLogJpaEntity {
  @Id @GeneratedValue private UUID id;
  @Column(nullable=false) private String q;
  @Column(nullable=false) private int minStock;
  @Column(nullable=false) private String requestedBy;
  @Column(nullable=false) private Instant createdAt;
}
