package com.exchange.clearing.adapter.out.persistence;

import com.exchange.clearing.domain.model.LedgerEntryDirection;
import com.exchange.clearing.domain.model.LedgerTransactionType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ledger_journals")
class LedgerJournalJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "transaction_type", nullable = false, columnDefinition = "ledger_transaction_type")
    private LedgerTransactionType transactionType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(length = 255)
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LedgerEntryJpaEntity> entries = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    Long getId() {
        return id;
    }

    UUID getEventId() {
        return eventId;
    }

    void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    void setTransactionType(LedgerTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    void setReferenceId(UUID referenceId) {
        this.referenceId = referenceId;
    }

    void setDescription(String description) {
        this.description = description;
    }

    List<LedgerEntryJpaEntity> getEntries() {
        return entries;
    }

    void addEntry(LedgerEntryJpaEntity entry) {
        entry.setJournal(this);
        entries.add(entry);
    }
}
