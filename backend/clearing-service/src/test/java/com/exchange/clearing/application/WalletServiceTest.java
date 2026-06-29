package com.exchange.clearing.application;

import com.exchange.clearing.domain.model.Wallet;
import com.exchange.clearing.port.in.DepositCommand;
import com.exchange.clearing.port.out.LedgerRepositoryPort;
import com.exchange.clearing.port.out.WalletRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepositoryPort walletRepository;

    @Mock
    private LedgerRepositoryPort ledgerRepository;

    private WalletService walletService;

    @BeforeEach
    void setUp() {
        ClearingProperties properties = new ClearingProperties(0L, new BigDecimal("0.001"), new BigDecimal("1000000"));
        walletService = new WalletService(walletRepository, ledgerRepository, properties);
    }

    @Test
    void depositTransfersFromTreasuryToUserWallet() {
        UUID eventId = UUID.randomUUID();
        Wallet userWallet = wallet(1L, 42L, 2L, "0");
        Wallet treasuryWallet = wallet(2L, 0L, 2L, "1000000");

        when(ledgerRepository.existsByEventId(eventId)).thenReturn(false);
        when(walletRepository.findByUserIdAndCurrencyIdForUpdate(42L, 2L)).thenReturn(Optional.of(userWallet));
        when(walletRepository.findByUserIdAndCurrencyIdForUpdate(0L, 2L)).thenReturn(Optional.of(treasuryWallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.deposit(new DepositCommand(eventId, 42L, 2L, new BigDecimal("250")));

        assertEquals(new BigDecimal("250"), result.getAvailableBalance());
        assertEquals(new BigDecimal("999750"), treasuryWallet.getAvailableBalance());
        verify(ledgerRepository).save(any());
    }

    private static Wallet wallet(Long id, Long userId, Long currencyId, String available) {
        Wallet wallet = new Wallet();
        wallet.setId(id);
        wallet.setUserId(userId);
        wallet.setCurrencyId(currencyId);
        wallet.setAvailableBalance(new BigDecimal(available));
        wallet.setLockedBalance(BigDecimal.ZERO);
        return wallet;
    }
}
