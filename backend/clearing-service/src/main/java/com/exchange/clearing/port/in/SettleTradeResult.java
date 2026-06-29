package com.exchange.clearing.port.in;

import java.util.UUID;

public record SettleTradeResult(UUID tradeId, boolean settled) {
}
