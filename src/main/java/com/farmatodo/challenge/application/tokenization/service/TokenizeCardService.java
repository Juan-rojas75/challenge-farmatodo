package com.farmatodo.challenge.application.tokenization.service;


import com.farmatodo.challenge.application.tokenization.port.in.TokenizeCardUseCase;
import com.farmatodo.challenge.application.tokenization.port.out.StoreTokenPort;
import com.farmatodo.challenge.bootstrap.config.TokenizationProperties;
import com.farmatodo.challenge.domain.tokenization.model.CreditCardToken;
import com.farmatodo.challenge.shared.exception.TokenizationRejectedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.function.DoubleSupplier;

@Service
@Transactional
public class TokenizeCardService implements TokenizeCardUseCase {

  private final StoreTokenPort store;
  private final TokenizationProperties props;
  private final DoubleSupplier random;

  @Autowired
  public TokenizeCardService(StoreTokenPort store, TokenizationProperties props) {
    this(store, props, () -> new SecureRandom().nextDouble());
  }

  // ctor visible para tests
  TokenizeCardService(StoreTokenPort store, TokenizationProperties props, DoubleSupplier random) {
    this.store = store;
    this.props = props;
    this.random = random;
  }

  /**
   * Ejecuta el caso de uso de tokenizar una tarjeta de crédito.
   *
   * @param cmd comando con los datos de la tarjeta
   * @return el token generado, o lanza una excepción en caso de que la operación sea
   *         rechazada por la política de probabilidad.
   */
  @Override
  public CreditCardToken execute(Command cmd) {
    double r = random.getAsDouble();

    if (r < props.getRejectProbability()) {
      throw new TokenizationRejectedException("Operación rechazada por política de probabilidad");
    }

    String token = generateToken();
    CreditCardToken t = new CreditCardToken(UUID.randomUUID(), token, Instant.now());
    return store.store(t);
  }

  /**
   * Genera un token aleatorio de 18 bytes, que se codifica en una cadena
   * utilizando Base64 sin padding. El token se genera con una fuente de
   * aleatoriedad criptográficamente segura.
   *
   * @return el token generado
   */
  private String generateToken() {
    byte[] b = new byte[18];
    new SecureRandom().nextBytes(b);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
  }
}
