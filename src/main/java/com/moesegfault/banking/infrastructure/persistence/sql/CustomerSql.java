package com.moesegfault.banking.infrastructure.persistence.sql;

/**
 * @brief 客户 SQL 常量（Customer SQL Constants）；
 *        Centralized SQL constants for customer persistence.
 */
public final class CustomerSql {

    /**
     * @brief 客户 upsert SQL（Customer Upsert SQL）；
     *        SQL for insert-or-update customer record.
     */
    public static final String UPSERT = """
            INSERT INTO customer (
                customer_id,
                id_type,
                id_number,
                issuing_region,
                mobile_phone,
                residential_address,
                mailing_address,
                is_us_tax_resident,
                crs_info,
                customer_status,
                created_at,
                updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (customer_id) DO UPDATE SET
                id_type = EXCLUDED.id_type,
                id_number = EXCLUDED.id_number,
                issuing_region = EXCLUDED.issuing_region,
                mobile_phone = EXCLUDED.mobile_phone,
                residential_address = EXCLUDED.residential_address,
                mailing_address = EXCLUDED.mailing_address,
                is_us_tax_resident = EXCLUDED.is_us_tax_resident,
                crs_info = EXCLUDED.crs_info,
                customer_status = EXCLUDED.customer_status,
                updated_at = EXCLUDED.updated_at
            """;

    /**
     * @brief 客户查询基础列（Customer Select Columns）；
     *        Base select statement for customer table.
     */
    public static final String SELECT_COLUMNS = """
            SELECT
                customer_id,
                id_type,
                id_number,
                issuing_region,
                mobile_phone,
                residential_address,
                mailing_address,
                is_us_tax_resident,
                crs_info,
                customer_status,
                created_at,
                updated_at
            FROM customer
            """;

    /**
     * @brief 按证件唯一性检查 SQL（Exists by Identity Document SQL）；
     *        SQL for checking customer existence by identity document.
     */
    public static final String EXISTS_BY_IDENTITY_DOCUMENT = """
            SELECT EXISTS (
                SELECT 1
                FROM customer
                WHERE id_type = ? AND id_number = ? AND issuing_region = ?
            )
            """;

    /**
     * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
     *        Utility class should not be instantiated.
     */
    private CustomerSql() {
    }
}
