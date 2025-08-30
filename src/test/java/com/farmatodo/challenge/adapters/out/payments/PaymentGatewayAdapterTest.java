package com.farmatodo.challenge.adapters.out.payments;

import com.farmatodo.challenge.bootstrap.config.PaymentsProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentGatewayAdapterTest {

  @Test
  void aprueba_si_probabilidad_parametro_es_cero() {
    var props = new PaymentsProperties();
    props.setRejectProbability(0.99); // no afecta porque pasamos 0.0
    var adapter = new PaymentGatewayAdapter(props);

    // con p=0.0, r < p nunca se cumple => siempre true
    for (int i = 0; i < 5; i++) {
      boolean ok = adapter.charge("tok", 12345L, 0.0);
      assertThat(ok).isTrue();
    }
  }

  @Test
  void rechaza_si_probabilidad_parametro_es_uno() {
    var props = new PaymentsProperties();
    props.setRejectProbability(0.0); // no afecta porque pasamos 1.0
    var adapter = new PaymentGatewayAdapter(props);

    // con p=1.0, r < 1.0 siempre se cumple (nextDouble ∈ [0,1)) => siempre false
    for (int i = 0; i < 5; i++) {
      boolean ok = adapter.charge("tok", 999L, 1.0);
      assertThat(ok).isFalse();
    }
  }

  @Test
  void usa_probabilidad_de_props_si_parametro_es_NaN_y_aprueba_con_props_en_cero() {
    var props = new PaymentsProperties();
    props.setRejectProbability(0.0); // aprueba siempre
    var adapter = new PaymentGatewayAdapter(props);

    boolean ok = adapter.charge("tok", 5000L, Double.NaN);
    assertThat(ok).isTrue();
  }

  @Test
  void usa_probabilidad_de_props_si_parametro_es_NaN_y_rechaza_con_props_en_uno() {
    var props = new PaymentsProperties();
    props.setRejectProbability(1.0); // rechaza siempre
    var adapter = new PaymentGatewayAdapter(props);

    boolean ok = adapter.charge("tok", 5000L, Double.NaN);
    assertThat(ok).isFalse();
  }
}
