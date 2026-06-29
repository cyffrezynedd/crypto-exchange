package com.exchange.clearing.adapter.in.web;

import com.exchange.clearing.adapter.in.web.dto.SettleTradeResponse;
import com.exchange.clearing.adapter.in.web.dto.UnfreezeFundsResponse;
import com.exchange.clearing.port.in.ClearingUseCase;
import com.exchange.common.clearing.CheckBalanceRequest;
import com.exchange.common.clearing.CheckBalanceResponse;
import com.exchange.common.clearing.FreezeFundsRequest;
import com.exchange.common.clearing.FreezeFundsResponse;
import com.exchange.common.clearing.SettleTradeRequest;
import com.exchange.common.clearing.UnfreezeFundsRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1")
public class InternalClearingController {

    private final ClearingUseCase clearingUseCase;

    public InternalClearingController(ClearingUseCase clearingUseCase) {
        this.clearingUseCase = clearingUseCase;
    }

    @PostMapping("/balance/check")
    public CheckBalanceResponse checkBalance(@Valid @RequestBody CheckBalanceRequest request) {
        return clearingUseCase.checkBalance(request);
    }

    @PostMapping("/funds/freeze")
    public FreezeFundsResponse freezeFunds(@Valid @RequestBody FreezeFundsRequest request) {
        return clearingUseCase.freezeFunds(request);
    }

    @PostMapping("/funds/unfreeze")
    public UnfreezeFundsResponse unfreezeFunds(@Valid @RequestBody UnfreezeFundsRequest request) {
        return UnfreezeFundsResponse.from(clearingUseCase.unfreezeFunds(request));
    }

    @PostMapping("/trades/settle")
    public SettleTradeResponse settleTrade(@Valid @RequestBody SettleTradeRequest request) {
        return SettleTradeResponse.from(clearingUseCase.settleTrade(request));
    }
}
