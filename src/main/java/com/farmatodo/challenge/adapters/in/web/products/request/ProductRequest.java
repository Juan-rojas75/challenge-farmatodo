package com.farmatodo.challenge.adapters.in.web.products.request;

import jakarta.validation.constraints.*;

public record ProductRequest(
    @NotBlank String sku,
    @NotBlank String name,
    @Min(1) Integer stock,
    @Min(1) Double price
) {
}
