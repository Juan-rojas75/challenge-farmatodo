package com.farmatodo.challenge.adapters.in.web.cart;

import com.farmatodo.challenge.application.cart.port.in.AddToCartUseCase;
import com.farmatodo.challenge.application.cart.port.out.LoadSaveCartPort;
import com.farmatodo.challenge.domain.cart.model.Cart;
import com.farmatodo.challenge.adapters.in.web.cart.request.AddToCartRequest;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import jakarta.validation.Valid;
import java.util.*;

@RestController @RequestMapping("/v1/cart")
public class CartController {
  private final AddToCartUseCase add; private final LoadSaveCartPort port;
  public CartController(AddToCartUseCase add, LoadSaveCartPort port){ this.add=add; this.port=port; }
  @PostMapping public ResponseEntity<Map<String,Object>> add(@Valid @RequestBody AddToCartRequest req){
    var id = add.addOrCreate(new AddToCartUseCase.Command((req.customerId()),
      req.items().stream().map(i -> new AddToCartUseCase.Command.Item(i.sku(), i.qty())).toList()));
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id.toString()));
  }
  @GetMapping("/{id}") public ResponseEntity<Cart> get(@PathVariable String id){
    return port.loadById(UUID.fromString(id)).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }
}