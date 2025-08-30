package com.farmatodo.challenge.application.products.service;

import com.farmatodo.challenge.application.products.port.in.RegisterProductUseCase;
import com.farmatodo.challenge.application.products.port.in.SearchProductsUseCase;
import com.farmatodo.challenge.application.products.port.out.LoadProductsPort;
import com.farmatodo.challenge.application.products.port.out.SaveProductPort;
import com.farmatodo.challenge.application.products.event.ProductSearchedEvent;
import com.farmatodo.challenge.bootstrap.config.ProductsProperties;
import com.farmatodo.challenge.domain.products.model.Product;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class SearchProductsService implements SearchProductsUseCase, RegisterProductUseCase {

  private final LoadProductsPort load;
  private final SaveProductPort savePort;
  private final ProductsProperties props;
  private final ApplicationEventPublisher events;

  public SearchProductsService(LoadProductsPort load, SaveProductPort savePort , ProductsProperties props, ApplicationEventPublisher events) {
    this.load = load; this.savePort = savePort; this.props = props; this.events = events;
  }

  /**
   * Busca productos por nombre y stock m nimo.
   * El par metro minStock es opcional y se utiliza como umbral m nimo de stock,
   * si no se proporciona se utiliza el umbral predeterminado en la configuraci n.
   * Se devuelve una lista de productos encontrados y se publica un evento
   * ProductSearchedEvent con los par metros de entrada.
   */
  @Override
  public List<Product> search(Query query) {
    int threshold = query.minStock() != null ? query.minStock() : props.getMinStockThreshold();
    List<Product> items =
        load.searchByNameAndStock(query.q(), threshold);
    events.publishEvent(new ProductSearchedEvent(query.q(), threshold, query.requestedBy()));
    return items;
  }

  @Override
  public UUID register(Command cmd) {
    Product toSave = new Product(UUID.randomUUID(), cmd.sku(), cmd.name(), cmd.stock(), cmd.price());
    Product saved = savePort.save(toSave);
    return saved.getId();
  }

}
