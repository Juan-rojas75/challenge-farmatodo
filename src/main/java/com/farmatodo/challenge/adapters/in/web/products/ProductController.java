package com.farmatodo.challenge.adapters.in.web.products;

import com.farmatodo.challenge.application.products.port.in.SearchProductsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/products")
public class ProductController {

  private final SearchProductsUseCase search;

  public ProductController(SearchProductsUseCase search) { this.search = search; }

  @GetMapping("/search")
  public ResponseEntity<Map<String,Object>> search(
      @RequestParam String q,
      @RequestParam(required = false) Integer minStock,
      Authentication auth
  ) {
    String requestedBy = (auth != null ? auth.getName() : "anonymous");
    var items = search.search(new SearchProductsUseCase.Query(q, minStock, requestedBy));
    // respuesta simple
    Map<String,Object> body = Map.of(
        "q", q,
        "minStock", (minStock != null ? minStock : null),
        "items", items
    );
    return ResponseEntity.ok(body);
  }
}
