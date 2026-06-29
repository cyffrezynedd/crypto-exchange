package com.exchange.clearing.domain.exception;

import com.exchange.common.error.ErrorCode;
import com.exchange.common.error.ExchangeException;

public class WalletNotFoundException extends ExchangeException {

    public WalletNotFoundException(Long userId, Long currencyId) {
        super(ErrorCode.WALLET_NOT_FOUND,
                "Wallet not found for userId=%d currencyId=%d".formatted(userId, currencyId));
    }

    public WalletNotFoundException(Long walletId) {
        super(ErrorCode.WALLET_NOT_FOUND, "Wallet not found: id=%d".formatted(walletId));
    }
}
