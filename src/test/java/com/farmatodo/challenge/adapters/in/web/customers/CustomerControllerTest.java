package com.farmatodo.challenge.adapters.in.web.customers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.farmatodo.challenge.application.customers.port.in.RegisterCustomerUseCase;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.UUID;
class CustomerControllerTest {

  private MockMvc mvc;
  private RegisterCustomerUseCase register;

  @BeforeEach
  void setup() {
    register = mock(RegisterCustomerUseCase.class);
    var controller = new CustomerController(register);
    mvc = MockMvcBuilders.standaloneSetup(controller)
        // .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void create_ok_returns_201() throws Exception {
    var id = UUID.randomUUID();
    when(register.register(any())).thenReturn(id);

    mvc.perform(post("/v1/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"name":"Andres","email":"a@b.com","phone":"3001112233","address":"Calle 123"}
        """))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(id.toString()));
  }
}

