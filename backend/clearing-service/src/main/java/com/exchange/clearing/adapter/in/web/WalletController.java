package com.exchange.clearing.adapter.in.web;

import com.exchange.clearing.adapter.in.web.dto.DepositRequest;
import com.exchange.clearing.adapter.in.web.dto.WalletResponse;
import com.exchange.clearing.port.in.DepositCommand;
import com.exchange.clearing.port.in.WalletUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    public static final String USER_ID_HEADER = "X-User-Id";

    private final WalletUseCase walletUseCase;

    public WalletController(WalletUseCase walletUseCase) {
        this.walletUseCase = walletUseCase;
    }

    @GetMapping
    public List<WalletResponse> list(@RequestHeader(USER_ID_HEADER) Long userId) {
        return walletUseCase.listWalletsByUserId(userId).stream()
                .map(WalletResponse::from)
                .toList();
    }

    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse deposit(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody DepositRequest request) {
        DepositCommand command = new DepositCommand(
                request.eventId(),
                userId,
                request.currencyId(),
                request.amount());
        return WalletResponse.from(walletUseCase.deposit(command));
    }
}
