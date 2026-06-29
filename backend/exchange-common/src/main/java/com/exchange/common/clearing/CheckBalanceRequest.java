package com.exchange.common.clearing;

import java.math.BigDecimal;

public record CheckBalanceRequest(Long userId, Long currencyId, BigDecimal requiredAmount) {
}
