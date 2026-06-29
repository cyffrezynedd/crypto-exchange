package com.exchange.clearing.port.out;

import com.exchange.clearing.domain.model.LedgerJournal;

import java.util.Optional;
import java.util.UUID;

public interface LedgerRepositoryPort {

    boolean existsByEventId(UUID eventId);

    Optional<LedgerJournal> findByEventId(UUID eventId);

    LedgerJournal save(LedgerJournal journal);
}
