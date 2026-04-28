package com.moesegfault.banking.presentation.web.ledger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.ledger.result.LedgerEntryResult;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @brief 单条分录响应 DTO（Ledger Entry Response DTO），统一分录查询单项 schema；
 *        Single ledger-entry response DTO standardizing item schema for entry-query API.
 */
public record LedgerEntryResponseDto(
        @JsonProperty("entry_id") String entryId,
        @JsonProperty("transaction_id") String transactionId,
        @JsonProperty("batch_id") String batchId,
        @JsonProperty("account_id") String accountId,
        @JsonProperty("currency_code") String currencyCode,
        @JsonProperty("entry_direction") String entryDirection,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("ledger_balance_after") BigDecimal ledgerBalanceAfter,
        @JsonProperty("available_balance_after") BigDecimal availableBalanceAfter,
        @JsonProperty("entry_type") String entryType,
        @JsonProperty("posted_at") String postedAt
) {

    /**
     * @brief 规范化并校验响应字段（Normalize and Validate Response Fields）；
     *        Normalize and validate ledger-entry response fields.
     */
    public LedgerEntryResponseDto {
        entryId = normalizeRequiredText(entryId, "entry_id");
        transactionId = normalizeRequiredText(transactionId, "transaction_id");
        batchId = normalizeOptionalText(batchId);
        accountId = normalizeRequiredText(accountId, "account_id");
        currencyCode = normalizeRequiredText(currencyCode, "currency_code");
        entryDirection = normalizeRequiredText(entryDirection, "entry_direction");
        amount = Objects.requireNonNull(amount, "amount must not be null");
        entryType = normalizeRequiredText(entryType, "entry_type");
        postedAt = normalizeRequiredText(postedAt, "posted_at");
    }

    /**
     * @brief 从应用层结果映射 DTO（Map from Application Result to DTO）；
     *        Map application ledger-entry result to web response DTO.
     *
     * @param result 应用层结果（Application result）。
     * @return 分录响应 DTO（Ledger-entry response DTO）。
     */
    public static LedgerEntryResponseDto fromResult(final LedgerEntryResult result) {
        final LedgerEntryResult normalizedResult = Objects.requireNonNull(result, "result must not be null");
        return new LedgerEntryResponseDto(
                normalizedResult.entryId(),
                normalizedResult.transactionId(),
                normalizedResult.batchId(),
                normalizedResult.accountId(),
                normalizedResult.currencyCode().value(),
                normalizedResult.entryDirection().name(),
                normalizedResult.amount().amount(),
                normalizedResult.ledgerBalanceAfter() == null ? null : normalizedResult.ledgerBalanceAfter().amount(),
                normalizedResult.availableBalanceAfter() == null ? null : normalizedResult.availableBalanceAfter().amount(),
                normalizedResult.entryType().name(),
                normalizedResult.postedAt().toString());
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

    /**
     * @brief 规范化可选文本（Normalize Optional Text）；
     *        Normalize optional text field and convert blank to null.
     *
     * @param rawValue 原始值（Raw value）。
     * @return 规范化文本（Normalized text, nullable）。
     */
    private static String normalizeOptionalText(final String rawValue) {
        if (rawValue == null) {
            return null;
        }
        final String normalized = rawValue.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
