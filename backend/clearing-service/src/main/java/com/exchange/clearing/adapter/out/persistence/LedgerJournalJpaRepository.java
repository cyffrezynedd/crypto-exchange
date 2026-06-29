package com.exchange.clearing.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface LedgerJournalJpaRepository extends JpaRepository<LedgerJournalJpaEntity, Long> {

    boolean existsByEventId(UUID eventId);

    Optional<LedgerJournalJpaEntity> findByEventId(UUID eventId);
}
