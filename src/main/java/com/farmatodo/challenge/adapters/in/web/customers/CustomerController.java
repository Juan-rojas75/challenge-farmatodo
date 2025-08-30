package com.farmatodo.challenge.adapters.in.web.customers;


import com.farmatodo.challenge.application.customers.port.in.RegisterCustomerUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.farmatodo.challenge.adapters.in.web.customers.request.CustomerRequest;
import com.farmatodo.challenge.adapters.in.web.customers.response.CustomerResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/customers")
@Tag(name = "Customers", description = "API para crear clientes")
public class CustomerController {

  private final RegisterCustomerUseCase register;

  public CustomerController(RegisterCustomerUseCase register) { this.register = register; }

  /**
   * Crea un nuevo cliente
   *
   * <p>En caso de que el email o el n mero de telfono ya est n en la base de datos,
   * se lanza una excepci n con el mensaje "Email/phone ya existe".
   *
   * @param req el objeto de request con los datos del nuevo cliente
   * @return el objeto de respuesta con el id del nuevo cliente
   */
  @Operation(summary = "Crea un nuevo cliente")
  @PostMapping
  public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest req) {
    UUID id = register.register(new RegisterCustomerUseCase.Command(
        req.name(), req.email(), req.phone(), req.address()
    ));
    return ResponseEntity.status(201).body(new CustomerResponse(id, req.name(), req.email(), req.phone(), req.address()));
  }
}