package com.exchange.marketdata.application;

import com.exchange.marketdata.redis.RedisMarketDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class OrderBookOwnerBackfill {

    private static final Logger log = LoggerFactory.getLogger(OrderBookOwnerBackfill.class);

    private static final String ACTIVE_ORDER_OWNERS_SQL = """
            SELECT o.id::text AS order_id, o.user_id, u.username
            FROM trading.orders o
            JOIN iam.users u ON u.id = o.user_id
            WHERE o.status IN ('NEW', 'PARTIALLY_FILLED')
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RedisMarketDataStore store;

    public OrderBookOwnerBackfill(Optional<JdbcTemplate> jdbcTemplate, RedisMarketDataStore store) {
        this.jdbcTemplate = jdbcTemplate.orElse(null);
        this.store = store;
    }

    @EventListener(ApplicationReadyEvent.class)
    void backfillOwnersAndUsernames() {
        if (jdbcTemplate == null) {
            log.warn("JdbcTemplate is not configured; skipping order book username backfill");
            return;
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(ACTIVE_ORDER_OWNERS_SQL);
        for (Map<String, Object> row : rows) {
            UUID orderId = UUID.fromString(String.valueOf(row.get("order_id")));
            long userId = ((Number) row.get("user_id")).longValue();
            String username = String.valueOf(row.get("username"));
            store.indexOrderOwner(orderId, userId, username);
        }
        store.refreshAllOrderBookUsernames();
        log.info("Order book username index backfilled for {} active orders", rows.size());
    }
}
