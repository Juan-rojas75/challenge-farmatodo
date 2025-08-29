package com.farmatodo.challenge.application.tokenization.port.out;

import com.farmatodo.challenge.domain.tokenization.model.CreditCardToken;

public interface StoreTokenPort {
  CreditCardToken store(CreditCardToken token);
}