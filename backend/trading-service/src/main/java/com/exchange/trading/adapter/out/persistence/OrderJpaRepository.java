package com.exchange.trading.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID>, JpaSpecificationExecutor<OrderJpaEntity> {

    List<OrderJpaEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<OrderJpaEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    boolean existsByUserIdAndClientOrderId(Long userId, String clientOrderId);
}
