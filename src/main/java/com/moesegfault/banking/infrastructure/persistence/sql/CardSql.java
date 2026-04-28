package com.moesegfault.banking.infrastructure.persistence.sql;

/**
 * @brief 卡片 SQL 常量（Card SQL Constants）；
 *        Centralized SQL constants for card persistence.
 */
public final class CardSql {

    /**
     * @brief 借记卡 upsert SQL（Debit Card Upsert SQL）；
     *        SQL for insert-or-update debit card record.
     */
    public static final String UPSERT_DEBIT = """
            INSERT INTO debit_card (
                card_id,
                card_no,
                holder_customer_id,
                savings_account_id,
                fx_account_id,
                card_status,
                issued_at,
                expired_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (card_id) DO UPDATE SET
                card_no = EXCLUDED.card_no,
                holder_customer_id = EXCLUDED.holder_customer_id,
                savings_account_id = EXCLUDED.savings_account_id,
                fx_account_id = EXCLUDED.fx_account_id,
                card_status = EXCLUDED.card_status,
                issued_at = EXCLUDED.issued_at,
                expired_at = EXCLUDED.expired_at
            """;

    /**
     * @brief 借记附属卡 upsert SQL（Supplementary Debit Card Upsert SQL）；
     *        SQL for insert-or-update supplementary debit card record.
     */
    public static final String UPSERT_SUPPLEMENTARY_DEBIT = """
            INSERT INTO supplementary_debit_card (
                supplementary_card_id,
                card_no,
                holder_customer_id,
                primary_debit_card_id,
                card_status,
                issued_at,
                expired_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (supplementary_card_id) DO UPDATE SET
                card_no = EXCLUDED.card_no,
                holder_customer_id = EXCLUDED.holder_customer_id,
                primary_debit_card_id = EXCLUDED.primary_debit_card_id,
                card_status = EXCLUDED.card_status,
                issued_at = EXCLUDED.issued_at,
                expired_at = EXCLUDED.expired_at
            """;

    /**
     * @brief 信用卡 upsert SQL（Credit Card Upsert SQL）；
     *        SQL for insert-or-update credit card record.
     */
    public static final String UPSERT_CREDIT = """
            INSERT INTO credit_card (
                credit_card_id,
                card_no,
                holder_customer_id,
                credit_card_account_id,
                card_role,
                primary_credit_card_id,
                card_status,
                issued_at,
                expired_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (credit_card_id) DO UPDATE SET
                card_no = EXCLUDED.card_no,
                holder_customer_id = EXCLUDED.holder_customer_id,
                credit_card_account_id = EXCLUDED.credit_card_account_id,
                card_role = EXCLUDED.card_role,
                primary_credit_card_id = EXCLUDED.primary_credit_card_id,
                card_status = EXCLUDED.card_status,
                issued_at = EXCLUDED.issued_at,
                expired_at = EXCLUDED.expired_at
            """;

    /**
     * @brief 任意卡号存在性检查 SQL（Exists Any by Card Number SQL）；
     *        SQL for checking card number across three card tables.
     */
    public static final String EXISTS_ANY_BY_CARD_NUMBER = """
            SELECT EXISTS (
                SELECT 1 FROM debit_card WHERE card_no = ?
                UNION ALL
                SELECT 1 FROM supplementary_debit_card WHERE card_no = ?
                UNION ALL
                SELECT 1 FROM credit_card WHERE card_no = ?
            )
            """;

    /**
     * @brief 主借记卡下附属卡计数 SQL（Count Supplementary Debit Cards SQL）；
     *        SQL for counting supplementary debit cards by primary card id.
     */
    public static final String COUNT_SUPPLEMENTARY_BY_PRIMARY =
            "SELECT COUNT(1) FROM supplementary_debit_card WHERE primary_debit_card_id = ?";

    /**
     * @brief 按借记卡 ID 查询 SQL（Find Debit Card by ID SQL）；
     *        SQL for finding debit card by card id.
     */
    public static final String FIND_DEBIT_BY_ID = "SELECT * FROM debit_card WHERE card_id = ?";

    /**
     * @brief 按借记附属卡 ID 查询 SQL（Find Supplementary Debit Card by ID SQL）；
     *        SQL for finding supplementary debit card by id.
     */
    public static final String FIND_SUPPLEMENTARY_DEBIT_BY_ID =
            "SELECT * FROM supplementary_debit_card WHERE supplementary_card_id = ?";

    /**
     * @brief 按信用卡 ID 查询 SQL（Find Credit Card by ID SQL）；
     *        SQL for finding credit card by id.
     */
    public static final String FIND_CREDIT_BY_ID = "SELECT * FROM credit_card WHERE credit_card_id = ?";

    /**
     * @brief 按借记卡号查询 SQL（Find Debit Card by Card Number SQL）；
     *        SQL for finding debit card by card number.
     */
    public static final String FIND_DEBIT_BY_CARD_NUMBER = "SELECT * FROM debit_card WHERE card_no = ?";

    /**
     * @brief 按信用卡号查询 SQL（Find Credit Card by Card Number SQL）；
     *        SQL for finding credit card by card number.
     */
    public static final String FIND_CREDIT_BY_CARD_NUMBER = "SELECT * FROM credit_card WHERE card_no = ?";

    /**
     * @brief 按账户查询信用卡列表 SQL（Find Credit Cards by Account ID SQL）；
     *        SQL for finding credit cards by credit account id.
     */
    public static final String FIND_CREDIT_BY_ACCOUNT_ID =
            "SELECT * FROM credit_card WHERE credit_card_account_id = ? ORDER BY issued_at DESC";

    /**
     * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
     *        Utility class should not be instantiated.
     */
    private CardSql() {
    }
}
