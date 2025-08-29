package com.farmatodo.challenge.adapters.out.persistence.tokenization;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity @Table(name="card_tokens")
public class TokenJpaEntity {
  @Id private UUID id;
  @Column(nullable=false, unique=true) private String token;
  @Column(nullable=false) private Instant createdAt;
}