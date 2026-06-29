package com.exchange.trading.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
class OutboxEventJpaEntity {

    @Id
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 32)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 64)
    private String aggregateId;

    @Column(name = "event_type", nullable = false, length = 64)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(nullable = false)
    private boolean processed;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    UUID getId() {
        return id;
    }

    void setId(UUID id) {
        this.id = id;
    }

    String getAggregateType() {
        return aggregateType;
    }

    void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    String getAggregateId() {
        return aggregateId;
    }

    void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    String getEventType() {
        return eventType;
    }

    void setEventType(String eventType) {
        this.eventType = eventType;
    }

    String getPayload() {
        return payload;
    }

    void setPayload(String payload) {
        this.payload = payload;
    }

    boolean isProcessed() {
        return processed;
    }

    void setProcessed(boolean processed) {
        this.processed = processed;
    }

    Instant getProcessedAt() {
        return processedAt;
    }

    void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    int getRetryCount() {
        return retryCount;
    }

    void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    String getLastError() {
        return lastError;
    }

    void setLastError(String lastError) {
        this.lastError = lastError;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
