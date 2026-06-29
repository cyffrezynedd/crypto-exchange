package com.exchange.clearing.port.out;

import com.exchange.clearing.domain.model.LedgerJournal;
import com.exchange.clearing.domain.model.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepositoryPort {

    Wallet save(Wallet wallet);

    Optional<Wallet> findById(Long id);

    Optional<Wallet> findByUserIdAndCurrencyId(Long userId, Long currencyId);

    Optional<Wallet> findByUserIdAndCurrencyIdForUpdate(Long userId, Long currencyId);

    java.util.List<Wallet> findAllByUserId(Long userId);
}
