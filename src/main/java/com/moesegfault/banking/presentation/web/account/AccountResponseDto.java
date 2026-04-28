package com.moesegfault.banking.presentation.web.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.application.account.result.OpenAccountResult;
import java.util.Objects;

/**
 * @brief 账户响应 DTO（Account Response DTO），统一账户详情和开户结果的输出 schema；
 *        Account-response DTO standardizing schema for account detail and opening result.
 */
public record AccountResponseDto(
        @JsonProperty("account_id") String accountId,
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("account_no") String accountNo,
        @JsonProperty("account_type") String accountType,
        @JsonProperty("account_status") String accountStatus,
        @JsonProperty("opened_at") String openedAt,
        @JsonProperty("closed_at") String closedAt,
        @JsonProperty("linked_savings_account_id") String linkedSavingsAccountId
) {

    /**
     * @brief 规范化并校验账户响应（Normalize and Validate Account Response）；
     *        Normalize and validate account-response fields.
     */
    public AccountResponseDto {
        accountId = normalizeRequiredText(accountId, "accountId");
        customerId = normalizeRequiredText(customerId, "customerId");
        accountNo = normalizeRequiredText(accountNo, "accountNo");
        accountType = normalizeRequiredText(accountType, "accountType");
        accountStatus = normalizeRequiredText(accountStatus, "accountStatus");
        openedAt = normalizeRequiredText(openedAt, "openedAt");
        closedAt = normalizeNullableText(closedAt);
        linkedSavingsAccountId = normalizeNullableText(linkedSavingsAccountId);
    }

    /**
     * @brief 从开户结果构建响应（Build Response from Open-Account Result）；
     *        Build account response from open-account result.
     *
     * @param result 开户结果（Open-account result）。
     * @return 账户响应（Account response DTO）。
     */
    public static AccountResponseDto from(final OpenAccountResult result) {
        final OpenAccountResult normalizedResult = Objects.requireNonNull(result, "result must not be null");
        return new AccountResponseDto(
                normalizedResult.accountId(),
                normalizedResult.customerId(),
                normalizedResult.accountNo(),
                normalizedResult.accountType(),
                normalizedResult.accountStatus(),
                normalizedResult.openedAt().toString(),
                null,
                normalizedResult.linkedSavingsAccountId());
    }

    /**
     * @brief 从账户结果构建响应（Build Response from Account Result）；
     *        Build account response from account result.
     *
     * @param result 账户结果（Account result）。
     * @return 账户响应（Account response DTO）。
     */
    public static AccountResponseDto from(final AccountResult result) {
        final AccountResult normalizedResult = Objects.requireNonNull(result, "result must not be null");
        return new AccountResponseDto(
                normalizedResult.accountId(),
                normalizedResult.customerId(),
                normalizedResult.accountNo(),
                normalizedResult.accountType(),
                normalizedResult.accountStatus(),
                normalizedResult.openedAt().toString(),
                normalizedResult.closedAt() == null ? null : normalizedResult.closedAt().toString(),
                normalizedResult.linkedSavingsAccountId());
    }

    /**
     * @brief 规范化必填文本（Normalize Required Text）；
     *        Normalize required text and reject blank value.
     *
     * @param value 原始值（Raw value）。
     * @param field 字段名（Field name）。
     * @return 规范化文本（Normalized text）。
     */
    private static String normalizeRequiredText(final String value, final String field) {
        final String normalized = Objects.requireNonNull(value, field + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return normalized;
    }

    /**
     * @brief 规范化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text by trimming and converting blank to null.
     *
     * @param value 原始值（Raw value, nullable）。
     * @return 规范化文本或 null（Normalized text or null）。
     */
    private static String normalizeNullableText(final String value) {
        if (value == null) {
            return null;
        }
        final String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
