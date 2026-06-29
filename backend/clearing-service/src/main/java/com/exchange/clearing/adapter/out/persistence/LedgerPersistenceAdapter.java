package com.exchange.clearing.adapter.out.persistence;

import com.exchange.clearing.domain.model.LedgerEntry;
import com.exchange.clearing.domain.model.LedgerJournal;
import com.exchange.clearing.port.out.LedgerRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class LedgerPersistenceAdapter implements LedgerRepositoryPort {

    private final LedgerJournalJpaRepository repository;

    public LedgerPersistenceAdapter(LedgerJournalJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByEventId(UUID eventId) {
        return repository.existsByEventId(eventId);
    }

    @Override
    public Optional<LedgerJournal> findByEventId(UUID eventId) {
        return repository.findByEventId(eventId).map(this::toDomain);
    }

    @Override
    public LedgerJournal save(LedgerJournal journal) {
        LedgerJournalJpaEntity entity = toEntity(journal);
        LedgerJournalJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    private LedgerJournalJpaEntity toEntity(LedgerJournal journal) {
        LedgerJournalJpaEntity entity = new LedgerJournalJpaEntity();
        entity.setEventId(journal.getEventId());
        entity.setTransactionType(journal.getTransactionType());
        entity.setReferenceId(journal.getReferenceId());
        entity.setDescription(journal.getDescription());
        for (LedgerEntry entry : journal.getEntries()) {
            LedgerEntryJpaEntity entryEntity = new LedgerEntryJpaEntity();
            entryEntity.setWalletId(entry.getWalletId());
            entryEntity.setDirection(entry.getDirection());
            entryEntity.setAmount(entry.getAmount());
            entity.addEntry(entryEntity);
        }
        return entity;
    }

    private LedgerJournal toDomain(LedgerJournalJpaEntity entity) {
        LedgerJournal journal = new LedgerJournal();
        journal.setId(entity.getId());
        journal.setEventId(entity.getEventId());
        return journal;
    }
}
