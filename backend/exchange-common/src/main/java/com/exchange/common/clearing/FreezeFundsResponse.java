package com.exchange.common.clearing;

import java.math.BigDecimal;

public record FreezeFundsResponse(boolean success, Long walletId, BigDecimal lockedBalance) {
}
