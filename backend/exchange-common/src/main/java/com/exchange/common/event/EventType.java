package com.exchange.common.event;

public final class EventType {

    public static final String ORDER_CREATED = "OrderCreated";
    public static final String ORDER_CANCELLED = "OrderCancelled";
    public static final String TRADE_EXECUTED = "TradeExecuted";
    public static final String DEPOSIT_CONFIRMED = "DepositConfirmed";
    public static final String WITHDRAWAL_REQUESTED = "WithdrawalRequested";

    private EventType() {
    }
}
