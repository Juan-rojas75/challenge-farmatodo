package com.farmatodo.challenge.application.products.service;

import com.farmatodo.challenge.application.products.port.in.SearchProductsUseCase;
import com.farmatodo.challenge.application.products.port.out.LoadProductsPort;
import com.farmatodo.challenge.application.products.event.ProductSearchedEvent;
import com.farmatodo.challenge.bootstrap.config.ProductsProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchProductsService implements SearchProductsUseCase {

  private final LoadProductsPort load;
  private final ProductsProperties props;
  private final ApplicationEventPublisher events;

  public SearchProductsService(LoadProductsPort load, ProductsProperties props, ApplicationEventPublisher events) {
    this.load = load; this.props = props; this.events = events;
  }

  @Override
  public List<com.farmatodo.challenge.domain.products.model.Product> search(Query query) {
    int threshold = query.minStock() != null ? query.minStock() : props.getMinStockThreshold();
    List<com.farmatodo.challenge.domain.products.model.Product> items =
        load.searchByNameAndStock(query.q(), threshold);
    events.publishEvent(new ProductSearchedEvent(query.q(), threshold, query.requestedBy()));
    return items;
  }
}
