CREATE TABLE clearing.ledger_journals (
    id                  BIGSERIAL       PRIMARY KEY,
    event_id            UUID            NOT NULL,
    transaction_type    ledger_transaction_type NOT NULL,
    reference_id        UUID,
    description         VARCHAR(255),
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_ledger_journals_event_id UNIQUE (event_id)
);

CREATE INDEX idx_ledger_journals_reference_id ON clearing.ledger_journals (reference_id);
CREATE INDEX idx_ledger_journals_created_at ON clearing.ledger_journals (created_at DESC);

CREATE TABLE clearing.ledger_entries (
    id                  BIGSERIAL       PRIMARY KEY,
    journal_id          BIGINT          NOT NULL REFERENCES clearing.ledger_journals (id) ON DELETE RESTRICT,
    wallet_id           BIGINT          NOT NULL REFERENCES clearing.wallets (id),
    direction           ledger_entry_direction NOT NULL,
    amount              NUMERIC(36, 18) NOT NULL,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_ledger_entries_amount_positive CHECK (amount > 0)
);

CREATE INDEX idx_ledger_entries_journal_id ON clearing.ledger_entries (journal_id);
CREATE INDEX idx_ledger_entries_wallet_id ON clearing.ledger_entries (wallet_id);
