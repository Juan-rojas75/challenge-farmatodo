package com.farmatodo.challenge.application.tokenization.port.in;


import com.farmatodo.challenge.domain.tokenization.model.CreditCardToken;

public interface TokenizeCardUseCase {
  record Command(String cardNumber, String cvv, int expMonth, int expYear, String holder){}
  CreditCardToken execute(Command cmd);
}