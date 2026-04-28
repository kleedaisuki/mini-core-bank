package com.moesegfault.banking.application.account.result;

import com.moesegfault.banking.domain.account.Account;
import com.moesegfault.banking.domain.account.SavingsAccountId;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 开户结果（Open Account Result），用于返回开户后的核心状态快照；
 *        Opening result snapshot returned by account-opening use cases.
 */
public final class OpenAccountResult {

    /**
     * @brief 账户 ID（Account ID），对齐 `account.account_id`；
     *        Account identifier aligned with `account.account_id`.
     */
    private final String accountId;

    /**
     * @brief 客户 ID（Customer ID），对齐 `account.customer_id`；
     *        Customer identifier aligned with `account.customer_id`.
     */
    private final String customerId;

    /**
     * @brief 账户号（Account Number），对齐 `account.account_no`；
     *        Account number aligned with `account.account_no`.
     */
    private final String accountNo;

    /**
     * @brief 账户类型（Account Type），对齐 `account.account_type`；
     *        Account type aligned with `account.account_type`.
     */
    private final String accountType;

    /**
     * @brief 账户状态（Account Status），对齐 `account.account_status`；
     *        Account status aligned with `account.account_status`.
     */
    private final String accountStatus;

    /**
     * @brief 开户时间（Opened Timestamp），对齐 `account.opened_at`；
     *        Opened timestamp aligned with `account.opened_at`.
     */
    private final Instant openedAt;

    /**
     * @brief 绑定储蓄账户 ID（Linked Savings Account ID），仅 FX 账户有值；
     *        Linked savings-account ID, present only for FX accounts.
     */
    private final String linkedSavingsAccountId;

    /**
     * @brief 构造开户结果（Construct Open Account Result）；
     *        Construct open-account result.
     *
     * @param accountId               账户 ID（Account ID）。
     * @param customerId              客户 ID（Customer ID）。
     * @param accountNo               账户号（Account number）。
     * @param accountType             账户类型（Account type）。
     * @param accountStatus           账户状态（Account status）。
     * @param openedAt                开户时间（Opened timestamp）。
     * @param linkedSavingsAccountId 绑定储蓄账户 ID（Linked savings-account ID, nullable）。
     */
    public OpenAccountResult(
            final String accountId,
            final String customerId,
            final String accountNo,
            final String accountType,
            final String accountStatus,
            final Instant openedAt,
            final String linkedSavingsAccountId
    ) {
        this.accountId = normalize(accountId, "accountId");
        this.customerId = normalize(customerId, "customerId");
        this.accountNo = normalize(accountNo, "accountNo");
        this.accountType = normalize(accountType, "accountType");
        this.accountStatus = normalize(accountStatus, "accountStatus");
        this.openedAt = Objects.requireNonNull(openedAt, "openedAt must not be null");
        this.linkedSavingsAccountId = normalizeNullable(linkedSavingsAccountId);
    }

    /**
     * @brief 从基础账户构建开户结果（Build Open Result from Base Account）；
     *        Build opening result from base account.
     *
     * @param account 账户实体（Account entity）。
     * @return 开户结果（Open-account result）。
     */
    public static OpenAccountResult from(final Account account) {
        return from(account, null);
    }

    /**
     * @brief 从账户与绑定储蓄账户构建开户结果（Build Open Result with Linked Savings Account）；
     *        Build opening result with linked savings-account id.
     *
     * @param account                 账户实体（Account entity）。
     * @param linkedSavingsAccountId 绑定储蓄账户 ID（Linked savings-account ID, nullable）。
     * @return 开户结果（Open-account result）。
     */
    public static OpenAccountResult from(final Account account, final SavingsAccountId linkedSavingsAccountId) {
        final Account normalized = Objects.requireNonNull(account, "account must not be null");
        return new OpenAccountResult(
                normalized.accountId().value(),
                normalized.customerId().value(),
                normalized.accountNo().value(),
                normalized.accountType().name(),
                normalized.accountStatus().name(),
                normalized.openedAt(),
                linkedSavingsAccountId == null ? null : linkedSavingsAccountId.value());
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
     * @brief 返回客户 ID（Return Customer ID）；
     *        Return customer identifier.
     *
     * @return 客户 ID（Customer ID）。
     */
    public String customerId() {
        return customerId;
    }

    /**
     * @brief 返回账户号（Return Account Number）；
     *        Return account number.
     *
     * @return 账户号（Account number）。
     */
    public String accountNo() {
        return accountNo;
    }

    /**
     * @brief 返回账户类型（Return Account Type）；
     *        Return account type.
     *
     * @return 账户类型（Account type）。
     */
    public String accountType() {
        return accountType;
    }

    /**
     * @brief 返回账户状态（Return Account Status）；
     *        Return account status.
     *
     * @return 账户状态（Account status）。
     */
    public String accountStatus() {
        return accountStatus;
    }

    /**
     * @brief 返回开户时间（Return Opened Timestamp）；
     *        Return opened timestamp.
     *
     * @return 开户时间（Opened timestamp）。
     */
    public Instant openedAt() {
        return openedAt;
    }

    /**
     * @brief 返回绑定储蓄账户 ID（Return Linked Savings Account ID）；
     *        Return linked savings-account identifier.
     *
     * @return 绑定储蓄账户 ID（Linked savings-account ID, nullable）。
     */
    public String linkedSavingsAccountId() {
        return linkedSavingsAccountId;
    }

    /**
     * @brief 标准化非空字符串（Normalize Required String）；
     *        Normalize required string value.
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
