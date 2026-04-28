package com.moesegfault.banking.presentation.web.credit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.credit.result.CreditCardStatementResult;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @brief 信用卡账单响应 DTO（Statement Response DTO），用于返回标准化账单快照；
 *        Response DTO returning canonical credit-card statement snapshot.
 */
public record StatementResponseDto(
        @JsonProperty("statement_id")
        String statementId,
        @JsonProperty("credit_card_account_id")
        String creditCardAccountId,
        @JsonProperty("statement_period_start")
        String statementPeriodStart,
        @JsonProperty("statement_period_end")
        String statementPeriodEnd,
        @JsonProperty("statement_date")
        String statementDate,
        @JsonProperty("payment_due_date")
        String paymentDueDate,
        @JsonProperty("total_amount_due")
        BigDecimal totalAmountDue,
        @JsonProperty("minimum_amount_due")
        BigDecimal minimumAmountDue,
        @JsonProperty("paid_amount")
        BigDecimal paidAmount,
        @JsonProperty("outstanding_amount")
        BigDecimal outstandingAmount,
        @JsonProperty("statement_status")
        String statementStatus,
        @JsonProperty("currency_code")
        String currencyCode
) {

    /**
     * @brief 规范化并校验账单响应（Normalize and Validate Statement Response）；
     *        Normalize and validate statement-response fields.
     */
    public StatementResponseDto {
        statementId = normalizeRequiredText(statementId, "statementId");
        creditCardAccountId = normalizeRequiredText(creditCardAccountId, "creditCardAccountId");
        statementPeriodStart = normalizeRequiredText(statementPeriodStart, "statementPeriodStart");
        statementPeriodEnd = normalizeRequiredText(statementPeriodEnd, "statementPeriodEnd");
        statementDate = normalizeRequiredText(statementDate, "statementDate");
        paymentDueDate = normalizeRequiredText(paymentDueDate, "paymentDueDate");
        totalAmountDue = Objects.requireNonNull(totalAmountDue, "totalAmountDue must not be null");
        minimumAmountDue = Objects.requireNonNull(minimumAmountDue, "minimumAmountDue must not be null");
        paidAmount = Objects.requireNonNull(paidAmount, "paidAmount must not be null");
        outstandingAmount = Objects.requireNonNull(outstandingAmount, "outstandingAmount must not be null");
        statementStatus = normalizeRequiredText(statementStatus, "statementStatus");
        currencyCode = normalizeRequiredText(currencyCode, "currencyCode");
    }

    /**
     * @brief 从应用层结果创建响应（Create Response from Application Result）；
     *        Create statement response DTO from application-layer result.
     *
     * @param result 应用层结果（Application result）。
     * @return 账单响应 DTO（Statement response DTO）。
     */
    public static StatementResponseDto from(final CreditCardStatementResult result) {
        final CreditCardStatementResult normalizedResult = Objects.requireNonNull(result, "result must not be null");
        return new StatementResponseDto(
                normalizedResult.statementId(),
                normalizedResult.creditCardAccountId(),
                normalizedResult.statementPeriodStart().toString(),
                normalizedResult.statementPeriodEnd().toString(),
                normalizedResult.statementDate().toString(),
                normalizedResult.paymentDueDate().toString(),
                normalizedResult.totalAmountDue(),
                normalizedResult.minimumAmountDue(),
                normalizedResult.paidAmount(),
                normalizedResult.outstandingAmount(),
                normalizedResult.statementStatus(),
                normalizedResult.currencyCode());
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
}
