package com.exchange.clearing.adapter.out.persistence;

import com.exchange.clearing.domain.model.LedgerEntryDirection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ledger_entries")
class LedgerEntryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "journal_id", nullable = false)
    private LedgerJournalJpaEntity journal;

    @Column(name = "wallet_id", nullable = false)
    private Long walletId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "ledger_entry_direction")
    private LedgerEntryDirection direction;

    @Column(nullable = false, precision = 36, scale = 18)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    void setJournal(LedgerJournalJpaEntity journal) {
        this.journal = journal;
    }

    void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    void setDirection(LedgerEntryDirection direction) {
        this.direction = direction;
    }

    void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
