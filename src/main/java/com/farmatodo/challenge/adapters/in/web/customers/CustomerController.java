package com.farmatodo.challenge.adapters.in.web.customers;


import com.farmatodo.challenge.application.customers.port.in.RegisterCustomerUseCase;
import com.farmatodo.challenge.adapters.in.web.customers.request.CustomerRequest;
import com.farmatodo.challenge.adapters.in.web.customers.response.CustomerResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

  private final RegisterCustomerUseCase register;

  public CustomerController(RegisterCustomerUseCase register) { this.register = register; }

  @PostMapping
  public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest req) {
    UUID id = register.register(new RegisterCustomerUseCase.Command(
        req.name(), req.email(), req.phone(), req.address()
    ));
    return ResponseEntity.status(201).body(new CustomerResponse(id, req.name(), req.email(), req.phone(), req.address()));
  }
}