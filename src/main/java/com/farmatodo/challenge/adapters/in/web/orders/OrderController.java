package com.farmatodo.challenge.adapters.in.web.orders;

import com.farmatodo.challenge.application.orders.port.in.PlaceOrderUseCase;
import com.farmatodo.challenge.application.orders.port.in.SearchOrderuseCase;
import com.farmatodo.challenge.domain.orders.model.Order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.farmatodo.challenge.adapters.in.web.orders.request.CreateOrderRequest;
import com.farmatodo.challenge.adapters.in.web.orders.response.OrderResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
@Tag(name = "Orders", description = "API para gestionar pedidos")
public class OrderController {
  private final PlaceOrderUseCase place;
  private final SearchOrderuseCase searchOrder;
  public OrderController(PlaceOrderUseCase place, SearchOrderuseCase search){ this.place = place; this.searchOrder = search;}

   /**
     * Crea un nuevo pedido con los items del carrito, para el cliente y
     * con la direccion de envio especificados, y devuelve el resultado
     * de la operaci n.
     *
     * @param r contenedor con los datos del pedido a crear
     * @return respuesta con el resultado de la operaci n, que incluye
     * el id del pedido y su estado
     */
  @Operation(summary = "Crea un nuevo pedido")
  @PostMapping
  public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest r){
    var res = place.place(new PlaceOrderUseCase.Command(
        UUID.fromString(r.customerId()), UUID.fromString(r.cartId()),
        r.shippingAddress(), r.token(), r.retryCount(), r.rejectProbability()
    ));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new OrderResponse(res.id().toString(), res.status().name()));
  }
  
  
  @Operation(summary = "Obtiene un pedido")
  @GetMapping("/{id}")
  public ResponseEntity<Order> search(
      @PathVariable String id
      ){

    return searchOrder.search(UUID.fromString(id)).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }
}