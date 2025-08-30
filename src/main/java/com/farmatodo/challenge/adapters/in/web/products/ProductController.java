package com.farmatodo.challenge.adapters.in.web.products;

import com.farmatodo.challenge.application.products.port.in.SearchProductsUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/products")
@Tag(name = "Products", description = "API para gestionar productos")
public class ProductController {

  private final SearchProductsUseCase search;

  public ProductController(SearchProductsUseCase search) { this.search = search; }

   /**
    * Busca productos por nombre y stock m nimo.
    * El par metro q es obligatorio y se busca por nombre de producto.
    * El par metro minStock es opcional (por defecto es 0) y se busca por stock m nimo.
    * El par metro de autenticaci n (Authentication) es opcional y se utiliza para
    * guardar el usuario que realiz la b squeda.
    * La respuesta es un Map que contiene los par metros de entrada y una lista de
    * productos encontrados.
    */
   @Operation(summary = "Busca productos por nombre y stock mínimo")
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
