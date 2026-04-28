-- Customer domain schema

CREATE TABLE customer (
    customer_id VARCHAR(36) PRIMARY KEY,
    id_type VARCHAR(32) NOT NULL,
    id_number VARCHAR(64) NOT NULL,
    issuing_region VARCHAR(32) NOT NULL,
    mobile_phone VARCHAR(32) NOT NULL,
    residential_address TEXT NOT NULL,
    mailing_address TEXT NOT NULL,
    is_us_tax_resident BOOLEAN NOT NULL DEFAULT FALSE,
    crs_info TEXT,
    customer_status VARCHAR(16) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_customer_identity UNIQUE (
        id_type,
        id_number,
        issuing_region
    ),
    CONSTRAINT ck_customer_status CHECK (
        customer_status IN ('ACTIVE', 'FROZEN', 'CLOSED')
    )
);

CREATE INDEX idx_customer_mobile_phone ON customer (mobile_phone);