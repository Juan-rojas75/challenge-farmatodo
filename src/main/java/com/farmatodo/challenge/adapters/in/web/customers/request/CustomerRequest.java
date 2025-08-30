package com.farmatodo.challenge.adapters.in.web.customers.request;

import jakarta.validation.constraints.*;

public record CustomerRequest(
  @NotBlank String name,
  @Email @NotBlank String email,
  @NotBlank String phone,
  @NotBlank String address
) {}