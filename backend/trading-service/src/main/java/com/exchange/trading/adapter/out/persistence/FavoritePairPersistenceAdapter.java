package com.exchange.trading.adapter.out.persistence;

import com.exchange.common.web.PageResponse;
import com.exchange.trading.domain.model.FavoritePair;
import com.exchange.trading.port.in.FavoritePairSearchQuery;
import com.exchange.trading.port.out.FavoritePairRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class FavoritePairPersistenceAdapter implements FavoritePairRepositoryPort {

    private final UserFavoritePairJpaRepository favoriteRepository;
    private final TradingPairJpaRepository tradingPairRepository;

    FavoritePairPersistenceAdapter(
            UserFavoritePairJpaRepository favoriteRepository,
            TradingPairJpaRepository tradingPairRepository
    ) {
        this.favoriteRepository = favoriteRepository;
        this.tradingPairRepository = tradingPairRepository;
    }

    @Override
    public PageResponse<FavoritePair> search(FavoritePairSearchQuery query) {
        String symbolPattern = toSymbolPattern(query.symbol());
        Page<UserFavoritePairJpaEntity> page = favoriteRepository.search(
                query.userId(),
                query.tradingPairId(),
                symbolPattern,
                PageRequest.of(query.page(), query.size())
        );
        return new PageResponse<>(
                page.getContent().stream()
                        .map(entity -> entity.toDomain(resolveSymbol(entity.getId().getTradingPairId())))
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public FavoritePair save(FavoritePair favorite) {
        UserFavoritePairJpaEntity saved = favoriteRepository.save(
                UserFavoritePairJpaEntity.of(
                        favorite.getUserId(),
                        favorite.getTradingPairId(),
                        favorite.getAddedAt()
                )
        );
        return saved.toDomain(resolveSymbol(saved.getId().getTradingPairId()));
    }

    @Override
    public void delete(Long userId, Long tradingPairId) {
        favoriteRepository.deleteById(new UserFavoritePairId(userId, tradingPairId));
    }

    @Override
    public boolean exists(Long userId, Long tradingPairId) {
        return favoriteRepository.existsById(new UserFavoritePairId(userId, tradingPairId));
    }

    @Override
    public Optional<String> findSymbol(Long tradingPairId) {
        return tradingPairRepository.findById(tradingPairId).map(TradingPairJpaEntity::getSymbol);
    }

    private String resolveSymbol(Long tradingPairId) {
        return tradingPairRepository.findById(tradingPairId)
                .map(TradingPairJpaEntity::getSymbol)
                .orElse("?");
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static String toSymbolPattern(String value) {
        String symbol = blankToNull(value);
        if (symbol == null) {
            return null;
        }
        return "%" + symbol.toLowerCase() + "%";
    }
}
