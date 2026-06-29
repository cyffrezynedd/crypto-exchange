package com.exchange.clearing.adapter.out.persistence;

import com.exchange.clearing.domain.model.Wallet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "wallets")
class WalletJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "currency_id", nullable = false)
    private Long currencyId;

    @Column(name = "available_balance", nullable = false, precision = 36, scale = 18)
    private BigDecimal availableBalance;

    @Column(name = "locked_balance", nullable = false, precision = 36, scale = 18)
    private BigDecimal lockedBalance;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (availableBalance == null) {
            availableBalance = BigDecimal.ZERO;
        }
        if (lockedBalance == null) {
            lockedBalance = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    static WalletJpaEntity fromDomain(Wallet wallet) {
        WalletJpaEntity entity = new WalletJpaEntity();
        entity.id = wallet.getId();
        entity.userId = wallet.getUserId();
        entity.currencyId = wallet.getCurrencyId();
        entity.availableBalance = wallet.getAvailableBalance();
        entity.lockedBalance = wallet.getLockedBalance();
        entity.version = wallet.getVersion();
        entity.createdAt = wallet.getCreatedAt();
        entity.updatedAt = wallet.getUpdatedAt();
        return entity;
    }

    Wallet toDomain() {
        Wallet wallet = new Wallet();
        wallet.setId(id);
        wallet.setUserId(userId);
        wallet.setCurrencyId(currencyId);
        wallet.setAvailableBalance(availableBalance);
        wallet.setLockedBalance(lockedBalance);
        wallet.setVersion(version);
        wallet.setCreatedAt(createdAt);
        wallet.setUpdatedAt(updatedAt);
        return wallet;
    }
}
