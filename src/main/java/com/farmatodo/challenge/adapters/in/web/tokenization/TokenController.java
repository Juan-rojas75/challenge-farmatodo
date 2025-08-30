package com.farmatodo.challenge.adapters.in.web.tokenization;

import com.farmatodo.challenge.application.tokenization.port.in.TokenizeCardUseCase;
import com.farmatodo.challenge.adapters.in.web.tokenization.request.TokenRequest;
import com.farmatodo.challenge.adapters.in.web.tokenization.response.TokenResponse;
import com.farmatodo.challenge.domain.tokenization.model.CreditCardToken;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/tokenize")
@Tag(name = "Tokenize", description = "API para tokenizar tarjetas de crédito")
public class TokenController {

  private final TokenizeCardUseCase tokenize;

  public TokenController(TokenizeCardUseCase tokenize) { this.tokenize = tokenize; }

  /**
   * Tokeniza una tarjeta de crédito.
   *
   * @param req petición con los datos de la tarjeta
   * @return respuesta con el token y la fecha de creación
   */
  @Operation(summary = "Tokeniza una tarjeta de crédito")
  @PostMapping
  public ResponseEntity<TokenResponse> create(@Valid @RequestBody TokenRequest req) {
    CreditCardToken t = tokenize.execute(new TokenizeCardUseCase.Command(
        req.cardNumber(), req.cvv(), req.expMonth(), req.expYear(), req.holder()
    ));
    return ResponseEntity.status(201).body(new TokenResponse(t.getId(), t.getToken(), t.getCreatedAt()));
  }
}