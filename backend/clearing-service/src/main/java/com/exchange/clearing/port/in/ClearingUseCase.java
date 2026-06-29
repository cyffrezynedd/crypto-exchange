package com.exchange.clearing.port.in;

import com.exchange.common.clearing.CheckBalanceRequest;
import com.exchange.common.clearing.CheckBalanceResponse;
import com.exchange.common.clearing.FreezeFundsRequest;
import com.exchange.common.clearing.FreezeFundsResponse;
import com.exchange.common.clearing.SettleTradeRequest;
import com.exchange.common.clearing.UnfreezeFundsRequest;

public interface ClearingUseCase {

    CheckBalanceResponse checkBalance(CheckBalanceRequest request);

    FreezeFundsResponse freezeFunds(FreezeFundsRequest request);

    UnfreezeFundsResult unfreezeFunds(UnfreezeFundsRequest request);

    SettleTradeResult settleTrade(SettleTradeRequest request);
}
