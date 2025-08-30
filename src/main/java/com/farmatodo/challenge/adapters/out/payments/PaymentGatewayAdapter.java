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

  @Override
  public boolean charge(String token, long amountInCents, double rejectProbability) {
    double p = Double.isNaN(rejectProbability) ? props.getRejectProbability() : rejectProbability;
    double r = rnd.nextDouble();
    return !(r < p);
  }
}