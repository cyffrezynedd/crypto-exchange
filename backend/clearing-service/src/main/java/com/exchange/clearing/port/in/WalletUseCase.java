package com.exchange.clearing.port.in;

import com.exchange.clearing.domain.model.Wallet;

import java.util.List;
import java.util.UUID;

public interface WalletUseCase {

    List<Wallet> listWalletsByUserId(Long userId);

    Wallet deposit(DepositCommand command);
}
