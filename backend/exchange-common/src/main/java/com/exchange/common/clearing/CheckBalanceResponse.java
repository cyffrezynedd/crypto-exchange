package com.exchange.common.clearing;

import java.math.BigDecimal;

public record CheckBalanceResponse(boolean sufficient, BigDecimal availableBalance) {
}
