package com.farmatodo.challenge.application.products.service;

import com.farmatodo.challenge.application.products.event.ProductSearchedEvent;
import com.farmatodo.challenge.application.products.port.in.SearchProductsUseCase;
import com.farmatodo.challenge.application.products.port.out.LoadProductsPort;
import com.farmatodo.challenge.bootstrap.config.ProductsProperties;
import com.farmatodo.challenge.domain.products.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SearchProductsServiceTest {

  private LoadProductsPort load;
  private ProductsProperties props;
  private ApplicationEventPublisher events;
  private SearchProductsService svc;

  @BeforeEach
  void setUp() {
    load = mock(LoadProductsPort.class);
    props = new ProductsProperties();
    props.setMinStockThreshold(3);
    events = mock(ApplicationEventPublisher.class);
    svc = new SearchProductsService(load, props, events);
  }

  @Test
  void usa_umbral_por_defecto_y_publica_evento() {
    var q = new SearchProductsUseCase.Query("vit", null, "tester");
    var items = List.of(new Product(UUID.randomUUID(),"SKU","Vitamina",10,10000));
    when(load.searchByNameAndStock("vit", 3)).thenReturn(items);

    var res = svc.search(q);

    assertThat(res).hasSize(1).extracting(Product::getSku).containsExactly("SKU");
    verify(load).searchByNameAndStock("vit", 3);

    var cap = ArgumentCaptor.forClass(ProductSearchedEvent.class);
    verify(events).publishEvent(cap.capture());
    var ev = (ProductSearchedEvent) cap.getValue();
    assertThat(ev.q()).isEqualTo("vit");
    assertThat(ev.minStock()).isEqualTo(3);
    assertThat(ev.requestedBy()).isEqualTo("tester");
  }

  @Test
  void usa_minStock_del_query_si_viene() {
    var q = new SearchProductsUseCase.Query("ibup", 7, "tester");
    when(load.searchByNameAndStock("ibup", 7)).thenReturn(List.of());

    var res = svc.search(q);

    assertThat(res).isEmpty();
    verify(load).searchByNameAndStock("ibup", 7);
  }
}
