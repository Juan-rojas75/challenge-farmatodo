package com.farmatodo.challenge.adapters.out.persistence.eventlog;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant; import java.util.UUID;

@Data
@Entity @Table(name="event_log")
public class EventLogJpaEntity {
  @Id @GeneratedValue private UUID id;
  @Column(nullable=false) private String type;
  @Column(nullable=false) private String txId;
  @Column(nullable=false) private Instant createdAt;
  @Lob @Column(nullable=false) private String payloadJson;
}
