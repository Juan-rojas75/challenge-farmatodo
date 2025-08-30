package com.farmatodo.challenge.domain.tokenization.model;

import java.time.Instant;
import java.util.UUID;

public class CreditCardToken {
  private final UUID id;
  private final String token;
  private final Instant createdAt;

  public CreditCardToken(UUID id, String token, Instant createdAt) {
    this.id = id; this.token = token; this.createdAt = createdAt;
  }
  public UUID getId() { return id; }
  public String getToken() { return token; }
  public Instant getCreatedAt() { return createdAt; }
}
