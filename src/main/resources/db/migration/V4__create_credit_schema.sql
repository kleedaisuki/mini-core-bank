-- Credit domain schema

CREATE TABLE credit_card_account (
    account_id VARCHAR(36) PRIMARY KEY,
    credit_limit NUMERIC(19, 4) NOT NULL,
    available_credit NUMERIC(19, 4) NOT NULL,
    billing_cycle_day SMALLINT NOT NULL,
    payment_due_day SMALLINT NOT NULL,
    interest_rate NUMERIC(9, 6) NOT NULL,
    cash_advance_limit NUMERIC(19, 4) NOT NULL,
    account_currency_code VARCHAR(3) NOT NULL,
    CONSTRAINT fk_credit_card_account_base FOREIGN KEY (account_id) REFERENCES account (account_id),
    CONSTRAINT fk_credit_account_currency FOREIGN KEY (account_currency_code) REFERENCES currency (currency_code),
    CONSTRAINT ck_credit_limit_non_negative CHECK (credit_limit >= 0),
    CONSTRAINT ck_available_credit_non_negative CHECK (available_credit >= 0),
    CONSTRAINT ck_available_credit_not_exceed_limit CHECK (
        available_credit <= credit_limit
    ),
    CONSTRAINT ck_billing_cycle_day CHECK (
        billing_cycle_day BETWEEN 1 AND 31
    ),
    CONSTRAINT ck_payment_due_day CHECK (
        payment_due_day BETWEEN 1 AND 31
    ),
    CONSTRAINT ck_interest_rate_non_negative CHECK (interest_rate >= 0),
    CONSTRAINT ck_cash_advance_limit_non_negative CHECK (cash_advance_limit >= 0)
);

ALTER TABLE credit_card
ADD CONSTRAINT fk_credit_card_credit_account FOREIGN KEY (credit_card_account_id) REFERENCES credit_card_account (account_id);

CREATE TABLE credit_card_statement (
    statement_id VARCHAR(36) PRIMARY KEY,
    credit_card_account_id VARCHAR(36) NOT NULL,
    statement_period_start DATE NOT NULL,
    statement_period_end DATE NOT NULL,
    statement_date DATE NOT NULL,
    payment_due_date DATE NOT NULL,
    total_amount_due NUMERIC(19, 4) NOT NULL,
    minimum_amount_due NUMERIC(19, 4) NOT NULL,
    paid_amount NUMERIC(19, 4) NOT NULL DEFAULT 0,
    statement_status VARCHAR(16) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    CONSTRAINT fk_statement_credit_account FOREIGN KEY (credit_card_account_id) REFERENCES credit_card_account (account_id),
    CONSTRAINT fk_statement_currency FOREIGN KEY (currency_code) REFERENCES currency (currency_code),
    CONSTRAINT uq_statement_period UNIQUE (
        credit_card_account_id,
        statement_period_start,
        statement_period_end
    ),
    CONSTRAINT ck_statement_period CHECK (
        statement_period_end >= statement_period_start
    ),
    CONSTRAINT ck_statement_due_amount_non_negative CHECK (total_amount_due >= 0),
    CONSTRAINT ck_statement_min_due_non_negative CHECK (minimum_amount_due >= 0),
    CONSTRAINT ck_statement_paid_non_negative CHECK (paid_amount >= 0),
    CONSTRAINT ck_statement_paid_not_exceed_total CHECK (
        paid_amount <= total_amount_due
    ),
    CONSTRAINT ck_statement_status CHECK (
        statement_status IN (
            'OPEN',
            'PAID',
            'OVERDUE',
            'CLOSED'
        )
    )
);

CREATE INDEX idx_statement_credit_account ON credit_card_statement (credit_card_account_id);