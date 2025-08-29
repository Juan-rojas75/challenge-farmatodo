package com.farmatodo.challenge.adapters.out.persistence.tokenization;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataTokenRepository extends JpaRepository<TokenJpaEntity, UUID> {}