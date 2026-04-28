-- Business transaction domain schema

CREATE TABLE business_type (
    business_type_code VARCHAR(64) PRIMARY KEY,
    business_category VARCHAR(32) NOT NULL,
    business_name VARCHAR(128) NOT NULL,
    description TEXT,
    is_financial BOOLEAN NOT NULL DEFAULT TRUE,
    is_reversible BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(16) NOT NULL,
    CONSTRAINT ck_business_type_status CHECK (
        status IN ('ACTIVE', 'INACTIVE')
    )
);

CREATE TABLE business_transaction (
    transaction_id VARCHAR(36) PRIMARY KEY,
    business_type_code VARCHAR(64) NOT NULL,
    initiator_customer_id VARCHAR(36),
    operator_id VARCHAR(64),
    channel VARCHAR(16) NOT NULL,
    transaction_status VARCHAR(16) NOT NULL,
    requested_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ,
    reference_no VARCHAR(64) NOT NULL,
    remarks TEXT,
    CONSTRAINT fk_business_txn_type FOREIGN KEY (business_type_code) REFERENCES business_type (business_type_code),
    CONSTRAINT fk_business_txn_customer FOREIGN KEY (initiator_customer_id) REFERENCES customer (customer_id),
    CONSTRAINT uq_business_reference_no UNIQUE (reference_no),
    CONSTRAINT ck_business_channel CHECK (
        channel IN (
            'BRANCH',
            'MOBILE',
            'ATM',
            'ONLINE',
            'SYSTEM'
        )
    ),
    CONSTRAINT ck_business_txn_status CHECK (
        transaction_status IN (
            'PENDING',
            'SUCCESS',
            'FAILED',
            'REVERSED'
        )
    ),
    CONSTRAINT ck_business_completed_at CHECK (
        completed_at IS NULL
        OR completed_at >= requested_at
    )
);

CREATE INDEX idx_business_txn_customer_id ON business_transaction (initiator_customer_id);

CREATE INDEX idx_business_txn_requested_at ON business_transaction (requested_at);

CREATE TABLE transfer_detail (
    transaction_id VARCHAR(36) PRIMARY KEY,
    from_account_id VARCHAR(36) NOT NULL,
    from_currency_code VARCHAR(3) NOT NULL,
    to_account_id VARCHAR(36) NOT NULL,
    to_currency_code VARCHAR(3) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    exchange_rate NUMERIC(19, 8) NOT NULL,
    fee_amount NUMERIC(19, 4) NOT NULL DEFAULT 0,
    fee_currency_code VARCHAR(3),
    CONSTRAINT fk_transfer_txn FOREIGN KEY (transaction_id) REFERENCES business_transaction (transaction_id),
    CONSTRAINT fk_transfer_from_account FOREIGN KEY (from_account_id) REFERENCES account (account_id),
    CONSTRAINT fk_transfer_from_currency FOREIGN KEY (from_currency_code) REFERENCES currency (currency_code),
    CONSTRAINT fk_transfer_to_account FOREIGN KEY (to_account_id) REFERENCES account (account_id),
    CONSTRAINT fk_transfer_to_currency FOREIGN KEY (to_currency_code) REFERENCES currency (currency_code),
    CONSTRAINT fk_transfer_fee_currency FOREIGN KEY (fee_currency_code) REFERENCES currency (currency_code),
    CONSTRAINT ck_transfer_amount_positive CHECK (amount > 0),
    CONSTRAINT ck_transfer_exchange_rate_positive CHECK (exchange_rate > 0),
    CONSTRAINT ck_transfer_fee_non_negative CHECK (fee_amount >= 0)
);

CREATE TABLE investment_order_detail (
    transaction_id VARCHAR(36) PRIMARY KEY,
    investment_account_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    order_side VARCHAR(16) NOT NULL,
    quantity NUMERIC(19, 6) NOT NULL,
    price NUMERIC(19, 6) NOT NULL,
    gross_amount NUMERIC(19, 4) NOT NULL,
    fee_amount NUMERIC(19, 4) NOT NULL DEFAULT 0,
    currency_code VARCHAR(3) NOT NULL,
    trade_at TIMESTAMPTZ NOT NULL,
    settlement_at TIMESTAMPTZ,
    CONSTRAINT fk_investment_order_txn FOREIGN KEY (transaction_id) REFERENCES business_transaction (transaction_id),
    CONSTRAINT fk_investment_order_account FOREIGN KEY (investment_account_id) REFERENCES investment_account (account_id),
    CONSTRAINT fk_investment_order_product FOREIGN KEY (product_id) REFERENCES investment_product (product_id),
    CONSTRAINT fk_investment_order_currency FOREIGN KEY (currency_code) REFERENCES currency (currency_code),
    CONSTRAINT ck_investment_order_side CHECK (
        order_side IN (
            'BUY',
            'SELL',
            'REDEMPTION',
            'DIVIDEND'
        )
    ),
    CONSTRAINT ck_investment_order_quantity_non_negative CHECK (quantity >= 0),
    CONSTRAINT ck_investment_order_price_non_negative CHECK (price >= 0),
    CONSTRAINT ck_investment_order_gross_non_negative CHECK (gross_amount >= 0),
    CONSTRAINT ck_investment_order_fee_non_negative CHECK (fee_amount >= 0),
    CONSTRAINT ck_investment_order_settlement CHECK (
        settlement_at IS NULL
        OR settlement_at >= trade_at
    )
);

CREATE TABLE account_operation_detail (
    transaction_id VARCHAR(36) PRIMARY KEY,
    account_id VARCHAR(36) NOT NULL,
    related_account_id VARCHAR(36),
    operation_reason TEXT,
    CONSTRAINT fk_account_op_txn FOREIGN KEY (transaction_id) REFERENCES business_transaction (transaction_id),
    CONSTRAINT fk_account_op_account FOREIGN KEY (account_id) REFERENCES account (account_id),
    CONSTRAINT fk_account_op_related_account FOREIGN KEY (related_account_id) REFERENCES account (account_id)
);

CREATE TABLE card_operation_detail (
    transaction_id VARCHAR(36) PRIMARY KEY,
    card_kind VARCHAR(32) NOT NULL,
    debit_card_id VARCHAR(36),
    supplementary_debit_card_id VARCHAR(36),
    credit_card_id VARCHAR(36),
    related_card_id VARCHAR(36),
    savings_account_id VARCHAR(36),
    fx_account_id VARCHAR(36),
    credit_card_account_id VARCHAR(36),
    CONSTRAINT fk_card_op_txn FOREIGN KEY (transaction_id) REFERENCES business_transaction (transaction_id),
    CONSTRAINT fk_card_op_debit FOREIGN KEY (debit_card_id) REFERENCES debit_card (card_id),
    CONSTRAINT fk_card_op_supp_debit FOREIGN KEY (supplementary_debit_card_id) REFERENCES supplementary_debit_card (supplementary_card_id),
    CONSTRAINT fk_card_op_credit FOREIGN KEY (credit_card_id) REFERENCES credit_card (credit_card_id),
    CONSTRAINT fk_card_op_savings FOREIGN KEY (savings_account_id) REFERENCES savings_account (account_id),
    CONSTRAINT fk_card_op_fx FOREIGN KEY (fx_account_id) REFERENCES fx_account (account_id),
    CONSTRAINT fk_card_op_credit_account FOREIGN KEY (credit_card_account_id) REFERENCES credit_card_account (account_id),
    CONSTRAINT ck_card_op_kind CHECK (
        card_kind IN (
            'DEBIT',
            'SUPPLEMENTARY_DEBIT',
            'CREDIT',
            'SUPPLEMENTARY_CREDIT'
        )
    )
);

CREATE TABLE credit_card_transaction_detail (
    transaction_id VARCHAR(36) PRIMARY KEY,
    credit_card_id VARCHAR(36) NOT NULL,
    credit_card_account_id VARCHAR(36) NOT NULL,
    statement_id VARCHAR(36),
    merchant_name VARCHAR(256),
    merchant_category_code VARCHAR(8),
    amount NUMERIC(19, 4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    authorization_code VARCHAR(32),
    transaction_at TIMESTAMPTZ NOT NULL,
    posted_at TIMESTAMPTZ,
    CONSTRAINT fk_credit_txn_detail_txn FOREIGN KEY (transaction_id) REFERENCES business_transaction (transaction_id),
    CONSTRAINT fk_credit_txn_detail_card FOREIGN KEY (credit_card_id) REFERENCES credit_card (credit_card_id),
    CONSTRAINT fk_credit_txn_detail_account FOREIGN KEY (credit_card_account_id) REFERENCES credit_card_account (account_id),
    CONSTRAINT fk_credit_txn_detail_statement FOREIGN KEY (statement_id) REFERENCES credit_card_statement (statement_id),
    CONSTRAINT fk_credit_txn_detail_currency FOREIGN KEY (currency_code) REFERENCES currency (currency_code),
    CONSTRAINT ck_credit_txn_detail_amount_positive CHECK (amount > 0),
    CONSTRAINT ck_credit_txn_posted_after_txn CHECK (
        posted_at IS NULL
        OR posted_at >= transaction_at
    )
);

CREATE TABLE credit_card_repayment (
    transaction_id VARCHAR(36) PRIMARY KEY,
    credit_card_account_id VARCHAR(36) NOT NULL,
    from_account_id VARCHAR(36) NOT NULL,
    statement_id VARCHAR(36),
    repayment_amount NUMERIC(19, 4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    repayment_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_credit_repay_txn FOREIGN KEY (transaction_id) REFERENCES business_transaction (transaction_id),
    CONSTRAINT fk_credit_repay_account FOREIGN KEY (credit_card_account_id) REFERENCES credit_card_account (account_id),
    CONSTRAINT fk_credit_repay_from_account FOREIGN KEY (from_account_id) REFERENCES account (account_id),
    CONSTRAINT fk_credit_repay_statement FOREIGN KEY (statement_id) REFERENCES credit_card_statement (statement_id),
    CONSTRAINT fk_credit_repay_currency FOREIGN KEY (currency_code) REFERENCES currency (currency_code),
    CONSTRAINT ck_credit_repay_amount_positive CHECK (repayment_amount > 0)
);