package com.farmatodo.challenge.adapters.out.persistence.orders;

import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;

public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, UUID> {}
