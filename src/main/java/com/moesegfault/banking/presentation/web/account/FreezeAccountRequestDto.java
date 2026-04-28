package com.moesegfault.banking.presentation.web.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Locale;
import java.util.Objects;

/**
 * @brief 冻结账户请求 DTO（Freeze Account Request DTO），对应 `PATCH /accounts/{accountId}`；
 *        Freeze-account request DTO for `PATCH /accounts/{accountId}`.
 */
public record FreezeAccountRequestDto(
        @JsonProperty("account_status") String accountStatus,
        @JsonProperty("freeze_reason") String freezeReason
) {

    /**
     * @brief 规范化并校验冻结请求（Normalize and Validate Freeze Request）；
     *        Normalize and validate freeze-account request fields.
     */
    public FreezeAccountRequestDto {
        accountStatus = normalizeRequiredText(accountStatus, "accountStatus").toUpperCase(Locale.ROOT);
        freezeReason = normalizeRequiredText(freezeReason, "freezeReason");
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

