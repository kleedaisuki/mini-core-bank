package com.moesegfault.banking.presentation.web.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * @brief 开立外汇账户请求 DTO（Open FX Account Request DTO），对应 `POST /accounts/fx`；
 *        Open-FX-account request DTO for `POST /accounts/fx`.
 */
public record OpenFxAccountRequestDto(
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("account_no") String accountNo,
        @JsonProperty("linked_savings_account_id") String linkedSavingsAccountId
) {

    /**
     * @brief 规范化并校验开户请求（Normalize and Validate Opening Request）；
     *        Normalize and validate opening request fields.
     */
    public OpenFxAccountRequestDto {
        customerId = normalizeRequiredText(customerId, "customerId");
        accountNo = normalizeRequiredText(accountNo, "accountNo");
        linkedSavingsAccountId = normalizeRequiredText(linkedSavingsAccountId, "linkedSavingsAccountId");
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

