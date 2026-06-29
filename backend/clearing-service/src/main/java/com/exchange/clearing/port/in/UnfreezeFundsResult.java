package com.exchange.clearing.port.in;

import java.math.BigDecimal;

public record UnfreezeFundsResult(boolean success, BigDecimal availableBalance) {
}
