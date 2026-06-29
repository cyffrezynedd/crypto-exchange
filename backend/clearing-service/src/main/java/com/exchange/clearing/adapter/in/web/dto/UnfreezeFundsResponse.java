package com.exchange.clearing.adapter.in.web.dto;

import com.exchange.clearing.port.in.UnfreezeFundsResult;

import java.math.BigDecimal;

public record UnfreezeFundsResponse(boolean success, BigDecimal availableBalance) {

    public static UnfreezeFundsResponse from(UnfreezeFundsResult result) {
        return new UnfreezeFundsResponse(result.success(), result.availableBalance());
    }
}
