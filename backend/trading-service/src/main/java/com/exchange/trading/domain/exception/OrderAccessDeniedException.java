package com.exchange.trading.domain.exception;

public class OrderAccessDeniedException extends RuntimeException {

    public OrderAccessDeniedException() {
        super("Order does not belong to the requesting user");
    }
}
