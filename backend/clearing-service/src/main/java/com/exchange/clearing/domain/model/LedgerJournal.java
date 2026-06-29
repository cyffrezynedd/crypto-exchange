package com.exchange.clearing.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LedgerJournal {

    private Long id;
    private UUID eventId;
    private LedgerTransactionType transactionType;
    private UUID referenceId;
    private String description;
    private Instant createdAt;
    private List<LedgerEntry> entries = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public LedgerTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(LedgerTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public UUID getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(UUID referenceId) {
        this.referenceId = referenceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<LedgerEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<LedgerEntry> entries) {
        this.entries = entries;
    }

    public void addEntry(LedgerEntry entry) {
        entries.add(entry);
    }
}
