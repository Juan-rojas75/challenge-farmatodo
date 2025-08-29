package com.farmatodo.challenge.adapters.out.persistence.tokenization;

import com.farmatodo.challenge.application.tokenization.port.out.StoreTokenPort;
import com.farmatodo.challenge.domain.tokenization.model.CreditCardToken;
import org.springframework.stereotype.Component;

@Component
class TokenPersistenceAdapter implements StoreTokenPort {

  private final SpringDataTokenRepository repo;

  TokenPersistenceAdapter(SpringDataTokenRepository repo) { this.repo = repo; }

  @Override
  public CreditCardToken store(CreditCardToken t) {
    TokenJpaEntity e = new TokenJpaEntity();
    e.setId(t.getId()); e.setToken(t.getToken()); e.setCreatedAt(t.getCreatedAt());
    repo.save(e);
    return t;
  }
}