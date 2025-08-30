package com.farmatodo.challenge.application.orders.port.out;

public interface ChargePaymentPort {
  boolean charge(String token, long amountInCents, double rejectProbability);
}