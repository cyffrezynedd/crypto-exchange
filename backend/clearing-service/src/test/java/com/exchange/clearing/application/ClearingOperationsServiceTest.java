package com.exchange.clearing.application;

import com.exchange.clearing.domain.model.Wallet;
import com.exchange.clearing.port.out.LedgerRepositoryPort;
import com.exchange.clearing.port.out.WalletRepositoryPort;
import com.exchange.common.clearing.CheckBalanceRequest;
import com.exchange.common.clearing.CheckBalanceResponse;
import com.exchange.common.clearing.FreezeFundsRequest;
import com.exchange.common.clearing.FreezeFundsResponse;
import com.exchange.common.clearing.UnfreezeFundsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearingOperationsServiceTest {

    @Mock
    private WalletRepositoryPort walletRepository;

    @Mock
    private LedgerRepositoryPort ledgerRepository;

    private ClearingOperationsService service;

    @BeforeEach
    void setUp() {
        ClearingProperties properties = new ClearingProperties(0L, new BigDecimal("0.001"), new BigDecimal("1000000"));
        service = new ClearingOperationsService(walletRepository, ledgerRepository, properties);
    }

    @Test
    void checkBalanceReportsSufficientFunds() {
        Wallet wallet = wallet(1L, 10L, 1L, "100", "0");
        when(walletRepository.findByUserIdAndCurrencyId(10L, 1L)).thenReturn(Optional.of(wallet));

        CheckBalanceResponse response = service.checkBalance(
                new CheckBalanceRequest(10L, 1L, new BigDecimal("50")));

        assertTrue(response.sufficient());
        assertEquals(new BigDecimal("100"), response.availableBalance());
    }

    @Test
    void checkBalanceTreatsMissingWalletAsZero() {
        when(walletRepository.findByUserIdAndCurrencyId(10L, 2L)).thenReturn(Optional.empty());

        CheckBalanceResponse response = service.checkBalance(
                new CheckBalanceRequest(10L, 2L, new BigDecimal("1")));

        assertFalse(response.sufficient());
        assertEquals(BigDecimal.ZERO, response.availableBalance());
    }

    @Test
    void freezeFundsMovesAvailableToLocked() {
        Wallet wallet = wallet(5L, 20L, 3L, "100", "0");
        when(ledgerRepository.existsByEventId(any())).thenReturn(false);
        when(walletRepository.findByUserIdAndCurrencyIdForUpdate(20L, 3L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);

        FreezeFundsResponse response = service.freezeFunds(new FreezeFundsRequest(
                "freeze-1", 20L, 3L, new BigDecimal("40"), "ORDER_LOCK"));

        assertTrue(response.success());
        assertEquals(new BigDecimal("60"), wallet.getAvailableBalance());
        assertEquals(new BigDecimal("40"), wallet.getLockedBalance());
        verify(ledgerRepository).save(any());
    }

    @Test
    void freezeFundsFailsWhenInsufficientAvailable() {
        Wallet wallet = wallet(6L, 21L, 1L, "10", "0");
        when(ledgerRepository.existsByEventId(any())).thenReturn(false);
        when(walletRepository.findByUserIdAndCurrencyIdForUpdate(21L, 1L)).thenReturn(Optional.of(wallet));

        FreezeFundsResponse response = service.freezeFunds(new FreezeFundsRequest(
                "freeze-2", 21L, 1L, new BigDecimal("50"), "ORDER_LOCK"));

        assertFalse(response.success());
        verify(ledgerRepository, never()).save(any());
    }

    @Test
    void unfreezeFundsReturnsLockedToAvailable() {
        Wallet wallet = wallet(7L, 30L, 1L, "0", "25");
        when(ledgerRepository.existsByEventId(any())).thenReturn(false);
        when(walletRepository.findByUserIdAndCurrencyIdForUpdate(30L, 1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);

        var result = service.unfreezeFunds(new UnfreezeFundsRequest(
                "unfreeze-1", 30L, 1L, new BigDecimal("10"), "ORDER_UNLOCK"));

        assertTrue(result.success());
        assertEquals(new BigDecimal("10"), wallet.getAvailableBalance());
        assertEquals(new BigDecimal("15"), wallet.getLockedBalance());
    }

    private static Wallet wallet(Long id, Long userId, Long currencyId, String available, String locked) {
        Wallet wallet = new Wallet();
        wallet.setId(id);
        wallet.setUserId(userId);
        wallet.setCurrencyId(currencyId);
        wallet.setAvailableBalance(new BigDecimal(available));
        wallet.setLockedBalance(new BigDecimal(locked));
        return wallet;
    }
}
