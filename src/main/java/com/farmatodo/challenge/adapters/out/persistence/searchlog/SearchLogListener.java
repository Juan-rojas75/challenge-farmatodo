package com.farmatodo.challenge.adapters.out.persistence.searchlog;

import com.farmatodo.challenge.application.products.event.ProductSearchedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
class SearchLogListener {

  private final SpringDataSearchLogRepository repo;
  SearchLogListener(SpringDataSearchLogRepository repo) { this.repo = repo; }

  /**
   * Listens for ProductSearchedEvent and records the search log to database asynchronously.
   * @param e event
   */
  @Async
  @EventListener
  public void on(ProductSearchedEvent e) {
    SearchLogJpaEntity log = new SearchLogJpaEntity();
    log.setQ(e.q());
    log.setMinStock(e.minStock());
    log.setRequestedBy(e.requestedBy());
    log.setCreatedAt(Instant.now());
    repo.save(log);
  }
}