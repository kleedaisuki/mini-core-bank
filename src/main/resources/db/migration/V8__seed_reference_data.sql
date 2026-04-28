-- Seed reference data

INSERT INTO
    currency (
        currency_code,
        currency_name,
        minor_unit,
        is_active
    )
VALUES ('USD', 'US Dollar', 2, TRUE),
    (
        'CNH',
        'Offshore Renminbi',
        2,
        TRUE
    ),
    ('EUR', 'Euro', 2, TRUE),
    (
        'JPY',
        'Japanese Yen',
        0,
        TRUE
    ),
    (
        'SGD',
        'Singapore Dollar',
        2,
        TRUE
    ),
    (
        'HKD',
        'Hong Kong Dollar',
        2,
        TRUE
    ),
    ('KRW', 'Korean Won', 0, TRUE),
    (
        'AUD',
        'Australian Dollar',
        2,
        TRUE
    ),
    (
        'GBP',
        'Pound Sterling',
        2,
        TRUE
    ),
    (
        'CAD',
        'Canadian Dollar',
        2,
        TRUE
    ) ON CONFLICT (currency_code) DO
UPDATE
SET
    currency_name = EXCLUDED.currency_name,
    minor_unit = EXCLUDED.minor_unit,
    is_active = EXCLUDED.is_active;

INSERT INTO
    business_type (
        business_type_code,
        business_category,
        business_name,
        description,
        is_financial,
        is_reversible,
        status
    )
VALUES (
        'OPEN_SAVINGS_ACCOUNT',
        'ACCOUNT',
        'Open savings account',
        'Create a savings account for customer profile setup',
        FALSE,
        FALSE,
        'ACTIVE'
    ),
    (
        'OPEN_FX_ACCOUNT',
        'ACCOUNT',
        'Open FX account',
        'Create a linked FX account under existing savings account',
        FALSE,
        FALSE,
        'ACTIVE'
    ),
    (
        'OPEN_INVESTMENT_ACCOUNT',
        'ACCOUNT',
        'Open investment account',
        'Create an investment account container',
        FALSE,
        FALSE,
        'ACTIVE'
    ),
    (
        'OPEN_DEBIT_CARD',
        'CARD',
        'Issue debit card',
        'Issue primary debit card linked to savings and FX accounts',
        FALSE,
        FALSE,
        'ACTIVE'
    ),
    (
        'OPEN_SUPPLEMENTARY_DEBIT_CARD',
        'CARD',
        'Issue supplementary debit card',
        'Issue supplementary debit card under primary debit card',
        FALSE,
        FALSE,
        'ACTIVE'
    ),
    (
        'OPEN_CREDIT_CARD',
        'CARD',
        'Issue credit card',
        'Issue primary credit card and credit account',
        FALSE,
        FALSE,
        'ACTIVE'
    ),
    (
        'OPEN_SUPPLEMENTARY_CREDIT_CARD',
        'CARD',
        'Issue supplementary credit card',
        'Issue supplementary credit card under primary credit card',
        FALSE,
        FALSE,
        'ACTIVE'
    ),
    (
        'TRANSFER_INTERNAL',
        'TRANSFER',
        'Internal transfer',
        'Transfer between internal accounts',
        TRUE,
        TRUE,
        'ACTIVE'
    ),
    (
        'FX_EXCHANGE',
        'TRANSFER',
        'FX exchange',
        'Cross-currency transfer and exchange',
        TRUE,
        TRUE,
        'ACTIVE'
    ),
    (
        'BUY_PRODUCT',
        'INVESTMENT',
        'Buy investment product',
        'Buy a product and increase holding',
        TRUE,
        TRUE,
        'ACTIVE'
    ),
    (
        'SELL_PRODUCT',
        'INVESTMENT',
        'Sell investment product',
        'Sell or redeem product holding',
        TRUE,
        TRUE,
        'ACTIVE'
    ),
    (
        'CREDIT_CARD_PURCHASE',
        'CREDIT_CARD',
        'Credit card purchase',
        'Post a credit card purchase transaction',
        TRUE,
        TRUE,
        'ACTIVE'
    ),
    (
        'CREDIT_CARD_REFUND',
        'CREDIT_CARD',
        'Credit card refund',
        'Post a credit card refund transaction',
        TRUE,
        TRUE,
        'ACTIVE'
    ),
    (
        'CREDIT_CARD_REPAYMENT',
        'CREDIT_CARD',
        'Credit card repayment',
        'Repay outstanding credit card statement',
        TRUE,
        TRUE,
        'ACTIVE'
    ),
    (
        'CREDIT_CARD_FEE',
        'CREDIT_CARD',
        'Credit card fee',
        'Post annual fee or service fee',
        TRUE,
        TRUE,
        'ACTIVE'
    ),
    (
        'CREDIT_CARD_INTEREST',
        'CREDIT_CARD',
        'Credit card interest',
        'Post accrued credit card interest',
        TRUE,
        TRUE,
        'ACTIVE'
    ) ON CONFLICT (business_type_code) DO
UPDATE
SET
    business_category = EXCLUDED.business_category,
    business_name = EXCLUDED.business_name,
    description = EXCLUDED.description,
    is_financial = EXCLUDED.is_financial,
    is_reversible = EXCLUDED.is_reversible,
    status = EXCLUDED.status;