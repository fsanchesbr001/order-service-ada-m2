package com.fabriciosanches.adapter.outbound.persistence.repository;

import com.fabriciosanches.adapter.outbound.persistence.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, String> {
}
