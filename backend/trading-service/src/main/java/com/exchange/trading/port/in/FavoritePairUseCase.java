package com.exchange.trading.port.in;

import com.exchange.common.web.PageResponse;
import com.exchange.trading.domain.model.FavoritePair;

public interface FavoritePairUseCase {

    PageResponse<FavoritePair> search(FavoritePairSearchQuery query);

    FavoritePair add(Long userId, Long tradingPairId);

    void remove(Long userId, Long tradingPairId);
}
