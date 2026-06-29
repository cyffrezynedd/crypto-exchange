package com.exchange.clearing.domain.exception;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.ExchangeException;

import java.math.BigDecimal;

public class InsufficientFundsException extends ExchangeException {

    public InsufficientFundsException(Long userId, Long currencyId, BigDecimal required, BigDecimal available) {
        super(ErrorCode.INSUFFICIENT_FUNDS,
                "Insufficient funds for userId=%d currencyId=%d: required=%s available=%s"
                        .formatted(userId, currencyId, required, available));
    }
}
