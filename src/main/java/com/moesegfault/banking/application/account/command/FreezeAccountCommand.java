package com.moesegfault.banking.application.account.command;

/**
 * @brief 冻结账户命令（Freeze Account Command）；
 *        Command object for freezing an account.
 */
public final class FreezeAccountCommand {

    /**
     * @brief 账户 ID（Account ID），对齐 `account.account_id`；
     *        Account identifier aligned with `account.account_id`.
     */
    private final String accountId;

    /**
     * @brief 冻结原因（Freeze Reason），用于审计扩展；
     *        Freeze reason reserved for future audit extensibility.
     */
    private final String freezeReason;

    /**
     * @brief 构造冻结账户命令（Construct Freeze Account Command）；
     *        Construct freeze-account command.
     *
     * @param accountId    账户 ID（Account ID）。
     * @param freezeReason 冻结原因（Freeze reason）。
     */
    public FreezeAccountCommand(final String accountId, final String freezeReason) {
        this.accountId = normalize(accountId, "accountId");
        this.freezeReason = normalize(freezeReason, "freezeReason");
    }

    /**
     * @brief 返回账户 ID（Return Account ID）；
     *        Return account identifier.
     *
     * @return 账户 ID（Account ID）。
     */
    public String accountId() {
        return accountId;
    }

    /**
     * @brief 返回冻结原因（Return Freeze Reason）；
     *        Return freeze reason.
     *
     * @return 冻结原因（Freeze reason）。
     */
    public String freezeReason() {
        return freezeReason;
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
