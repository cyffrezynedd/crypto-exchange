package com.exchange.trading.domain.exception;

public class DuplicateClientOrderException extends RuntimeException {

    public DuplicateClientOrderException(String clientOrderId) {
        super("Client order id already exists: " + clientOrderId);
    }
}
