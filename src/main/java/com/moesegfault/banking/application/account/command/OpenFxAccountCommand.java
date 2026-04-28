package com.moesegfault.banking.application.account.command;

/**
 * @brief 开外汇账户命令（Open FX Account Command）；
 *        Command object for opening an FX account.
 */
public final class OpenFxAccountCommand {

    /**
     * @brief 客户 ID（Customer ID），对齐 `account.customer_id`；
     *        Customer identifier aligned with `account.customer_id`.
     */
    private final String customerId;

    /**
     * @brief 外汇账户号（FX Account Number），对齐 `account.account_no`；
     *        FX account number aligned with `account.account_no`.
     */
    private final String accountNo;

    /**
     * @brief 绑定储蓄账户 ID（Linked Savings Account ID），对齐 `fx_account.linked_savings_account_id`；
     *        Linked savings-account identifier aligned with `fx_account.linked_savings_account_id`.
     */
    private final String linkedSavingsAccountId;

    /**
     * @brief 构造开外汇账户命令（Construct Open FX Account Command）；
     *        Construct open-FX-account command.
     *
     * @param customerId             客户 ID（Customer ID）。
     * @param accountNo              外汇账户号（FX account number）。
     * @param linkedSavingsAccountId 绑定储蓄账户 ID（Linked savings-account ID）。
     */
    public OpenFxAccountCommand(
            final String customerId,
            final String accountNo,
            final String linkedSavingsAccountId
    ) {
        this.customerId = normalize(customerId, "customerId");
        this.accountNo = normalize(accountNo, "accountNo");
        this.linkedSavingsAccountId = normalize(linkedSavingsAccountId, "linkedSavingsAccountId");
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
     * @brief 返回外汇账户号（Return FX Account Number）；
     *        Return FX account number.
     *
     * @return 外汇账户号（FX account number）。
     */
    public String accountNo() {
        return accountNo;
    }

    /**
     * @brief 返回绑定储蓄账户 ID（Return Linked Savings Account ID）；
     *        Return linked savings-account identifier.
     *
     * @return 绑定储蓄账户 ID（Linked savings-account ID）。
     */
    public String linkedSavingsAccountId() {
        return linkedSavingsAccountId;
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
