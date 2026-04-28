package com.moesegfault.banking.application.account.query;

/**
 * @brief 客户账户列表查询请求（List Customer Accounts Query）；
 *        Query object for listing accounts of one customer.
 */
public final class ListCustomerAccountsQuery {

    /**
     * @brief 客户 ID（Customer ID），对齐 `account.customer_id`；
     *        Customer identifier aligned with `account.customer_id`.
     */
    private final String customerId;

    /**
     * @brief 是否包含已关闭账户（Include Closed Accounts）；
     *        Whether CLOSED accounts should be included.
     */
    private final boolean includeClosedAccounts;

    /**
     * @brief 构造客户账户列表查询（Construct List Customer Accounts Query）；
     *        Construct list-customer-accounts query.
     *
     * @param customerId           客户 ID（Customer ID）。
     * @param includeClosedAccounts 是否包含已关闭账户（Include closed accounts）。
     */
    public ListCustomerAccountsQuery(final String customerId, final boolean includeClosedAccounts) {
        this.customerId = normalize(customerId, "customerId");
        this.includeClosedAccounts = includeClosedAccounts;
    }

    /**
     * @brief 返回客户 ID（Return Customer ID）；
     *        Return customer identifier.
     *
     * @return 客户 ID（Customer ID）。
     */
    public String customerId() {
        return customerId;
    }

    /**
     * @brief 返回是否包含已关闭账户（Return Include Closed Flag）；
     *        Return include-closed-accounts flag.
     *
     * @return 包含关闭账户返回 true（true when closed accounts should be included）。
     */
    public boolean includeClosedAccounts() {
        return includeClosedAccounts;
    }

    /**
     * @brief 标准化字符串（Normalize String）；
     *        Normalize string value.
     *
     * @param raw   原始值（Raw value）。
     * @param field 字段名（Field name）。
     * @return 标准化值（Normalized value）。
     */
    private static String normalize(final String raw, final String field) {
        if (raw == null) {
            throw new IllegalArgumentException(field + " must not be null");
        }
        final String normalized = raw.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return normalized;
    }
}
