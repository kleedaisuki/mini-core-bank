package com.moesegfault.banking.presentation.web.ledger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.ledger.result.BalanceResult;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @brief 余额响应 DTO（Balance Response DTO），统一余额查询 API 输出 schema；
 *        Balance response DTO standardizing output schema for balance-query API.
 */
public record BalanceResponseDto(
        @JsonProperty("account_id") String accountId,
        @JsonProperty("currency_code") String currencyCode,
        @JsonProperty("ledger_balance") BigDecimal ledgerBalance,
        @JsonProperty("available_balance") BigDecimal availableBalance,
        @JsonProperty("updated_at") String updatedAt
) {

    /**
     * @brief 规范化并校验响应字段（Normalize and Validate Response Fields）；
     *        Normalize and validate balance-response fields.
     */
    public BalanceResponseDto {
        accountId = normalizeRequiredText(accountId, "account_id");
        currencyCode = normalizeRequiredText(currencyCode, "currency_code");
        ledgerBalance = Objects.requireNonNull(ledgerBalance, "ledger_balance must not be null");
        availableBalance = Objects.requireNonNull(availableBalance, "available_balance must not be null");
        updatedAt = normalizeRequiredText(updatedAt, "updated_at");
    }

    /**
     * @brief 从应用层结果映射 DTO（Map from Application Result to DTO）；
     *        Map application balance result to web response DTO.
     *
     * @param result 应用层结果（Application result）。
     * @return 余额响应 DTO（Balance response DTO）。
     */
    public static BalanceResponseDto fromResult(final BalanceResult result) {
        final BalanceResult normalizedResult = Objects.requireNonNull(result, "result must not be null");
        return new BalanceResponseDto(
                normalizedResult.accountId(),
                normalizedResult.currencyCode().value(),
                normalizedResult.ledgerBalance().amount(),
                normalizedResult.availableBalance().amount(),
                normalizedResult.updatedAt().toString());
    }

    /**
     * @brief 规范化必填文本（Normalize Required Text）；
     *        Normalize required text field and reject blank value.
     *
     * @param rawValue 原始值（Raw value）。
     * @param fieldName 字段名（Field name）。
     * @return 规范化文本（Normalized text）。
     */
    private static String normalizeRequiredText(final String rawValue, final String fieldName) {
        final String normalized = Objects.requireNonNull(rawValue, fieldName + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
