package com.moesegfault.banking.application.account.command;

/**
 * @brief 开投资账户命令（Open Investment Account Command）；
 *        Command object for opening an investment account.
 */
public final class OpenInvestmentAccountCommand {

    /**
     * @brief 客户 ID（Customer ID），对齐 `account.customer_id`；
     *        Customer identifier aligned with `account.customer_id`.
     */
    private final String customerId;

    /**
     * @brief 投资账户号（Investment Account Number），对齐 `account.account_no`；
     *        Investment account number aligned with `account.account_no`.
     */
    private final String accountNo;

    /**
     * @brief 构造开投资账户命令（Construct Open Investment Account Command）；
     *        Construct open-investment-account command.
     *
     * @param customerId 客户 ID（Customer ID）。
     * @param accountNo  投资账户号（Investment account number）。
     */
    public OpenInvestmentAccountCommand(final String customerId, final String accountNo) {
        this.customerId = normalize(customerId, "customerId");
        this.accountNo = normalize(accountNo, "accountNo");
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
     * @brief 返回投资账户号（Return Investment Account Number）；
     *        Return investment account number.
     *
     * @return 投资账户号（Investment account number）。
     */
    public String accountNo() {
        return accountNo;
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
