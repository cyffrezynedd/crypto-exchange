CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE SCHEMA IF NOT EXISTS iam;
CREATE SCHEMA IF NOT EXISTS market;
CREATE SCHEMA IF NOT EXISTS clearing;
CREATE SCHEMA IF NOT EXISTS trading;
CREATE SCHEMA IF NOT EXISTS platform;

CREATE TYPE kyc_status AS ENUM (
    'PENDING',
    'VERIFIED',
    'REJECTED'
);

CREATE TYPE order_side AS ENUM (
    'BUY',
    'SELL'
);

CREATE TYPE order_type AS ENUM (
    'LIMIT',
    'MARKET'
);

CREATE TYPE order_status AS ENUM (
    'NEW',
    'PARTIALLY_FILLED',
    'FILLED',
    'CANCELLED'
);

CREATE TYPE ledger_transaction_type AS ENUM (
    'DEPOSIT',
    'WITHDRAWAL',
    'TRADE_SETTLEMENT',
    'TRADE_FEE',
    'ORDER_LOCK',
    'ORDER_UNLOCK'
);

CREATE TYPE ledger_entry_direction AS ENUM (
    'DEBIT',
    'CREDIT'
);
