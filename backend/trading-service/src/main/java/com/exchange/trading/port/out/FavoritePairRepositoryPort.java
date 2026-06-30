package com.exchange.trading.port.out;

import com.exchange.trading.domain.model.FavoritePair;
import com.exchange.trading.port.in.FavoritePairSearchQuery;

import java.util.Optional;

public interface FavoritePairRepositoryPort {

    com.exchange.common.web.PageResponse<FavoritePair> search(FavoritePairSearchQuery query);

    FavoritePair save(FavoritePair favorite);

    void delete(Long userId, Long tradingPairId);

    boolean exists(Long userId, Long tradingPairId);

    Optional<String> findSymbol(Long tradingPairId);
}
