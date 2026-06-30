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

    public void addOrder(String symbol, String side, UUID orderId, String price, String quantity, String username) {
        redis.opsForSet().add(RedisKeys.PAIRS, symbol);
        String key = sideKey(symbol, side);
        double score = toScore(price);
        String resolvedUsername = resolveUsername(orderId.toString(), username == null ? "" : username);
        redis.opsForZSet().add(key, member(orderId, quantity, resolvedUsername), score);
    }

    public void indexOrderOwner(UUID orderId, Long userId, String username) {
        if (orderId == null || userId == null) {
            return;
        }
        redis.opsForHash().put(RedisKeys.ORDER_OWNERS, orderId.toString(), userId.toString());
        if (username != null && !username.isBlank()) {
            redis.opsForHash().put(RedisKeys.USERNAMES, userId.toString(), username);
        }
    }

    public void refreshOrderBookUsernames(String symbol) {
        refreshSideUsernames(RedisKeys.bids(symbol));
        refreshSideUsernames(RedisKeys.asks(symbol));
    }

    public void refreshAllOrderBookUsernames() {
        for (String symbol : listPairs()) {
            refreshOrderBookUsernames(symbol);
        }
    }

    public void removeOrder(String symbol, UUID orderId) {
        removeFromSide(RedisKeys.bids(symbol), orderId);
        removeFromSide(RedisKeys.asks(symbol), orderId);
        redis.opsForHash().delete(RedisKeys.ORDER_OWNERS, orderId.toString());
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

    private void removeFromSide(String key, UUID orderId) {
        Set<ZSetOperations.TypedTuple<String>> entries = redis.opsForZSet().rangeWithScores(key, 0, -1);
        if (entries == null) {
            return;
        }
        for (ZSetOperations.TypedTuple<String> entry : entries) {
            String member = entry.getValue();
            if (member == null) {
                continue;
            }
            ParsedMember parsed = parseMember(member);
            if (parsed != null && orderId.toString().equals(parsed.orderId())) {
                redis.opsForZSet().remove(key, member);
                return;
            }
        }
    }

    private void updateSide(String symbol, String key, UUID orderId, BigDecimal fillQuantity) {
        Set<ZSetOperations.TypedTuple<String>> entries = redis.opsForZSet().rangeWithScores(key, 0, -1);
        if (entries == null) {
            return;
        }
        for (ZSetOperations.TypedTuple<String> entry : entries) {
            String member = entry.getValue();
            if (member == null) {
                continue;
            }
            ParsedMember parsed = parseMember(member);
            if (parsed == null || !orderId.toString().equals(parsed.orderId())) {
                continue;
            }
            BigDecimal remaining = new BigDecimal(parsed.quantity()).subtract(fillQuantity);
            redis.opsForZSet().remove(key, member);
            if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                String username = resolveUsername(parsed.orderId(), parsed.username());
                redis.opsForZSet().add(
                        key,
                        member(UUID.fromString(parsed.orderId()), remaining.toPlainString(), username),
                        entry.getScore()
                );
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
            ParsedMember parsed = parseMember(member);
            if (parsed == null) {
                continue;
            }
            levels.add(new OrderBookEntry(
                    parsed.orderId(),
                    formatPrice(entry.getScore()),
                    parsed.quantity(),
                    resolveUsername(parsed.orderId(), parsed.username())
            ));
        }
        return Collections.unmodifiableList(levels);
    }

    private void refreshSideUsernames(String key) {
        Set<ZSetOperations.TypedTuple<String>> entries = redis.opsForZSet().rangeWithScores(key, 0, -1);
        if (entries == null || entries.isEmpty()) {
            return;
        }
        for (ZSetOperations.TypedTuple<String> entry : entries) {
            String rawMember = entry.getValue();
            if (rawMember == null || entry.getScore() == null) {
                continue;
            }
            ParsedMember parsed = parseMember(rawMember);
            if (parsed == null) {
                continue;
            }
            String resolved = resolveUsername(parsed.orderId(), parsed.username());
            if (resolved.equals(parsed.username())) {
                continue;
            }
            redis.opsForZSet().remove(key, rawMember);
            redis.opsForZSet().add(
                    key,
                    member(UUID.fromString(parsed.orderId()), parsed.quantity(), resolved),
                    entry.getScore()
            );
        }
    }

    private String resolveUsername(String orderId, String storedUsername) {
        if (storedUsername != null && !storedUsername.isBlank()) {
            return storedUsername;
        }
        Object owner = redis.opsForHash().get(RedisKeys.ORDER_OWNERS, orderId);
        if (owner == null) {
            return "";
        }
        Object username = redis.opsForHash().get(RedisKeys.USERNAMES, owner.toString());
        return username == null ? "" : username.toString();
    }

    private static String sideKey(String symbol, String side) {
        return "BUY".equalsIgnoreCase(side) ? RedisKeys.bids(symbol) : RedisKeys.asks(symbol);
    }

    private static String member(UUID orderId, String quantity, String username) {
        String safeUsername = username == null ? "" : username;
        return orderId + "|" + quantity + "|" + safeUsername;
    }

    private static ParsedMember parseMember(String member) {
        int first = member.indexOf('|');
        if (first > 0) {
            int second = member.indexOf('|', first + 1);
            if (second > first) {
                return new ParsedMember(
                        member.substring(0, first),
                        member.substring(first + 1, second),
                        member.substring(second + 1)
                );
            }
        }
        int separator = member.indexOf(':');
        if (separator > 0) {
            return new ParsedMember(
                    member.substring(0, separator),
                    member.substring(separator + 1),
                    ""
            );
        }
        return null;
    }

    private record ParsedMember(String orderId, String quantity, String username) {
    }

    private static double toScore(String price) {
        return new BigDecimal(price).doubleValue();
    }

    private static String formatPrice(double score) {
        return BigDecimal.valueOf(score).stripTrailingZeros().toPlainString();
    }

    public record OrderBookEntry(String orderId, String price, String quantity, String username) {
    }
}
