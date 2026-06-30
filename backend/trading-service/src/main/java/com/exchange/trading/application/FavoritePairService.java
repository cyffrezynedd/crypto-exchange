package com.exchange.trading.application;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.ExchangeException;
import com.exchange.common.web.PageResponse;
import com.exchange.trading.domain.exception.TradingPairNotFoundException;
import com.exchange.trading.domain.model.FavoritePair;
import com.exchange.trading.port.in.FavoritePairSearchQuery;
import com.exchange.trading.port.in.FavoritePairUseCase;
import com.exchange.trading.port.out.FavoritePairRepositoryPort;
import com.exchange.trading.port.out.TradingPairRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class FavoritePairService implements FavoritePairUseCase {

    private final FavoritePairRepositoryPort favoritePairRepository;
    private final TradingPairRepositoryPort tradingPairRepository;

    public FavoritePairService(
            FavoritePairRepositoryPort favoritePairRepository,
            TradingPairRepositoryPort tradingPairRepository
    ) {
        this.favoritePairRepository = favoritePairRepository;
        this.tradingPairRepository = tradingPairRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FavoritePair> search(FavoritePairSearchQuery query) {
        int page = Math.max(query.page(), 0);
        int size = Math.min(Math.max(query.size(), 1), 100);
        return favoritePairRepository.search(new FavoritePairSearchQuery(
                query.userId(),
                query.tradingPairId(),
                query.symbol(),
                page,
                size
        ));
    }

    @Override
    @Transactional
    public FavoritePair add(Long userId, Long tradingPairId) {
        tradingPairRepository.findById(tradingPairId)
                .orElseThrow(() -> new TradingPairNotFoundException(tradingPairId));
        if (favoritePairRepository.exists(userId, tradingPairId)) {
            throw new ExchangeException(ErrorCode.ALREADY_EXISTS, "Pair already in favorites");
        }
        FavoritePair favorite = new FavoritePair();
        favorite.setUserId(userId);
        favorite.setTradingPairId(tradingPairId);
        favorite.setAddedAt(Instant.now());
        return favoritePairRepository.save(favorite);
    }

    @Override
    @Transactional
    public void remove(Long userId, Long tradingPairId) {
        if (!favoritePairRepository.exists(userId, tradingPairId)) {
            throw new ExchangeException(ErrorCode.NOT_FOUND, "Favorite pair not found");
        }
        favoritePairRepository.delete(userId, tradingPairId);
    }
}
