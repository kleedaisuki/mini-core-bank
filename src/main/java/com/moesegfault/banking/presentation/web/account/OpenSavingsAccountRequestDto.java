package com.moesegfault.banking.presentation.web.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * @brief 开立储蓄账户请求 DTO（Open Savings Account Request DTO），对应 `POST /accounts/savings`；
 *        Open-savings-account request DTO for `POST /accounts/savings`.
 */
public record OpenSavingsAccountRequestDto(
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("account_no") String accountNo
) {

    /**
     * @brief 规范化并校验开户请求（Normalize and Validate Opening Request）；
     *        Normalize and validate opening request fields.
     */
    public OpenSavingsAccountRequestDto {
        customerId = normalizeRequiredText(customerId, "customerId");
        accountNo = normalizeRequiredText(accountNo, "accountNo");
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
}

