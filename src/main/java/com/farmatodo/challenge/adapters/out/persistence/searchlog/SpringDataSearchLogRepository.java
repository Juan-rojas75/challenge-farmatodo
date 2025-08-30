package com.farmatodo.challenge.adapters.out.persistence.searchlog;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataSearchLogRepository extends JpaRepository<SearchLogJpaEntity, UUID> {}