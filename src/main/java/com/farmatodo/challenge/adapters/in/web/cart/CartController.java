package com.farmatodo.challenge.adapters.in.web.cart;

import com.farmatodo.challenge.application.cart.port.in.AddToCartUseCase;
import com.farmatodo.challenge.application.cart.port.out.LoadSaveCartPort;
import com.farmatodo.challenge.domain.cart.model.Cart;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.farmatodo.challenge.adapters.in.web.cart.request.AddToCartRequest;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import jakarta.validation.Valid;
import java.util.*;

@RestController @RequestMapping("/v1/cart")
@Tag(name = "Carts", description = "API para gestionar el carrito de compras")
public class CartController {
  private final AddToCartUseCase add; private final LoadSaveCartPort port;
  public CartController(AddToCartUseCase add, LoadSaveCartPort port){ this.add=add; this.port=port; }

  
   /**
     * Crea un nuevo carrito o agrega items a uno existente.<br>
     * <ul>
     *   <li>Si el carrito no existe, se crea uno nuevo.</li>
     *   <li>Si el carrito existe, se agregan los items al carrito existente.</li>
     *   <li>El carrito se identifica con el customerId.</li>
     *   <li>Devuelve el id del carrito creado o actualizado.</li>
     * </ul>
     */
   @Operation(summary = "Crea un nuevo carrito o agrega items a uno existente")
  @PostMapping public ResponseEntity<Map<String,Object>> add(@Valid @RequestBody AddToCartRequest req){
    var id = add.addOrCreate(new AddToCartUseCase.Command((req.customerId()),
      req.items().stream().map(i -> new AddToCartUseCase.Command.Item(i.sku(), i.qty())).toList()));
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id.toString()));
  }

   /**
     * Obtiene el carrito por su ID.<br>
     * <ul>
     *   <li>Devuelve el carrito si existe.</li>
     *   <li>Devuelve un status 404 si el carrito no existe.</li>
     * </ul>
     */
   @Operation(summary = "Obtiene el carrito por su ID")
  @GetMapping("/{id}") public ResponseEntity<Cart> get(@PathVariable String id){
    return port.loadById(UUID.fromString(id)).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }
}