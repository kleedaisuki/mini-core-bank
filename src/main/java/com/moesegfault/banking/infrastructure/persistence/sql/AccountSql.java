package com.moesegfault.banking.infrastructure.persistence.sql;

/**
 * @brief 账户 SQL 常量（Account SQL Constants）；
 *        Centralized SQL constants for account persistence.
 */
public final class AccountSql {

        /**
         * @brief 账户 upsert SQL（Account Upsert SQL）；
         *        SQL for insert-or-update account record.
         */
        public static final String UPSERT_ACCOUNT = """
                        INSERT INTO account (
                            account_id,
                            customer_id,
                            account_no,
                            account_type,
                            account_status,
                            opened_at,
                            closed_at
                        ) VALUES (?, ?, ?, ?, ?, ?, ?)
                        ON CONFLICT (account_id) DO UPDATE SET
                            customer_id = EXCLUDED.customer_id,
                            account_no = EXCLUDED.account_no,
                            account_type = EXCLUDED.account_type,
                            account_status = EXCLUDED.account_status,
                            opened_at = EXCLUDED.opened_at,
                            closed_at = EXCLUDED.closed_at
                        """;

        /**
         * @brief 储蓄账户 upsert SQL（Savings Account Upsert SQL）；
         *        SQL for insert-if-absent savings account record.
         */
        public static final String UPSERT_SAVINGS = """
                        INSERT INTO savings_account (account_id)
                        VALUES (?)
                        ON CONFLICT (account_id) DO NOTHING
                        """;

        /**
         * @brief 外汇账户 upsert SQL（FX Account Upsert SQL）；
         *        SQL for insert-or-update FX account record.
         */
        public static final String UPSERT_FX = """
                        INSERT INTO fx_account (account_id, linked_savings_account_id)
                        VALUES (?, ?)
                        ON CONFLICT (account_id) DO UPDATE SET
                            linked_savings_account_id = EXCLUDED.linked_savings_account_id
                        """;

        /**
         * @brief 投资账户 upsert SQL（Investment Account Upsert SQL）；
         *        SQL for insert-if-absent investment account record.
         */
        public static final String UPSERT_INVESTMENT = """
                        INSERT INTO investment_account (account_id)
                        VALUES (?)
                        ON CONFLICT (account_id) DO NOTHING
                        """;

        /**
         * @brief 账户查询基础列（Account Select Columns）；
         *        Base select statement for account table.
         */
        public static final String SELECT_COLUMNS = """
                        SELECT
                            account_id,
                            customer_id,
                            account_no,
                            account_type,
                            account_status,
                            opened_at,
                            closed_at
                        FROM account
                        """;

        /**
         * @brief 查询外汇账户 SQL（Find FX Account SQL）；
         *        SQL for querying FX account with linked savings account id.
         */
        public static final String FIND_FX_ACCOUNT_BY_ID = """
                        SELECT
                            a.account_id,
                            a.customer_id,
                            a.account_no,
                            a.account_type,
                            a.account_status,
                            a.opened_at,
                            a.closed_at,
                            f.linked_savings_account_id
                        FROM account a
                        JOIN fx_account f ON f.account_id = a.account_id
                        WHERE a.account_id = ?
                          AND a.account_type = 'FX'
                        """;

        /**
         * @brief 账户号存在性检查 SQL（Exists by Account Number SQL）；
         *        SQL for checking account-number uniqueness.
         */
        public static final String EXISTS_BY_ACCOUNT_NUMBER = "SELECT EXISTS (SELECT 1 FROM account WHERE account_no = ?)";

        /**
         * @brief 统计客户投资账户数 SQL（Count Investment Accounts SQL）；
         *        SQL for counting investment accounts by customer id.
         */
        public static final String COUNT_INVESTMENT_BY_CUSTOMER = """
                        SELECT COUNT(1)
                        FROM investment_account i
                        JOIN account a ON a.account_id = i.account_id
                        WHERE a.customer_id = ?
                        """;

        /**
         * @brief 按 ID 查询储蓄账户 SQL（Find Savings Account by ID SQL）；
         *        SQL for querying savings account by account id.
         */
        public static final String FIND_SAVINGS_BY_ID = SELECT_COLUMNS
                        + " WHERE account_id = ? AND account_type = 'SAVINGS' AND EXISTS ("
                        + "SELECT 1 FROM savings_account s WHERE s.account_id = account.account_id)";

        /**
         * @brief 按 ID 查询投资账户 SQL（Find Investment Account by ID SQL）；
         *        SQL for querying investment account by account id.
         */
        public static final String FIND_INVESTMENT_BY_ID = SELECT_COLUMNS
                        + " WHERE account_id = ? AND account_type = 'INVESTMENT' AND EXISTS ("
                        + "SELECT 1 FROM investment_account i WHERE i.account_id = account.account_id)";

        /**
         * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
         *        Utility class should not be instantiated.
         */
        private AccountSql() {
        }
}
