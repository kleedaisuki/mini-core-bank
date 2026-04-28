package com.moesegfault.banking.presentation.web.credit;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.credit.command.GenerateStatementCommand;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * @brief 生成账单请求 DTO（Generate Statement Request DTO），承载 `POST /credit/statements` 的请求体；
 *        Request DTO for `POST /credit/statements` payload.
 */
public record GenerateStatementRequestDto(
        @JsonProperty("credit_card_account_id")
        @JsonAlias({"creditCardAccountId"})
        String creditCardAccountId,
        @JsonProperty("statement_date")
        @JsonAlias({"statementDate"})
        String statementDate,
        @JsonProperty("minimum_payment_rate_decimal")
        @JsonAlias({"minimumPaymentRateDecimal"})
        BigDecimal minimumPaymentRateDecimal,
        @JsonProperty("minimum_payment_floor_amount")
        @JsonAlias({"minimumPaymentFloorAmount"})
        BigDecimal minimumPaymentFloorAmount
) {

    /**
     * @brief 规范化并校验请求 DTO（Normalize and Validate Request DTO）；
     *        Normalize and validate request fields.
     */
    public GenerateStatementRequestDto {
        creditCardAccountId = normalizeRequiredText(creditCardAccountId, "creditCardAccountId");
        statementDate = normalizeRequiredText(statementDate, "statementDate");
        minimumPaymentRateDecimal = Objects.requireNonNull(
                minimumPaymentRateDecimal,
                "minimumPaymentRateDecimal must not be null");
        minimumPaymentFloorAmount = Objects.requireNonNull(
                minimumPaymentFloorAmount,
                "minimumPaymentFloorAmount must not be null");
    }

    /**
     * @brief 转换为应用层命令（Convert to Application Command）；
     *        Convert this DTO into application-layer generate-statement command.
     *
     * @return 应用层命令（Application command）。
     */
    public GenerateStatementCommand toCommand() {
        return new GenerateStatementCommand(
                creditCardAccountId,
                parseDate(statementDate, "statementDate"),
                minimumPaymentRateDecimal,
                minimumPaymentFloorAmount);
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
     * @brief 解析日期文本（Parse Date Text）；
     *        Parse ISO-8601 date text.
     *
     * @param value 日期文本（Date text）。
     * @param fieldName 字段名（Field name）。
     * @return 日期对象（Date object）。
     */
    private static LocalDate parseDate(final String value, final String fieldName) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(fieldName + " must use yyyy-MM-dd format", exception);
        }
    }
}
