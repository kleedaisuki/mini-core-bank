-- Investment domain schema

CREATE TABLE investment_product (
    product_id VARCHAR(36) PRIMARY KEY,
    product_code VARCHAR(32) NOT NULL,
    product_name VARCHAR(128) NOT NULL,
    product_type VARCHAR(32) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    risk_level VARCHAR(16) NOT NULL,
    issuer VARCHAR(128) NOT NULL,
    status VARCHAR(16) NOT NULL,
    CONSTRAINT uq_investment_product_code UNIQUE (product_code),
    CONSTRAINT fk_investment_product_currency FOREIGN KEY (currency_code) REFERENCES currency (currency_code),
    CONSTRAINT ck_investment_product_status CHECK (
        status IN (
            'ACTIVE',
            'INACTIVE',
            'CLOSED'
        )
    )
);

CREATE TABLE investment_holding (
    holding_id VARCHAR(36) PRIMARY KEY,
    investment_account_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    quantity NUMERIC(19, 6) NOT NULL,
    average_cost NUMERIC(19, 6) NOT NULL,
    cost_currency_code VARCHAR(3) NOT NULL,
    market_value NUMERIC(19, 6) NOT NULL,
    valuation_currency_code VARCHAR(3) NOT NULL,
    unrealized_pnl NUMERIC(19, 6) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_holding_account FOREIGN KEY (investment_account_id) REFERENCES investment_account (account_id),
    CONSTRAINT fk_holding_product FOREIGN KEY (product_id) REFERENCES investment_product (product_id),
    CONSTRAINT fk_holding_cost_currency FOREIGN KEY (cost_currency_code) REFERENCES currency (currency_code),
    CONSTRAINT fk_holding_valuation_currency FOREIGN KEY (valuation_currency_code) REFERENCES currency (currency_code),
    CONSTRAINT uq_holding_by_product UNIQUE (
        investment_account_id,
        product_id
    ),
    CONSTRAINT ck_holding_quantity_non_negative CHECK (quantity >= 0),
    CONSTRAINT ck_holding_average_cost_non_negative CHECK (average_cost >= 0),
    CONSTRAINT ck_holding_market_value_non_negative CHECK (market_value >= 0)
);

CREATE TABLE product_valuation (
    product_id VARCHAR(36) NOT NULL,
    valuation_date DATE NOT NULL,
    nav NUMERIC(19, 6) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    CONSTRAINT pk_product_valuation PRIMARY KEY (product_id, valuation_date),
    CONSTRAINT fk_valuation_product FOREIGN KEY (product_id) REFERENCES investment_product (product_id),
    CONSTRAINT fk_valuation_currency FOREIGN KEY (currency_code) REFERENCES currency (currency_code),
    CONSTRAINT ck_valuation_nav_positive CHECK (nav > 0)
);

CREATE INDEX idx_investment_holding_account_id ON investment_holding (investment_account_id);