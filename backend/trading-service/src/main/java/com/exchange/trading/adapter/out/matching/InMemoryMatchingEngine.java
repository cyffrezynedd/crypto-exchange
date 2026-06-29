package com.exchange.trading.adapter.out.matching;

import com.exchange.trading.domain.model.MatchResult;
import com.exchange.trading.domain.model.Order;
import com.exchange.trading.domain.model.OrderSide;
import com.exchange.trading.domain.model.OrderType;
import com.exchange.trading.port.out.MatchingEnginePort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryMatchingEngine implements MatchingEnginePort {

    private final ConcurrentHashMap<Long, OrderBook> books = new ConcurrentHashMap<>();

    @Override
    public List<MatchResult> submitOrder(Order order) {
        OrderBook book = books.computeIfAbsent(order.getTradingPairId(), id -> new OrderBook());
        synchronized (book) {
            List<MatchResult> matches = order.getSide() == OrderSide.BUY
                    ? matchBuy(order, book)
                    : matchSell(order, book);

            if (shouldRest(order)) {
                book.add(order);
            }
            return matches;
        }
    }

    @Override
    public void removeOrder(UUID orderId, Long tradingPairId) {
        OrderBook book = books.get(tradingPairId);
        if (book != null) {
            synchronized (book) {
                book.remove(orderId);
            }
        }
    }

    @Override
    public Optional<BigDecimal> getBestAskPrice(Long tradingPairId) {
        OrderBook book = books.get(tradingPairId);
        if (book == null) {
            return Optional.empty();
        }
        synchronized (book) {
            return book.bestAskPrice();
        }
    }

    private List<MatchResult> matchBuy(Order taker, OrderBook book) {
        List<MatchResult> matches = new ArrayList<>();
        while (taker.remainingQuantity().compareTo(BigDecimal.ZERO) > 0) {
            Order maker = book.peekBestAsk();
            if (maker == null) {
                break;
            }
            if (taker.getType() == OrderType.LIMIT && maker.getPrice().compareTo(taker.getPrice()) > 0) {
                break;
            }
            MatchResult match = executeMatch(taker, maker, maker.getPrice(), book);
            matches.add(match);
            if (!maker.isActive()) {
                book.remove(maker.getId());
            }
        }
        return matches;
    }

    private List<MatchResult> matchSell(Order taker, OrderBook book) {
        List<MatchResult> matches = new ArrayList<>();
        while (taker.remainingQuantity().compareTo(BigDecimal.ZERO) > 0) {
            Order maker = book.peekBestBid();
            if (maker == null) {
                break;
            }
            if (taker.getType() == OrderType.LIMIT && maker.getPrice().compareTo(taker.getPrice()) < 0) {
                break;
            }
            MatchResult match = executeMatch(taker, maker, maker.getPrice(), book);
            matches.add(match);
            if (!maker.isActive()) {
                book.remove(maker.getId());
            }
        }
        return matches;
    }

    private MatchResult executeMatch(Order taker, Order maker, BigDecimal price, OrderBook book) {
        BigDecimal quantity = taker.remainingQuantity().min(maker.remainingQuantity());
        taker.applyFill(quantity);
        maker.applyFill(quantity);
        book.update(maker);
        return new MatchResult(taker, maker, price, quantity);
    }

    private boolean shouldRest(Order order) {
        return order.getType() == OrderType.LIMIT && order.isActive();
    }

    static final class OrderBook {

        private final PriorityQueue<Order> bids = new PriorityQueue<>(
                Comparator.comparing(Order::getPrice).reversed()
                        .thenComparing(Order::getCreatedAt)
        );
        private final PriorityQueue<Order> asks = new PriorityQueue<>(
                Comparator.comparing(Order::getPrice)
                        .thenComparing(Order::getCreatedAt)
        );

        void add(Order order) {
            if (order.getSide() == OrderSide.BUY) {
                bids.add(order);
            } else {
                asks.add(order);
            }
        }

        void remove(UUID orderId) {
            bids.removeIf(order -> order.getId().equals(orderId));
            asks.removeIf(order -> order.getId().equals(orderId));
        }

        void update(Order order) {
            remove(order.getId());
            if (order.isActive()) {
                add(order);
            }
        }

        Order peekBestBid() {
            cleanup(bids);
            return bids.peek();
        }

        Order peekBestAsk() {
            cleanup(asks);
            return asks.peek();
        }

        Optional<BigDecimal> bestAskPrice() {
            Order ask = peekBestAsk();
            return ask == null ? Optional.empty() : Optional.of(ask.getPrice());
        }

        private void cleanup(PriorityQueue<Order> queue) {
            while (!queue.isEmpty() && !queue.peek().isActive()) {
                queue.poll();
            }
        }
    }
}
