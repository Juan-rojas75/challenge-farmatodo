package com.farmatodo.challenge.adapters.in.web.cart.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;

public record AddToCartRequest(
  @NotNull UUID customerId,
  @NotEmpty List<@Valid Item> items
){
  public record Item(
      @NotBlank String sku,
      @Min(1) int qty
  ){}
}