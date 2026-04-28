package com.moesegfault.banking.infrastructure.persistence.sql;

/**
 * @brief 信用 SQL 常量（Credit SQL Constants）；
 *        Centralized SQL constants for credit persistence.
 */
public final class CreditSql {

    /**
     * @brief 信用账户 upsert SQL（Credit Account Upsert SQL）；
     *        SQL for insert-or-update credit card account record.
     */
    public static final String UPSERT_ACCOUNT = """
            INSERT INTO credit_card_account (
                account_id,
                credit_limit,
                available_credit,
                billing_cycle_day,
                payment_due_day,
                interest_rate,
                cash_advance_limit,
                account_currency_code
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (account_id) DO UPDATE SET
                credit_limit = EXCLUDED.credit_limit,
                available_credit = EXCLUDED.available_credit,
                billing_cycle_day = EXCLUDED.billing_cycle_day,
                payment_due_day = EXCLUDED.payment_due_day,
                interest_rate = EXCLUDED.interest_rate,
                cash_advance_limit = EXCLUDED.cash_advance_limit,
                account_currency_code = EXCLUDED.account_currency_code
            """;

    /**
     * @brief 信用账单 upsert SQL（Credit Statement Upsert SQL）；
     *        SQL for insert-or-update credit card statement record.
     */
    public static final String UPSERT_STATEMENT = """
            INSERT INTO credit_card_statement (
                statement_id,
                credit_card_account_id,
                statement_period_start,
                statement_period_end,
                statement_date,
                payment_due_date,
                total_amount_due,
                minimum_amount_due,
                paid_amount,
                statement_status,
                currency_code
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (statement_id) DO UPDATE SET
                credit_card_account_id = EXCLUDED.credit_card_account_id,
                statement_period_start = EXCLUDED.statement_period_start,
                statement_period_end = EXCLUDED.statement_period_end,
                statement_date = EXCLUDED.statement_date,
                payment_due_date = EXCLUDED.payment_due_date,
                total_amount_due = EXCLUDED.total_amount_due,
                minimum_amount_due = EXCLUDED.minimum_amount_due,
                paid_amount = EXCLUDED.paid_amount,
                statement_status = EXCLUDED.statement_status,
                currency_code = EXCLUDED.currency_code
            """;

    /**
     * @brief 按账期查询账单 SQL（Find Statement by Period SQL）；
     *        SQL for finding statement by account id and statement period.
     */
    public static final String FIND_STATEMENT_BY_PERIOD = """
            SELECT *
            FROM credit_card_statement
            WHERE credit_card_account_id = ?
              AND statement_period_start = ?
              AND statement_period_end = ?
            """;

    /**
     * @brief 查询可还款账单列表 SQL（List Repayable Statements SQL）；
     *        SQL for listing open or overdue statements by account id.
     */
    public static final String LIST_REPAYABLE_BY_ACCOUNT_ID = """
            SELECT *
            FROM credit_card_statement
            WHERE credit_card_account_id = ?
              AND statement_status IN ('OPEN', 'OVERDUE')
            ORDER BY statement_date ASC
            """;

    /**
     * @brief 按 ID 查询信用账户 SQL（Find Credit Account by ID SQL）；
     *        SQL for finding credit card account by account id.
     */
    public static final String FIND_ACCOUNT_BY_ID = "SELECT * FROM credit_card_account WHERE account_id = ?";

    /**
     * @brief 按 ID 查询信用账单 SQL（Find Credit Statement by ID SQL）；
     *        SQL for finding credit card statement by statement id.
     */
    public static final String FIND_STATEMENT_BY_ID = "SELECT * FROM credit_card_statement WHERE statement_id = ?";

    /**
     * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
     *        Utility class should not be instantiated.
     */
    private CreditSql() {
    }
}
