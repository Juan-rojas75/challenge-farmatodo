package com.farmatodo.challenge.adapters.out.persistence.eventlog;

import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;
public interface SpringDataEventLogRepository extends JpaRepository<EventLogJpaEntity, UUID> {}
