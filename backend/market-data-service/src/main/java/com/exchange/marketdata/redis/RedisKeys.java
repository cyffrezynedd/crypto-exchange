package com.exchange.marketdata.redis;

public final class RedisKeys {

    public static final String PAIRS = "market:pairs";
    public static final String PAIR_ID_TO_SYMBOL = "market:pair:id-to-symbol";

    private RedisKeys() {
    }

    public static String bids(String symbol) {
        return "market:orderbook:" + symbol + ":bids";
    }

    public static String asks(String symbol) {
        return "market:orderbook:" + symbol + ":asks";
    }

    public static String trades(String symbol) {
        return "market:trades:" + symbol;
    }

    public static final String ORDER_OWNERS = "market:orderbook:order-owners";

    public static final String USERNAMES = "market:orderbook:usernames";
}
