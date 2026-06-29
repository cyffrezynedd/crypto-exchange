package com.exchange.clearing.application;

import com.exchange.clearing.domain.model.LedgerEntry;
import com.exchange.clearing.domain.model.LedgerEntryDirection;
import com.exchange.clearing.domain.model.LedgerJournal;
import com.exchange.clearing.domain.model.LedgerTransactionType;
import com.exchange.clearing.domain.model.Wallet;
import com.exchange.clearing.port.in.DepositCommand;
import com.exchange.clearing.port.in.WalletUseCase;
import com.exchange.clearing.port.out.LedgerRepositoryPort;
import com.exchange.clearing.port.out.WalletRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WalletService implements WalletUseCase {

    private final WalletRepositoryPort walletRepository;
    private final LedgerRepositoryPort ledgerRepository;
    private final ClearingProperties properties;

    public WalletService(
            WalletRepositoryPort walletRepository,
            LedgerRepositoryPort ledgerRepository,
            ClearingProperties properties) {
        this.walletRepository = walletRepository;
        this.ledgerRepository = ledgerRepository;
        this.properties = properties;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Wallet> listWalletsByUserId(Long userId) {
        return walletRepository.findAllByUserId(userId);
    }

    @Override
    public Wallet deposit(DepositCommand command) {
        UUID eventId = command.eventId() != null ? command.eventId() : UUID.randomUUID();
        if (ledgerRepository.existsByEventId(eventId)) {
            return walletRepository.findByUserIdAndCurrencyId(command.userId(), command.currencyId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Deposit journal exists but wallet missing for eventId=" + eventId));
        }

        Wallet userWallet = getOrCreateWalletForUpdate(command.userId(), command.currencyId());
        Wallet treasuryWallet = getOrCreateTreasuryWalletForUpdate(command.currencyId());

        if (treasuryWallet.getAvailableBalance().compareTo(command.amount()) < 0) {
            throw new com.exchange.clearing.domain.exception.InsufficientFundsException(
                    properties.platformUserId(), command.currencyId(), command.amount(),
                    treasuryWallet.getAvailableBalance());
        }

        userWallet.setAvailableBalance(userWallet.getAvailableBalance().add(command.amount()));
        treasuryWallet.setAvailableBalance(treasuryWallet.getAvailableBalance().subtract(command.amount()));

        walletRepository.save(userWallet);
        walletRepository.save(treasuryWallet);

        LedgerJournal journal = new LedgerJournal();
        journal.setEventId(eventId);
        journal.setTransactionType(LedgerTransactionType.DEPOSIT);
        journal.setDescription("Test deposit for userId=%d".formatted(command.userId()));

        journal.addEntry(newEntry(userWallet.getId(), LedgerEntryDirection.CREDIT, command.amount()));
        journal.addEntry(newEntry(treasuryWallet.getId(), LedgerEntryDirection.DEBIT, command.amount()));

        ledgerRepository.save(journal);
        return userWallet;
    }

    private Wallet getOrCreateWalletForUpdate(Long userId, Long currencyId) {
        return walletRepository.findByUserIdAndCurrencyIdForUpdate(userId, currencyId)
                .orElseGet(() -> walletRepository.save(newWallet(userId, currencyId)));
    }

    private Wallet getOrCreateTreasuryWalletForUpdate(Long currencyId) {
        long platformUserId = properties.platformUserId();
        return walletRepository.findByUserIdAndCurrencyIdForUpdate(platformUserId, currencyId)
                .orElseGet(() -> walletRepository.save(
                        newWallet(platformUserId, currencyId, properties.treasuryInitialBalance())));
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
}
