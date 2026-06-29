package com.exchange.marketdata.redis;

import com.exchange.marketdata.config.MarketDataProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class RedisMarketDataStore {

    private final RedisTemplate<String, String> redis;
    private final ObjectMapper objectMapper;
    private final int maxRecentTrades;

    public RedisMarketDataStore(
            RedisTemplate<String, String> redis,
            ObjectMapper objectMapper,
            MarketDataProperties properties
    ) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.maxRecentTrades = properties.getMaxRecentTrades();
    }

    public void seedPairSymbols(Map<Long, String> pairSymbols) {
        if (pairSymbols == null || pairSymbols.isEmpty()) {
            return;
        }
        pairSymbols.forEach((id, symbol) -> {
            redis.opsForHash().put(RedisKeys.PAIR_ID_TO_SYMBOL, id.toString(), symbol);
            redis.opsForSet().add(RedisKeys.PAIRS, symbol);
        });
    }

    public String resolveSymbol(long tradingPairId) {
        Object cached = redis.opsForHash().get(RedisKeys.PAIR_ID_TO_SYMBOL, Long.toString(tradingPairId));
        if (cached != null) {
            return cached.toString();
        }
        String fallback = "PAIR_" + tradingPairId;
        redis.opsForHash().put(RedisKeys.PAIR_ID_TO_SYMBOL, Long.toString(tradingPairId), fallback);
        redis.opsForSet().add(RedisKeys.PAIRS, fallback);
        return fallback;
    }

    public List<String> listPairs() {
        Set<String> pairs = redis.opsForSet().members(RedisKeys.PAIRS);
        if (pairs == null || pairs.isEmpty()) {
            return List.of();
        }
        return pairs.stream().sorted().toList();
    }

    public void addOrder(String symbol, String side, UUID orderId, String price, String quantity) {
        redis.opsForSet().add(RedisKeys.PAIRS, symbol);
        String key = sideKey(symbol, side);
        double score = toScore(price);
        redis.opsForZSet().add(key, member(orderId, quantity), score);
    }

    public void recordTrade(
            String symbol,
            UUID tradeId,
            String price,
            String quantity,
            String executedAt,
            UUID makerOrderId,
            UUID takerOrderId
    ) throws JsonProcessingException {
        redis.opsForSet().add(RedisKeys.PAIRS, symbol);

        Map<String, String> trade = Map.of(
                "tradeId", tradeId.toString(),
                "price", price,
                "quantity", quantity,
                "executedAt", executedAt
        );
        String payload = objectMapper.writeValueAsString(trade);
        redis.opsForList().leftPush(RedisKeys.trades(symbol), payload);
        redis.opsForList().trim(RedisKeys.trades(symbol), 0, maxRecentTrades - 1L);

        applyFill(symbol, makerOrderId, quantity);
        applyFill(symbol, takerOrderId, quantity);
    }

    public List<OrderBookEntry> getBids(String symbol) {
        return readLevels(RedisKeys.bids(symbol), true);
    }

    public List<OrderBookEntry> getAsks(String symbol) {
        return readLevels(RedisKeys.asks(symbol), false);
    }

    public List<String> getRecentTrades(String symbol) {
        List<String> trades = redis.opsForList().range(RedisKeys.trades(symbol), 0, maxRecentTrades - 1L);
        if (trades == null || trades.isEmpty()) {
            return List.of();
        }
        return trades;
    }

    private void applyFill(String symbol, UUID orderId, String fillQuantity) {
        if (orderId == null) {
            return;
        }
        BigDecimal fill = new BigDecimal(fillQuantity);
        updateSide(symbol, RedisKeys.bids(symbol), orderId, fill);
        updateSide(symbol, RedisKeys.asks(symbol), orderId, fill);
    }

    private void updateSide(String symbol, String key, UUID orderId, BigDecimal fillQuantity) {
        Set<ZSetOperations.TypedTuple<String>> entries = redis.opsForZSet().rangeWithScores(key, 0, -1);
        if (entries == null) {
            return;
        }
        for (ZSetOperations.TypedTuple<String> entry : entries) {
            String member = entry.getValue();
            if (member == null || !member.startsWith(orderId + ":")) {
                continue;
            }
            String remainingQty = member.substring(orderId.toString().length() + 1);
            BigDecimal remaining = new BigDecimal(remainingQty).subtract(fillQuantity);
            redis.opsForZSet().remove(key, member);
            if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                redis.opsForZSet().add(key, member(orderId, remaining.toPlainString()), entry.getScore());
            }
            return;
        }
    }

    private List<OrderBookEntry> readLevels(String key, boolean reverse) {
        Set<ZSetOperations.TypedTuple<String>> entries = reverse
                ? redis.opsForZSet().reverseRangeWithScores(key, 0, -1)
                : redis.opsForZSet().rangeWithScores(key, 0, -1);
        if (entries == null || entries.isEmpty()) {
            return List.of();
        }
        List<OrderBookEntry> levels = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> entry : entries) {
            String member = entry.getValue();
            if (member == null || entry.getScore() == null) {
                continue;
            }
            int separator = member.indexOf(':');
            if (separator < 0) {
                continue;
            }
            levels.add(new OrderBookEntry(
                    member.substring(0, separator),
                    formatPrice(entry.getScore()),
                    member.substring(separator + 1)
            ));
        }
        return Collections.unmodifiableList(levels);
    }

    private static String sideKey(String symbol, String side) {
        return "BUY".equalsIgnoreCase(side) ? RedisKeys.bids(symbol) : RedisKeys.asks(symbol);
    }

    private static String member(UUID orderId, String quantity) {
        return orderId + ":" + quantity;
    }

    private static double toScore(String price) {
        return new BigDecimal(price).doubleValue();
    }

    private static String formatPrice(double score) {
        return BigDecimal.valueOf(score).stripTrailingZeros().toPlainString();
    }

    public record OrderBookEntry(String orderId, String price, String quantity) {
    }
}
