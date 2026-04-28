package com.moesegfault.banking.application.ledger.query;

/**
 * @brief 分录列表查询请求（List Ledger Entries Query），按账户读取最近分录；
 *        Ledger-entry list query request for reading recent account entries.
 *
 * @param accountId 账户 ID（Account ID）。
 * @param limit     返回条数上限（Maximum number of returned entries）。
 */
public record ListLedgerEntriesQuery(
        String accountId,
        int limit
) {

    /**
     * @brief 默认返回条数（Default Query Limit）；
     *        Default limit used by convenience constructor.
     */
    public static final int DEFAULT_LIMIT = 50;

    /**
     * @brief 仅按账户构造查询（Construct Query with Default Limit）；
     *        Construct query with default entry limit.
     *
     * @param accountId 账户 ID（Account ID）。
     */
    public ListLedgerEntriesQuery(final String accountId) {
        this(accountId, DEFAULT_LIMIT);
    }

    /**
     * @brief 紧凑构造并校验查询参数（Compact Constructor with Validation）；
     *        Compact constructor validating query parameters.
     */
    public ListLedgerEntriesQuery {
        accountId = normalizeRequiredId(accountId, "Account ID");
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
    }

    /**
     * @brief 标准化并校验必填标识（Normalize Required Identifier）；
     *        Normalize and validate required identifier.
     *
     * @param rawValue 原始值（Raw value）。
     * @param label    字段标签（Field label）。
     * @return 标准化标识（Normalized identifier）。
     */
    private static String normalizeRequiredId(final String rawValue, final String label) {
        if (rawValue == null) {
            throw new IllegalArgumentException(label + " must not be null");
        }
        final String normalized = rawValue.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
        return normalized;
    }
}
