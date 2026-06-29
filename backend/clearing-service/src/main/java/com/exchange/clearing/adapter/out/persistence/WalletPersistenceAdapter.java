package com.exchange.clearing.adapter.out.persistence;

import com.exchange.clearing.domain.model.Wallet;
import com.exchange.clearing.port.out.WalletRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class WalletPersistenceAdapter implements WalletRepositoryPort {

    private final WalletJpaRepository repository;

    public WalletPersistenceAdapter(WalletJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Wallet save(Wallet wallet) {
        WalletJpaEntity entity = WalletJpaEntity.fromDomain(wallet);
        WalletJpaEntity saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        return repository.findById(id).map(WalletJpaEntity::toDomain);
    }

    @Override
    public Optional<Wallet> findByUserIdAndCurrencyId(Long userId, Long currencyId) {
        return repository.findByUserIdAndCurrencyId(userId, currencyId).map(WalletJpaEntity::toDomain);
    }

    @Override
    public Optional<Wallet> findByUserIdAndCurrencyIdForUpdate(Long userId, Long currencyId) {
        return repository.findByUserIdAndCurrencyIdForUpdate(userId, currencyId).map(WalletJpaEntity::toDomain);
    }

    @Override
    public List<Wallet> findAllByUserId(Long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(WalletJpaEntity::toDomain)
                .toList();
    }
}
