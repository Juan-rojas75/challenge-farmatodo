package com.farmatodo.challenge.application.tokenization.service;

import com.farmatodo.challenge.application.tokenization.port.in.TokenizeCardUseCase;
import com.farmatodo.challenge.application.tokenization.port.out.StoreTokenPort;
import com.farmatodo.challenge.bootstrap.config.TokenizationProperties;
import com.farmatodo.challenge.domain.tokenization.model.CreditCardToken;
import com.farmatodo.challenge.shared.exception.TokenizationRejectedException;
import org.junit.jupiter.api.Test;

import java.util.function.DoubleSupplier;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenizeCardServiceTest {

  @Test
  void rechaza_cuando_random_menor_a_probabilidad() {
    var props = new TokenizationProperties(); props.setRejectProbability(0.5);
    var store = mock(StoreTokenPort.class);
    DoubleSupplier rnd = () -> 0.2; // 0.2 < 0.5 → rechaza

    var svc = new TokenizeCardService(store, props, rnd);

    assertThatThrownBy(() -> svc.execute(new TokenizeCardUseCase.Command(
        "4111111111111111", "123", 12, 2030, "JOHN DOE")))
        .isInstanceOf(TokenizationRejectedException.class);

    verify(store, never()).store(any());
  }

  @Test
  void acepta_y_persiste_token_valido() {
    var props = new TokenizationProperties(); props.setRejectProbability(0.2);
    var store = mock(StoreTokenPort.class);
    DoubleSupplier rnd = () -> 0.9; // 0.9 > 0.2 → acepta

    when(store.store(any())).thenAnswer(inv -> inv.getArgument(0, CreditCardToken.class));

    var svc = new TokenizeCardService(store, props, rnd);

    var out = svc.execute(new TokenizeCardUseCase.Command(
        "4111111111111111", "123", 12, 2030, "JOHN DOE"));

    assertThat(out.getId()).isNotNull();
    assertThat(out.getToken()).isNotBlank()
        .matches("^[A-Za-z0-9_-]{24}$"); // 18 bytes → Base64Url 24 chars, sin padding
    verify(store, times(1)).store(any());
  }
}
