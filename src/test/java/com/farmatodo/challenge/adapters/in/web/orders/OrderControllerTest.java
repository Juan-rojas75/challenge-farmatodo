package com.farmatodo.challenge.adapters.in.web.orders;

import com.farmatodo.challenge.application.orders.port.in.PlaceOrderUseCase;
import com.farmatodo.challenge.application.orders.port.in.SearchOrderuseCase;
import com.farmatodo.challenge.domain.orders.model.OrderStatus;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

  private MockMvc mvc;
  private PlaceOrderUseCase place;
  private SearchOrderuseCase search;

  @BeforeEach
  void setup() {
    place = mock(PlaceOrderUseCase.class);
    mvc = MockMvcBuilders.standaloneSetup(new OrderController(place, search)).build();
  }

  @Test
  void create_paid_201() throws Exception {
    var id = UUID.randomUUID();
    when(place.place(any())).thenReturn(new PlaceOrderUseCase.Result(id, OrderStatus.PAID));

    mvc.perform(post("/v1/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"customerId":"00000000-0000-0000-0000-000000000001",
           "cartId":"00000000-0000-0000-0000-000000000002",
           "shippingAddress":"Calle 123","token":"tok"}
        """))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(id.toString()))
      .andExpect(jsonPath("$.status").value("PAID"));
  }

  @Test
  void create_failed_201_con_status_FAILED() throws Exception {
    var id = UUID.randomUUID();
    when(place.place(any())).thenReturn(new PlaceOrderUseCase.Result(id, OrderStatus.FAILED));

    mvc.perform(post("/v1/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"customerId":"00000000-0000-0000-0000-000000000001",
           "cartId":"00000000-0000-0000-0000-000000000002",
           "shippingAddress":"Calle 123","token":"tok","rejectProbability":1.0}
        """))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.status").value("FAILED"));
  }
}
