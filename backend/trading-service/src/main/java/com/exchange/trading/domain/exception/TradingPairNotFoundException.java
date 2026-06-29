package com.exchange.trading.domain.exception;

public class TradingPairNotFoundException extends RuntimeException {

    public TradingPairNotFoundException(Long id) {
        super("Trading pair not found: " + id);
    }
}
