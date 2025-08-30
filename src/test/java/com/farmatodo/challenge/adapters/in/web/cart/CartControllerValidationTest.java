package com.farmatodo.challenge.adapters.in.web.cart;

import com.farmatodo.challenge.application.cart.port.in.AddToCartUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CartControllerValidationTest {

  private MockMvc mvc;
  private AddToCartUseCase add;
  private LoadSaveCartPortFake loadSave;

  interface LoadSaveCartPortFake extends com.farmatodo.challenge.application.cart.port.out.LoadSaveCartPort {}

  // @BeforeEach
  // void setup() {
  //   add = mock(AddToCartUseCase.class);
  //   loadSave = mock(LoadSaveCartPortFake.class);
  //   mvc = MockMvcBuilders.standaloneSetup(new CartController(add, loadSave)).build();
  // }

  // @Test
  // void qty_invalida_devuelve_400() throws Exception {
  //   var body = """
  //     {"customerId":"29b218fe-7244-4b64-a5fd-bc0c0ac61c14",
  //      "items":[{"sku":"SKU-1","qty":0}]}
  //   """;
  //   mvc.perform(post("/v1/cart").contentType(MediaType.APPLICATION_JSON).content(body))
  //      .andExpect(status().isBadRequest());
  //   verify(add, never()).addOrCreate(any());
  // }

  @BeforeEach
  void setup() {
    add = mock(AddToCartUseCase.class);

    var controller = new CartController(add, loadSave);

    var validator = new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
    validator.afterPropertiesSet();

    mvc = MockMvcBuilders.standaloneSetup(controller)
        .setValidator(validator)
        // .setControllerAdvice(new GlobalExceptionHandler()) // si tienes uno
        .build();
  }
  
  @Test
  void qty_invalida_devuelve_400() throws Exception {
    var body = """
      {"customerId":"29b218fe-7244-4b64-a5fd-bc0c0ac61c14",
      "items":[{"sku":"SKU-1","qty":0}]}
    """;
    mvc.perform(post("/v1/cart").contentType(MediaType.APPLICATION_JSON).content(body))
      .andExpect(status().isBadRequest());

    verify(add, never()).addOrCreate(any()); // no debe invocar el use case
  }

}
