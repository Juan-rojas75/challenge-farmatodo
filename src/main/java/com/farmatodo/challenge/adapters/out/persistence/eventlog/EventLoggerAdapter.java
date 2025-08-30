package com.farmatodo.challenge.adapters.out.persistence.eventlog;

import com.farmatodo.challenge.application.logs.port.out.EventLoggerPort;
import org.springframework.stereotype.Component;
import java.time.Instant;
@Component
class EventLoggerAdapter implements EventLoggerPort {
  private final SpringDataEventLogRepository repo;
  EventLoggerAdapter(SpringDataEventLogRepository repo){ this.repo=repo; }
  /**
   * Logs an event using the given parameters.
   * @param type the type of the event
   * @param txId the transaction ID of the event
   * @param payloadJson the payload of the event in JSON format
   */
  @Override public void log(String type, String txId, String payloadJson){
    var e = new EventLogJpaEntity();
    e.setType(type); e.setTxId(txId); e.setPayloadJson(payloadJson); e.setCreatedAt(Instant.now());
    repo.save(e);
  }
}
