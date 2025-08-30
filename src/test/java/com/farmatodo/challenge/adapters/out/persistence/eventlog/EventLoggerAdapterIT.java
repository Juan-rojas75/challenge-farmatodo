package com.farmatodo.challenge.adapters.out.persistence.eventlog;

import com.farmatodo.challenge.application.logs.port.out.EventLoggerPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventLoggerAdapterIT {

  @Autowired SpringDataEventLogRepository repo;

  @Test
  void guarda_evento() {
    EventLoggerPort logger = new EventLoggerAdapter(repo);
    logger.log("TEST", "tx-123", "{\"k\":\"v\"}");
    var all = repo.findAll();
    assertThat(all).hasSize(1);
    assertThat(all.get(0).getType()).isEqualTo("TEST");
  }
}
