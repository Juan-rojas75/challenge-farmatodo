package com.farmatodo.challenge.adapters.in.web.products;

import com.farmatodo.challenge.application.products.port.in.RegisterProductUseCase;
import com.farmatodo.challenge.application.products.port.in.SearchProductsUseCase;
import com.farmatodo.challenge.domain.products.model.Product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest {

  private MockMvc mvc;
  private SearchProductsUseCase search;
  private RegisterProductUseCase register;

  @BeforeEach
  void setup() {
    search = mock(SearchProductsUseCase.class);
    var controller = new ProductController(search, register);
    mvc = MockMvcBuilders.standaloneSetup(controller) // sin contexto
        // .setControllerAdvice(new GlobalExceptionHandler()) // si tienes
        .build();
  }

  @Test
  void search_ok() throws Exception {
    var p = new Product(UUID.randomUUID(),"SKU-001","Vitamina C",10,12500);
    when(search.search(any())).thenReturn(List.of(p));

    mvc.perform(get("/v1/products/search").param("q","vit").param("minStock","3"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.items[0].sku").value("SKU-001"));
  }
}

