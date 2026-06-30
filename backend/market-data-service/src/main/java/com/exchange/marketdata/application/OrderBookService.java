package com.exchange.marketdata.application;

import com.exchange.common.web.PageResponse;
import com.exchange.marketdata.adapter.in.web.dto.OrderBookLevel;
import com.exchange.marketdata.redis.RedisMarketDataStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class OrderBookService {

    private final RedisMarketDataStore store;

    public OrderBookService(RedisMarketDataStore store) {
        this.store = store;
    }

    public PageResponse<OrderBookLevel> getLevels(
            String symbol,
            String side,
            String username,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        List<OrderBookLevel> levels = new ArrayList<>();
        store.getAsks(symbol).forEach(entry -> levels.add(toLevel(entry, "SELL")));
        store.getBids(symbol).forEach(entry -> levels.add(toLevel(entry, "BUY")));

        levels.sort(Comparator
                .comparing((OrderBookLevel level) -> "SELL".equals(level.side()) ? 0 : 1)
                .thenComparing(OrderBookLevel::price));

        String sideFilter = side == null ? "" : side.trim().toUpperCase(Locale.ROOT);
        String usernameFilter = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);

        List<OrderBookLevel> filtered = levels.stream()
                .filter(level -> sideFilter.isEmpty() || sideFilter.equals(level.side()))
                .filter(level -> usernameFilter.isEmpty()
                        || level.username().toLowerCase(Locale.ROOT).contains(usernameFilter))
                .toList();

        int totalElements = filtered.size();
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / safeSize);
        int from = Math.min(safePage * safeSize, totalElements);
        int to = Math.min(from + safeSize, totalElements);

        return new PageResponse<>(
                filtered.subList(from, to),
                safePage,
                safeSize,
                totalElements,
                totalPages
        );
    }

    private static OrderBookLevel toLevel(RedisMarketDataStore.OrderBookEntry entry, String side) {
        return new OrderBookLevel(
                entry.orderId(),
                entry.price(),
                entry.quantity(),
                side,
                entry.username() == null || entry.username().isBlank() ? "—" : entry.username()
        );
    }
}
