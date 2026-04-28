-- Ledger domain schema

CREATE TABLE posting_batch (
    batch_id VARCHAR(36) PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL,
    idempotency_key VARCHAR(128),
    batch_status VARCHAR(16) NOT NULL,
    posted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_posting_batch_transaction UNIQUE (transaction_id),
    CONSTRAINT uq_posting_batch_idempotency_key UNIQUE (idempotency_key),
    CONSTRAINT fk_posting_batch_txn FOREIGN KEY (transaction_id) REFERENCES business_transaction (transaction_id),
    CONSTRAINT ck_posting_batch_status CHECK (
        batch_status IN (
            'PENDING',
            'POSTED',
            'FAILED',
            'REVERSED'
        )
    ),
    CONSTRAINT ck_posting_batch_posted_at CHECK (
        posted_at IS NULL
        OR posted_at >= created_at
    )
);

CREATE TABLE account_balance (
    account_id VARCHAR(36) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    ledger_balance NUMERIC(19, 4) NOT NULL DEFAULT 0,
    available_balance NUMERIC(19, 4) NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_account_balance PRIMARY KEY (account_id, currency_code),
    CONSTRAINT fk_balance_account FOREIGN KEY (account_id) REFERENCES account (account_id),
    CONSTRAINT fk_balance_currency FOREIGN KEY (currency_code) REFERENCES currency (currency_code)
);

CREATE TABLE account_entry (
    entry_id VARCHAR(36) PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL,
    batch_id VARCHAR(36),
    account_id VARCHAR(36) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    entry_direction VARCHAR(16) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    ledger_balance_after NUMERIC(19, 4),
    available_balance_after NUMERIC(19, 4),
    entry_type VARCHAR(32) NOT NULL,
    posted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_entry_txn FOREIGN KEY (transaction_id) REFERENCES business_transaction (transaction_id),
    CONSTRAINT fk_entry_batch FOREIGN KEY (batch_id) REFERENCES posting_batch (batch_id),
    CONSTRAINT fk_entry_account FOREIGN KEY (account_id) REFERENCES account (account_id),
    CONSTRAINT fk_entry_currency FOREIGN KEY (currency_code) REFERENCES currency (currency_code),
    CONSTRAINT ck_entry_direction CHECK (
        entry_direction IN (
            'DEBIT',
            'CREDIT',
            'INCREASE',
            'DECREASE'
        )
    ),
    CONSTRAINT ck_entry_amount_positive CHECK (amount > 0),
    CONSTRAINT ck_entry_type CHECK (
        entry_type IN (
            'PRINCIPAL',
            'FEE',
            'INTEREST',
            'DIVIDEND',
            'REPAYMENT',
            'ADJUSTMENT'
        )
    )
);

CREATE INDEX idx_account_entry_account_posted_at ON account_entry (account_id, posted_at);

CREATE INDEX idx_account_entry_transaction_id ON account_entry (transaction_id);