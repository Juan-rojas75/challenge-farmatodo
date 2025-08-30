package com.farmatodo.challenge.adapters.in.web.products;

import com.farmatodo.challenge.application.products.port.in.RegisterProductUseCase;
import com.farmatodo.challenge.application.products.port.in.SearchProductsUseCase;
import com.farmatodo.challenge.adapters.in.web.products.request.ProductRequest;
import com.farmatodo.challenge.adapters.in.web.products.response.ProductResponse;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/products")
@Tag(name = "Products", description = "API para gestionar productos")
public class ProductController {

  private final SearchProductsUseCase search;
  private final RegisterProductUseCase register;

  public ProductController(SearchProductsUseCase search, RegisterProductUseCase register) { this.search = search; this.register = register; }

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
  
  
  /**
    * Crea un nuevo producto.
    * La respuesta es el id del producto creado y sus datos.
    * La respuesta tiene un status 201 Created.
    */
  @PostMapping
  @Operation(summary = "Crea un nuevo producto")
  public ResponseEntity<ProductResponse> create(
      @Valid @RequestBody ProductRequest req
  ) {
   UUID id = register.register(new RegisterProductUseCase.Command(
        req.sku(), req.name(), req.stock(), req.price()
    ));
    return ResponseEntity.status(201).body(new ProductResponse(
        id.toString(), req.sku(), req.name(), req.stock(), req.price()
    ));
  }
}
