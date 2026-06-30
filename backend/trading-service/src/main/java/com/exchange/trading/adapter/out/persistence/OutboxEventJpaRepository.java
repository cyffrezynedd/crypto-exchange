package com.exchange.trading.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {

    @Query("""
            SELECT e FROM OutboxEventJpaEntity e
            WHERE e.processed = false
              AND e.retryCount < :maxRetries
            ORDER BY e.createdAt ASC
            """)
    List<OutboxEventJpaEntity> findUnprocessed(@Param("maxRetries") int maxRetries);
}
