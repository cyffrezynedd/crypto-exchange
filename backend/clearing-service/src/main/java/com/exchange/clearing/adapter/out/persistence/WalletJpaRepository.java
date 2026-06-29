package com.exchange.clearing.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface WalletJpaRepository extends JpaRepository<WalletJpaEntity, Long> {

    List<WalletJpaEntity> findAllByUserId(Long userId);

    Optional<WalletJpaEntity> findByUserIdAndCurrencyId(Long userId, Long currencyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM WalletJpaEntity w WHERE w.userId = :userId AND w.currencyId = :currencyId")
    Optional<WalletJpaEntity> findByUserIdAndCurrencyIdForUpdate(
            @Param("userId") Long userId,
            @Param("currencyId") Long currencyId);
}
