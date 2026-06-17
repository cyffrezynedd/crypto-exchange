CREATE TABLE outbox_events (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type      VARCHAR(32)     NOT NULL,
    aggregate_id        VARCHAR(64)     NOT NULL,
    event_type          VARCHAR(64)     NOT NULL,
    payload             JSONB           NOT NULL,
    processed           BOOLEAN         NOT NULL DEFAULT FALSE,
    processed_at        TIMESTAMPTZ,
    retry_count         INTEGER         NOT NULL DEFAULT 0,
    last_error          TEXT,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_outbox_events_retry_count_nonneg CHECK (retry_count >= 0)
);

CREATE INDEX idx_outbox_events_unprocessed
    ON outbox_events (created_at)
    WHERE processed = FALSE;
