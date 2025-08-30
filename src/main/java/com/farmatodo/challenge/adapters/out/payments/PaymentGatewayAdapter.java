package com.farmatodo.challenge.adapters.out.payments;

import com.farmatodo.challenge.application.orders.port.out.ChargePaymentPort;
import com.farmatodo.challenge.bootstrap.config.PaymentsProperties;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
class PaymentGatewayAdapter implements ChargePaymentPort {
  private final PaymentsProperties props;
  private final SecureRandom rnd = new SecureRandom();
  PaymentGatewayAdapter(PaymentsProperties props){ this.props = props; }

  /**
   * Simulates a payment gateway that rejects payment attempts with a given probability.
   *
   * @param token ignored
   * @param amountInCents ignored
   * @param rejectProbability probability of rejecting the payment, between 0.0 and 1.0.
   *  if NaN, {@link PaymentsProperties#getRejectProbability()} is used instead.
   * @return true if the payment was accepted, false otherwise.
   */
  @Override
  public boolean charge(String token, long amountInCents, double rejectProbability) {
    double p = Double.isNaN(rejectProbability) ? props.getRejectProbability() : rejectProbability;
    double r = rnd.nextDouble();
    return !(r < p);
  }
}