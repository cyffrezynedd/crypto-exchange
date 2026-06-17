CREATE TABLE wallets (
    id                  BIGSERIAL       PRIMARY KEY,
    user_id             BIGINT          NOT NULL REFERENCES users (id),
    currency_id         BIGINT          NOT NULL REFERENCES currencies (id),
    available_balance   NUMERIC(36, 18) NOT NULL DEFAULT 0,
    locked_balance      NUMERIC(36, 18) NOT NULL DEFAULT 0,
    version             BIGINT          NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_wallets_user_currency UNIQUE (user_id, currency_id),
    CONSTRAINT chk_wallets_available_nonneg CHECK (available_balance >= 0),
    CONSTRAINT chk_wallets_locked_nonneg CHECK (locked_balance >= 0)
);

CREATE INDEX idx_wallets_user_id ON wallets (user_id);
CREATE INDEX idx_wallets_currency_id ON wallets (currency_id);
