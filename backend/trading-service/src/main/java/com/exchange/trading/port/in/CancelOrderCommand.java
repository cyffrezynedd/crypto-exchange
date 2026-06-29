package com.exchange.trading.port.in;

import java.util.UUID;

public record CancelOrderCommand(Long userId, UUID orderId) {
}
