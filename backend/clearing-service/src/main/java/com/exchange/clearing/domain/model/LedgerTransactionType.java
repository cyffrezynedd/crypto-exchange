package com.exchange.clearing.domain.model;

public enum LedgerTransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRADE_SETTLEMENT,
    TRADE_FEE,
    ORDER_LOCK,
    ORDER_UNLOCK
}
