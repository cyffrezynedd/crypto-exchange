package com.exchange.clearing.application;

import com.exchange.clearing.domain.exception.InsufficientFundsException;
import com.exchange.clearing.domain.model.LedgerEntry;
import com.exchange.clearing.domain.model.LedgerEntryDirection;
import com.exchange.clearing.domain.model.LedgerJournal;
import com.exchange.clearing.domain.model.LedgerTransactionType;
import com.exchange.clearing.domain.model.Wallet;
import com.exchange.clearing.port.in.ClearingUseCase;
import com.exchange.clearing.port.in.SettleTradeResult;
import com.exchange.clearing.port.in.UnfreezeFundsResult;
import com.exchange.clearing.port.out.LedgerRepositoryPort;
import com.exchange.clearing.port.out.WalletRepositoryPort;
import com.exchange.common.clearing.CheckBalanceRequest;
import com.exchange.common.clearing.CheckBalanceResponse;
import com.exchange.common.clearing.FreezeFundsRequest;
import com.exchange.common.clearing.FreezeFundsResponse;
import com.exchange.common.clearing.SettleTradeRequest;
import com.exchange.common.clearing.UnfreezeFundsRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@Transactional
public class ClearingOperationsService implements ClearingUseCase {

    private static final int MONEY_SCALE = 18;

    private final WalletRepositoryPort walletRepository;
    private final LedgerRepositoryPort ledgerRepository;
    private final ClearingProperties properties;

    public ClearingOperationsService(
            WalletRepositoryPort walletRepository,
            LedgerRepositoryPort ledgerRepository,
            ClearingProperties properties) {
        this.walletRepository = walletRepository;
        this.ledgerRepository = ledgerRepository;
        this.properties = properties;
    }

    @Override
    @Transactional(readOnly = true)
    public CheckBalanceResponse checkBalance(CheckBalanceRequest request) {
        BigDecimal available = walletRepository
                .findByUserIdAndCurrencyId(request.userId(), request.currencyId())
                .map(Wallet::getAvailableBalance)
                .orElse(BigDecimal.ZERO);
        boolean sufficient = available.compareTo(request.requiredAmount()) >= 0;
        return new CheckBalanceResponse(sufficient, available);
    }

    @Override
    public FreezeFundsResponse freezeFunds(FreezeFundsRequest request) {
        UUID eventId = toEventId(request.idempotencyKey());
        if (ledgerRepository.existsByEventId(eventId)) {
            Wallet wallet = walletRepository.findByUserIdAndCurrencyId(request.userId(), request.currencyId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Freeze journal exists but wallet missing for eventId=" + eventId));
            return new FreezeFundsResponse(true, wallet.getId(), wallet.getLockedBalance());
        }

        Wallet wallet = getOrCreateWalletForUpdate(request.userId(), request.currencyId());
        if (wallet.getAvailableBalance().compareTo(request.amount()) < 0) {
            return new FreezeFundsResponse(false, wallet.getId(), wallet.getLockedBalance());
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(request.amount()));
        wallet.setLockedBalance(wallet.getLockedBalance().add(request.amount()));
        Wallet saved = walletRepository.save(wallet);

        LedgerJournal journal = new LedgerJournal();
        journal.setEventId(eventId);
        journal.setTransactionType(LedgerTransactionType.ORDER_LOCK);
        journal.setDescription(request.reason());
        journal.addEntry(newEntry(saved.getId(), LedgerEntryDirection.DEBIT, request.amount()));
        journal.addEntry(newEntry(saved.getId(), LedgerEntryDirection.CREDIT, request.amount()));
        ledgerRepository.save(journal);

        return new FreezeFundsResponse(true, saved.getId(), saved.getLockedBalance());
    }

    @Override
    public UnfreezeFundsResult unfreezeFunds(UnfreezeFundsRequest request) {
        UUID eventId = toEventId(request.idempotencyKey());
        if (ledgerRepository.existsByEventId(eventId)) {
            Wallet wallet = walletRepository.findByUserIdAndCurrencyId(request.userId(), request.currencyId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Unfreeze journal exists but wallet missing for eventId=" + eventId));
            return new UnfreezeFundsResult(true, wallet.getAvailableBalance());
        }

        Wallet wallet = walletRepository.findByUserIdAndCurrencyIdForUpdate(request.userId(), request.currencyId())
                .orElseThrow(() -> new InsufficientFundsException(
                        request.userId(), request.currencyId(), request.amount(), BigDecimal.ZERO));

        if (wallet.getLockedBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException(
                    request.userId(), request.currencyId(), request.amount(), wallet.getLockedBalance());
        }

        wallet.setLockedBalance(wallet.getLockedBalance().subtract(request.amount()));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(request.amount()));
        Wallet saved = walletRepository.save(wallet);

        LedgerJournal journal = new LedgerJournal();
        journal.setEventId(eventId);
        journal.setTransactionType(LedgerTransactionType.ORDER_UNLOCK);
        journal.setDescription(request.reason());
        journal.addEntry(newEntry(saved.getId(), LedgerEntryDirection.CREDIT, request.amount()));
        journal.addEntry(newEntry(saved.getId(), LedgerEntryDirection.DEBIT, request.amount()));
        ledgerRepository.save(journal);

        return new UnfreezeFundsResult(true, saved.getAvailableBalance());
    }

    @Override
    public SettleTradeResult settleTrade(SettleTradeRequest request) {
        UUID eventId = request.eventId();
        if (ledgerRepository.existsByEventId(eventId)) {
            return new SettleTradeResult(request.tradeId(), true);
        }

        BigDecimal quoteAmount = request.price()
                .multiply(request.quantity())
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal feeRate = request.feeRate() != null ? request.feeRate() : properties.defaultFeeRate();
        BigDecimal fee = quoteAmount.multiply(feeRate).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal buyerQuoteDebit = quoteAmount.add(fee);

        Wallet buyerQuote = lockWallet(request.buyerId(), request.quoteCurrencyId());
        Wallet sellerQuote = lockWallet(request.sellerId(), request.quoteCurrencyId());
        Wallet buyerBase = lockWallet(request.buyerId(), request.baseCurrencyId());
        Wallet sellerBase = lockWallet(request.sellerId(), request.baseCurrencyId());
        Wallet platformQuote = lockTreasuryWallet(request.quoteCurrencyId());

        if (buyerQuote.getLockedBalance().compareTo(buyerQuoteDebit) < 0) {
            throw new InsufficientFundsException(
                    request.buyerId(), request.quoteCurrencyId(), buyerQuoteDebit, buyerQuote.getLockedBalance());
        }
        if (sellerBase.getLockedBalance().compareTo(request.quantity()) < 0) {
            throw new InsufficientFundsException(
                    request.sellerId(), request.baseCurrencyId(), request.quantity(), sellerBase.getLockedBalance());
        }

        buyerQuote.setLockedBalance(buyerQuote.getLockedBalance().subtract(buyerQuoteDebit));
        sellerQuote.setAvailableBalance(sellerQuote.getAvailableBalance().add(quoteAmount));
        platformQuote.setAvailableBalance(platformQuote.getAvailableBalance().add(fee));
        sellerBase.setLockedBalance(sellerBase.getLockedBalance().subtract(request.quantity()));
        buyerBase.setAvailableBalance(buyerBase.getAvailableBalance().add(request.quantity()));

        walletRepository.save(buyerQuote);
        walletRepository.save(sellerQuote);
        walletRepository.save(platformQuote);
        walletRepository.save(sellerBase);
        walletRepository.save(buyerBase);

        LedgerJournal settlementJournal = new LedgerJournal();
        settlementJournal.setEventId(eventId);
        settlementJournal.setTransactionType(LedgerTransactionType.TRADE_SETTLEMENT);
        settlementJournal.setReferenceId(request.tradeId());
        settlementJournal.setDescription("Trade settlement tradeId=%s".formatted(request.tradeId()));

        settlementJournal.addEntry(newEntry(buyerQuote.getId(), LedgerEntryDirection.DEBIT, buyerQuoteDebit));
        settlementJournal.addEntry(newEntry(sellerQuote.getId(), LedgerEntryDirection.CREDIT, quoteAmount));
        if (fee.compareTo(BigDecimal.ZERO) > 0) {
            settlementJournal.addEntry(newEntry(platformQuote.getId(), LedgerEntryDirection.CREDIT, fee));
        }
        settlementJournal.addEntry(newEntry(sellerBase.getId(), LedgerEntryDirection.DEBIT, request.quantity()));
        settlementJournal.addEntry(newEntry(buyerBase.getId(), LedgerEntryDirection.CREDIT, request.quantity()));

        ledgerRepository.save(settlementJournal);

        return new SettleTradeResult(request.tradeId(), true);
    }

    private Wallet lockWallet(Long userId, Long currencyId) {
        return walletRepository.findByUserIdAndCurrencyIdForUpdate(userId, currencyId)
                .orElseThrow(() -> new InsufficientFundsException(userId, currencyId, BigDecimal.ONE, BigDecimal.ZERO));
    }

    private Wallet lockTreasuryWallet(Long currencyId) {
        long platformUserId = properties.platformUserId();
        return walletRepository.findByUserIdAndCurrencyIdForUpdate(platformUserId, currencyId)
                .orElseGet(() -> walletRepository.save(
                        newWallet(platformUserId, currencyId, properties.treasuryInitialBalance())));
    }

    private Wallet getOrCreateWalletForUpdate(Long userId, Long currencyId) {
        return walletRepository.findByUserIdAndCurrencyIdForUpdate(userId, currencyId)
                .orElseGet(() -> walletRepository.save(newWallet(userId, currencyId)));
    }

    private static Wallet newWallet(Long userId, Long currencyId) {
        return newWallet(userId, currencyId, BigDecimal.ZERO);
    }

    private static Wallet newWallet(Long userId, Long currencyId, BigDecimal initialBalance) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setCurrencyId(currencyId);
        wallet.setAvailableBalance(initialBalance);
        wallet.setLockedBalance(BigDecimal.ZERO);
        return wallet;
    }

    private static LedgerEntry newEntry(Long walletId, LedgerEntryDirection direction, BigDecimal amount) {
        LedgerEntry entry = new LedgerEntry();
        entry.setWalletId(walletId);
        entry.setDirection(direction);
        entry.setAmount(amount);
        return entry;
    }

    static UUID toEventId(String idempotencyKey) {
        try {
            return UUID.fromString(idempotencyKey);
        } catch (IllegalArgumentException ignored) {
            return UUID.nameUUIDFromBytes(idempotencyKey.getBytes(StandardCharsets.UTF_8));
        }
    }
}
