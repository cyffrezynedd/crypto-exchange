package com.exchange.clearing.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ClearingProperties {

    private final long platformUserId;
    private final BigDecimal defaultFeeRate;
    private final BigDecimal treasuryInitialBalance;

    public ClearingProperties(
            @Value("${clearing.platform-user-id:0}") long platformUserId,
            @Value("${clearing.default-fee-rate:0.001}") BigDecimal defaultFeeRate,
            @Value("${clearing.treasury-initial-balance:1000000000}") BigDecimal treasuryInitialBalance) {
        this.platformUserId = platformUserId;
        this.defaultFeeRate = defaultFeeRate;
        this.treasuryInitialBalance = treasuryInitialBalance;
    }

    public long platformUserId() {
        return platformUserId;
    }

    public BigDecimal defaultFeeRate() {
        return defaultFeeRate;
    }

    public BigDecimal treasuryInitialBalance() {
        return treasuryInitialBalance;
    }
}
