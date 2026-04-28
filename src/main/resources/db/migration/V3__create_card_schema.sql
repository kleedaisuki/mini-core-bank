-- Card domain schema

CREATE TABLE debit_card (
    card_id VARCHAR(36) PRIMARY KEY,
    card_no VARCHAR(32) NOT NULL,
    holder_customer_id VARCHAR(36) NOT NULL,
    savings_account_id VARCHAR(36) NOT NULL,
    fx_account_id VARCHAR(36) NOT NULL,
    card_status VARCHAR(16) NOT NULL,
    issued_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expired_at TIMESTAMPTZ,
    CONSTRAINT uq_debit_card_no UNIQUE (card_no),
    CONSTRAINT fk_debit_card_holder FOREIGN KEY (holder_customer_id) REFERENCES customer (customer_id),
    CONSTRAINT fk_debit_card_savings FOREIGN KEY (savings_account_id) REFERENCES savings_account (account_id),
    CONSTRAINT fk_debit_card_fx FOREIGN KEY (fx_account_id) REFERENCES fx_account (account_id),
    CONSTRAINT fk_debit_card_savings_owner FOREIGN KEY (
        savings_account_id,
        holder_customer_id
    ) REFERENCES account (account_id, customer_id),
    CONSTRAINT fk_debit_card_fx_owner FOREIGN KEY (
        fx_account_id,
        holder_customer_id
    ) REFERENCES account (account_id, customer_id),
    CONSTRAINT ck_debit_card_status CHECK (
        card_status IN (
            'ACTIVE',
            'BLOCKED',
            'CLOSED',
            'EXPIRED'
        )
    ),
    CONSTRAINT ck_debit_card_expiry CHECK (
        expired_at IS NULL
        OR expired_at > issued_at
    )
);

CREATE TABLE supplementary_debit_card (
    supplementary_card_id VARCHAR(36) PRIMARY KEY,
    card_no VARCHAR(32) NOT NULL,
    holder_customer_id VARCHAR(36) NOT NULL,
    primary_debit_card_id VARCHAR(36) NOT NULL,
    card_status VARCHAR(16) NOT NULL,
    issued_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expired_at TIMESTAMPTZ,
    CONSTRAINT uq_supplementary_debit_card_no UNIQUE (card_no),
    CONSTRAINT uq_primary_debit_card_single_supplementary UNIQUE (primary_debit_card_id),
    CONSTRAINT fk_supplementary_debit_holder FOREIGN KEY (holder_customer_id) REFERENCES customer (customer_id),
    CONSTRAINT fk_supplementary_primary_debit FOREIGN KEY (primary_debit_card_id) REFERENCES debit_card (card_id),
    CONSTRAINT ck_supplementary_debit_status CHECK (
        card_status IN (
            'ACTIVE',
            'BLOCKED',
            'CLOSED',
            'EXPIRED'
        )
    ),
    CONSTRAINT ck_supplementary_debit_expiry CHECK (
        expired_at IS NULL
        OR expired_at > issued_at
    )
);

CREATE TABLE credit_card (
    credit_card_id VARCHAR(36) PRIMARY KEY,
    card_no VARCHAR(32) NOT NULL,
    holder_customer_id VARCHAR(36) NOT NULL,
    credit_card_account_id VARCHAR(36) NOT NULL,
    card_role VARCHAR(16) NOT NULL,
    primary_credit_card_id VARCHAR(36),
    card_status VARCHAR(16) NOT NULL,
    issued_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expired_at TIMESTAMPTZ,
    CONSTRAINT uq_credit_card_no UNIQUE (card_no),
    CONSTRAINT fk_credit_card_holder FOREIGN KEY (holder_customer_id) REFERENCES customer (customer_id),
    CONSTRAINT fk_credit_card_primary FOREIGN KEY (primary_credit_card_id) REFERENCES credit_card (credit_card_id),
    CONSTRAINT ck_credit_card_role CHECK (
        card_role IN ('PRIMARY', 'SUPPLEMENTARY')
    ),
    CONSTRAINT ck_credit_card_status CHECK (
        card_status IN (
            'ACTIVE',
            'BLOCKED',
            'CLOSED',
            'EXPIRED'
        )
    ),
    CONSTRAINT ck_credit_card_role_parent CHECK (
        (
            card_role = 'PRIMARY'
            AND primary_credit_card_id IS NULL
        )
        OR (
            card_role = 'SUPPLEMENTARY'
            AND primary_credit_card_id IS NOT NULL
        )
    ),
    CONSTRAINT ck_credit_card_expiry CHECK (
        expired_at IS NULL
        OR expired_at > issued_at
    )
);

CREATE INDEX idx_debit_card_holder_customer_id ON debit_card (holder_customer_id);

CREATE INDEX idx_credit_card_holder_customer_id ON credit_card (holder_customer_id);

CREATE INDEX idx_credit_card_account_id ON credit_card (credit_card_account_id);