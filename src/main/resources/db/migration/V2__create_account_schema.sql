-- Account and reference schema

CREATE TABLE currency (
    currency_code VARCHAR(3) PRIMARY KEY,
    currency_name VARCHAR(64) NOT NULL,
    minor_unit SMALLINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_currency_minor_unit CHECK (
        minor_unit >= 0
        AND minor_unit <= 6
    )
);

CREATE TABLE account (
    account_id VARCHAR(36) PRIMARY KEY,
    customer_id VARCHAR(36) NOT NULL,
    account_no VARCHAR(32) NOT NULL,
    account_type VARCHAR(16) NOT NULL,
    account_status VARCHAR(16) NOT NULL,
    opened_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    closed_at TIMESTAMPTZ,
    CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES customer (customer_id),
    CONSTRAINT uq_account_no UNIQUE (account_no),
    CONSTRAINT uq_account_customer_composite UNIQUE (account_id, customer_id),
    CONSTRAINT ck_account_type CHECK (
        account_type IN (
            'SAVINGS',
            'FX',
            'INVESTMENT',
            'CREDIT_CARD'
        )
    ),
    CONSTRAINT ck_account_status CHECK (
        account_status IN ('ACTIVE', 'FROZEN', 'CLOSED')
    ),
    CONSTRAINT ck_account_closed_time CHECK (
        closed_at IS NULL
        OR closed_at >= opened_at
    )
);

CREATE INDEX idx_account_customer_id ON account (customer_id);

CREATE TABLE savings_account (
    account_id VARCHAR(36) PRIMARY KEY,
    CONSTRAINT fk_savings_account_base FOREIGN KEY (account_id) REFERENCES account (account_id)
);

CREATE TABLE fx_account (
    account_id VARCHAR(36) PRIMARY KEY,
    linked_savings_account_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_fx_account_base FOREIGN KEY (account_id) REFERENCES account (account_id),
    CONSTRAINT fk_fx_linked_savings FOREIGN KEY (linked_savings_account_id) REFERENCES savings_account (account_id)
);

CREATE INDEX idx_fx_linked_savings_account_id ON fx_account (linked_savings_account_id);

CREATE TABLE investment_account (
    account_id VARCHAR(36) PRIMARY KEY,
    CONSTRAINT fk_investment_account_base FOREIGN KEY (account_id) REFERENCES account (account_id)
);