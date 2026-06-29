package com.exchange.marketdata.redis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RedisKeysTest {

    @Test
    void buildsOrderBookKeysPerSymbol() {
        assertEquals("market:orderbook:BTC_USDT:bids", RedisKeys.bids("BTC_USDT"));
        assertEquals("market:orderbook:BTC_USDT:asks", RedisKeys.asks("BTC_USDT"));
    }

    @Test
    void buildsTradesKeyPerSymbol() {
        assertEquals("market:trades:ETH_USDT", RedisKeys.trades("ETH_USDT"));
    }
}
