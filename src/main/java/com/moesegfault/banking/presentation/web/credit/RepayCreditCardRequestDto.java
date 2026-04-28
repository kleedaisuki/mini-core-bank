package com.moesegfault.banking.presentation.web.credit;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.credit.command.RepayCreditCardCommand;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * @brief 信用卡还款请求 DTO（Repay Credit Card Request DTO），承载 `POST /credit/repayments` 请求体；
 *        Request DTO for `POST /credit/repayments` payload.
 */
public record RepayCreditCardRequestDto(
        @JsonProperty("credit_card_account_id")
        @JsonAlias({"creditCardAccountId"})
        String creditCardAccountId,
        @JsonProperty("repayment_amount")
        @JsonAlias({"repaymentAmount"})
        BigDecimal repaymentAmount,
        @JsonProperty("repayment_currency_code")
        @JsonAlias({"repaymentCurrencyCode"})
        String repaymentCurrencyCode,
        @JsonProperty("statement_id")
        @JsonAlias({"statementId"})
        String statementIdOrNull,
        @JsonProperty("source_account_id")
        @JsonAlias({"sourceAccountId"})
        String sourceAccountIdOrNull,
        @JsonProperty("as_of_date")
        @JsonAlias({"asOfDate"})
        String asOfDateOrNull
) {

    /**
     * @brief 规范化并校验请求 DTO（Normalize and Validate Request DTO）；
     *        Normalize and validate request fields.
     */
    public RepayCreditCardRequestDto {
        creditCardAccountId = normalizeRequiredText(creditCardAccountId, "creditCardAccountId");
        repaymentAmount = Objects.requireNonNull(repaymentAmount, "repaymentAmount must not be null");
        repaymentCurrencyCode = normalizeRequiredText(repaymentCurrencyCode, "repaymentCurrencyCode");
        statementIdOrNull = normalizeNullableText(statementIdOrNull);
        sourceAccountIdOrNull = normalizeNullableText(sourceAccountIdOrNull);
    }

    /**
     * @brief 转换为应用层命令（Convert to Application Command）；
     *        Convert this DTO into application-layer repay-credit-card command.
     *
     * @return 应用层命令（Application command）。
     */
    public RepayCreditCardCommand toCommand() {
        return new RepayCreditCardCommand(
                creditCardAccountId,
                repaymentAmount,
                repaymentCurrencyCode,
                statementIdOrNull,
                sourceAccountIdOrNull,
                parseNullableDate(asOfDateOrNull, "asOfDateOrNull"));
    }

    /**
     * @brief 规范化必填文本（Normalize Required Text）；
     *        Normalize required text and reject blank values.
     *
     * @param value 原始值（Raw value）。
     * @param fieldName 字段名（Field name）。
     * @return 规范化文本（Normalized text）。
     */
    private static String normalizeRequiredText(final String value, final String fieldName) {
        final String normalized = Objects.requireNonNull(value, fieldName + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    /**
     * @brief 规范化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text.
     *
     * @param value 原始值（Raw value）。
     * @return 规范化文本或 null（Normalized text or null）。
     */
    private static String normalizeNullableText(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    /**
     * @brief 解析可空日期文本（Parse Nullable Date Text）；
     *        Parse nullable ISO-8601 date text.
     *
     * @param value 日期文本（Date text）。
     * @param fieldName 字段名（Field name）。
     * @return 日期对象或 null（Date object or null）。
     */
    private static LocalDate parseNullableDate(final String value, final String fieldName) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(fieldName + " must use yyyy-MM-dd format", exception);
        }
    }
}
