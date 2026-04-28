package com.moesegfault.banking.infrastructure.persistence.sql;

/**
 * @brief 账务 SQL 常量（Ledger SQL Constants）；
 *        Centralized SQL constants for ledger persistence.
 */
public final class LedgerSql {

    /**
     * @brief 余额 upsert SQL（Balance Upsert SQL）；
     *        SQL for insert-or-update account balance record.
     */
    public static final String UPSERT_BALANCE = """
            INSERT INTO account_balance (
                account_id,
                currency_code,
                ledger_balance,
                available_balance,
                updated_at
            ) VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (account_id, currency_code) DO UPDATE SET
                ledger_balance = EXCLUDED.ledger_balance,
                available_balance = EXCLUDED.available_balance,
                updated_at = EXCLUDED.updated_at
            """;

    /**
     * @brief 分录 upsert SQL（Ledger Entry Upsert SQL）；
     *        SQL for insert-or-update account entry record.
     */
    public static final String UPSERT_ENTRY = """
            INSERT INTO account_entry (
                entry_id,
                transaction_id,
                batch_id,
                account_id,
                currency_code,
                entry_direction,
                amount,
                ledger_balance_after,
                available_balance_after,
                entry_type,
                posted_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (entry_id) DO UPDATE SET
                transaction_id = EXCLUDED.transaction_id,
                batch_id = EXCLUDED.batch_id,
                account_id = EXCLUDED.account_id,
                currency_code = EXCLUDED.currency_code,
                entry_direction = EXCLUDED.entry_direction,
                amount = EXCLUDED.amount,
                ledger_balance_after = EXCLUDED.ledger_balance_after,
                available_balance_after = EXCLUDED.available_balance_after,
                entry_type = EXCLUDED.entry_type,
                posted_at = EXCLUDED.posted_at
            """;

    /**
     * @brief 批次 upsert SQL（Posting Batch Upsert SQL）；
     *        SQL for insert-or-update posting batch record.
     */
    public static final String UPSERT_BATCH = """
            INSERT INTO posting_batch (
                batch_id,
                transaction_id,
                idempotency_key,
                batch_status,
                posted_at,
                created_at
            ) VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (batch_id) DO UPDATE SET
                transaction_id = EXCLUDED.transaction_id,
                idempotency_key = EXCLUDED.idempotency_key,
                batch_status = EXCLUDED.batch_status,
                posted_at = EXCLUDED.posted_at,
                created_at = EXCLUDED.created_at
            """;

    /**
     * @brief 查询余额 SQL（Find Balance SQL）；
     *        SQL for finding single account balance by account and currency.
     */
    public static final String FIND_BALANCE = "SELECT * FROM account_balance WHERE account_id = ? AND currency_code = ?";

    /**
     * @brief 按账户列出余额 SQL（List Balances by Account ID SQL）；
     *        SQL for listing balances under an account.
     */
    public static final String LIST_BALANCES_BY_ACCOUNT_ID = "SELECT * FROM account_balance WHERE account_id = ? ORDER BY currency_code";

    /**
     * @brief 按交易列出分录 SQL（List Entries by Transaction ID SQL）；
     *        SQL for listing entries by transaction id.
     */
    public static final String LIST_ENTRIES_BY_TRANSACTION_ID = "SELECT * FROM account_entry WHERE transaction_id = ? ORDER BY posted_at ASC, entry_id ASC";

    /**
     * @brief 按账户查询最近分录 SQL（List Recent Entries by Account ID SQL）；
     *        SQL for listing latest entries by account id with limit.
     */
    public static final String LIST_RECENT_ENTRIES_BY_ACCOUNT_ID = "SELECT * FROM account_entry WHERE account_id = ? ORDER BY posted_at DESC, entry_id DESC LIMIT ?";

    /**
     * @brief 按批次查询分录 SQL（List Entries by Batch ID SQL）；
     *        SQL for listing entries by posting batch id.
     */
    public static final String LIST_ENTRIES_BY_BATCH_ID = "SELECT * FROM account_entry WHERE batch_id = ? ORDER BY posted_at ASC, entry_id ASC";

    /**
     * @brief 批次查询前缀 SQL（Posting Batch Select Prefix SQL）；
     *        SQL prefix for posting batch conditional lookup.
     */
    public static final String FIND_POSTING_BATCH_PREFIX = "SELECT * FROM posting_batch WHERE ";

    /**
     * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
     *        Utility class should not be instantiated.
     */
    private LedgerSql() {
    }
}
