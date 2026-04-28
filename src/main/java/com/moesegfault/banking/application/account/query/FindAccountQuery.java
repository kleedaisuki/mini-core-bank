package com.moesegfault.banking.application.account.query;

/**
 * @brief 查询单个账户请求（Find Account Query）；
 *        Query object for finding a single account.
 */
public final class FindAccountQuery {

    /**
     * @brief 账户 ID（Account ID），可空；
     *        Account identifier, nullable.
     */
    private final String accountId;

    /**
     * @brief 账户号（Account Number），可空；
     *        Account number, nullable.
     */
    private final String accountNo;

    /**
     * @brief 构造查询对象（Construct Find Account Query）；
     *        Construct find-account query.
     *
     * @param accountId 账户 ID（Account ID, nullable）。
     * @param accountNo 账户号（Account number, nullable）。
     */
    private FindAccountQuery(final String accountId, final String accountNo) {
        this.accountId = normalizeNullable(accountId);
        this.accountNo = normalizeNullable(accountNo);
        final boolean hasAccountId = this.accountId != null;
        final boolean hasAccountNo = this.accountNo != null;
        if (hasAccountId == hasAccountNo) {
            throw new IllegalArgumentException("Exactly one of accountId or accountNo must be provided");
        }
    }

    /**
     * @brief 使用账户 ID 构建查询（Build Query by Account ID）；
     *        Build query by account ID.
     *
     * @param accountId 账户 ID（Account ID）。
     * @return 查询对象（Find-account query）。
     */
    public static FindAccountQuery byAccountId(final String accountId) {
        return new FindAccountQuery(accountId, null);
    }

    /**
     * @brief 使用账户号构建查询（Build Query by Account Number）；
     *        Build query by account number.
     *
     * @param accountNo 账户号（Account number）。
     * @return 查询对象（Find-account query）。
     */
    public static FindAccountQuery byAccountNo(final String accountNo) {
        return new FindAccountQuery(null, accountNo);
    }

    /**
     * @brief 返回账户 ID（Return Account ID）；
     *        Return account identifier.
     *
     * @return 账户 ID（Account ID, nullable）。
     */
    public String accountId() {
        return accountId;
    }

    /**
     * @brief 返回账户号（Return Account Number）；
     *        Return account number.
     *
     * @return 账户号（Account number, nullable）。
     */
    public String accountNo() {
        return accountNo;
    }

    /**
     * @brief 标准化可空字符串（Normalize Nullable String）；
     *        Normalize nullable string value.
     *
     * @param raw 原始值（Raw value）。
     * @return 标准化值或 null（Normalized value or null）。
     */
    private static String normalizeNullable(final String raw) {
        if (raw == null) {
            return null;
        }
        final String normalized = raw.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
