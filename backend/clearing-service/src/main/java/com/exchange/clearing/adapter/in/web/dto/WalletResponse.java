package com.exchange.clearing.adapter.in.web.dto;

import com.exchange.clearing.domain.model.Wallet;

import java.math.BigDecimal;
import java.time.Instant;

public record WalletResponse(
        Long id,
        Long userId,
        Long currencyId,
        BigDecimal availableBalance,
        BigDecimal lockedBalance,
        Instant createdAt,
        Instant updatedAt
) {
    public static WalletResponse from(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getCurrencyId(),
                wallet.getAvailableBalance(),
                wallet.getLockedBalance(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt());
    }
}
