package com.farmatodo.challenge.adapters.in.web.tokenization.request;

import jakarta.validation.constraints.*;

public record TokenRequest(
  @NotBlank String cardNumber,
  @NotBlank String cvv,
  @Min(1) @Max(12) int expMonth,
  @Min(2024) int expYear,
  @NotBlank String holder
) {}