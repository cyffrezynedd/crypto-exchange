CREATE TABLE iam.users (
    id              BIGSERIAL       PRIMARY KEY,
    email           VARCHAR(255)    NOT NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    username        VARCHAR(64)     NOT NULL,
    kyc_status      kyc_status      NOT NULL DEFAULT 'PENDING',
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT uq_users_username UNIQUE (username)
);

CREATE TABLE iam.roles (
    id              BIGSERIAL       PRIMARY KEY,
    code            VARCHAR(32)     NOT NULL,
    name            VARCHAR(64)     NOT NULL,
    description     VARCHAR(255),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_roles_code UNIQUE (code)
);

CREATE TABLE iam.user_roles (
    user_id         BIGINT          NOT NULL REFERENCES iam.users (id) ON DELETE CASCADE,
    role_id         BIGINT          NOT NULL REFERENCES iam.roles (id) ON DELETE CASCADE,
    assigned_at     TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_roles_role_id ON iam.user_roles (role_id);
