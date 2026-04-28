package com.moesegfault.banking.presentation.web.credit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.credit.result.CreditCardAccountResult;
import com.moesegfault.banking.application.credit.result.RepayCreditCardResult;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @brief 信用卡还款响应 DTO（Repayment Response DTO），输出还款分配摘要与影响账单；
 *        Repayment response DTO exposing allocation summary and affected statements.
 */
public record RepaymentResponseDto(
        @JsonProperty("credit_card_account")
        CreditCardAccountSnapshotDto creditCardAccount,
        @JsonProperty("applied_to_account_amount")
        BigDecimal appliedToAccountAmount,
        @JsonProperty("applied_to_statement_amount")
        BigDecimal appliedToStatementAmount,
        @JsonProperty("unapplied_amount")
        BigDecimal unappliedAmount,
        @JsonProperty("currency_code")
        String currencyCode,
        @JsonProperty("statement_id")
        String statementIdOrNull,
        @JsonProperty("affected_statements")
        List<StatementResponseDto> affectedStatements
) {

    /**
     * @brief 规范化并校验还款响应（Normalize and Validate Repayment Response）；
     *        Normalize and validate repayment-response fields.
     */
    public RepaymentResponseDto {
        creditCardAccount = Objects.requireNonNull(creditCardAccount, "creditCardAccount must not be null");
        appliedToAccountAmount = Objects.requireNonNull(appliedToAccountAmount, "appliedToAccountAmount must not be null");
        appliedToStatementAmount = Objects.requireNonNull(
                appliedToStatementAmount,
                "appliedToStatementAmount must not be null");
        unappliedAmount = Objects.requireNonNull(unappliedAmount, "unappliedAmount must not be null");
        currencyCode = normalizeRequiredText(currencyCode, "currencyCode");
        statementIdOrNull = normalizeNullableText(statementIdOrNull);
        affectedStatements = List.copyOf(Objects.requireNonNull(affectedStatements, "affectedStatements must not be null"));
    }

    /**
     * @brief 从应用层结果创建响应（Create Response from Application Result）；
     *        Create repayment response DTO from application-layer result.
     *
     * @param result 应用层结果（Application result）。
     * @return 还款响应 DTO（Repayment response DTO）。
     */
    public static RepaymentResponseDto from(final RepayCreditCardResult result) {
        final RepayCreditCardResult normalizedResult = Objects.requireNonNull(result, "result must not be null");
        return new RepaymentResponseDto(
                CreditCardAccountSnapshotDto.from(normalizedResult.creditCardAccount()),
                normalizedResult.appliedToAccountAmount(),
                normalizedResult.appliedToStatementAmount(),
                normalizedResult.unappliedAmount(),
                normalizedResult.currencyCode(),
                normalizedResult.statementIdOrNull(),
                normalizedResult.affectedStatements().stream().map(StatementResponseDto::from).toList());
    }

    /**
     * @brief 信用卡账户快照 DTO（Credit Card Account Snapshot DTO），返回还款后的账户额度状态；
     *        Credit-card-account snapshot DTO returning post-repayment account credit status.
     */
    public record CreditCardAccountSnapshotDto(
            @JsonProperty("credit_card_account_id")
            String creditCardAccountId,
            @JsonProperty("credit_limit")
            BigDecimal creditLimit,
            @JsonProperty("available_credit")
            BigDecimal availableCredit,
            @JsonProperty("used_credit")
            BigDecimal usedCredit,
            @JsonProperty("cash_advance_limit")
            BigDecimal cashAdvanceLimit,
            @JsonProperty("billing_cycle_day")
            int billingCycleDay,
            @JsonProperty("payment_due_day")
            int paymentDueDay,
            @JsonProperty("interest_rate_decimal")
            BigDecimal interestRateDecimal,
            @JsonProperty("account_currency_code")
            String accountCurrencyCode
    ) {

        /**
         * @brief 规范化并校验账户快照（Normalize and Validate Account Snapshot）；
         *        Normalize and validate account-snapshot fields.
         */
        public CreditCardAccountSnapshotDto {
            creditCardAccountId = normalizeRequiredText(creditCardAccountId, "creditCardAccountId");
            creditLimit = Objects.requireNonNull(creditLimit, "creditLimit must not be null");
            availableCredit = Objects.requireNonNull(availableCredit, "availableCredit must not be null");
            usedCredit = Objects.requireNonNull(usedCredit, "usedCredit must not be null");
            cashAdvanceLimit = Objects.requireNonNull(cashAdvanceLimit, "cashAdvanceLimit must not be null");
            interestRateDecimal = Objects.requireNonNull(interestRateDecimal, "interestRateDecimal must not be null");
            accountCurrencyCode = normalizeRequiredText(accountCurrencyCode, "accountCurrencyCode");
        }

        /**
         * @brief 从应用层账户结果创建快照（Create Snapshot from Application Account Result）；
         *        Create snapshot DTO from application-layer account result.
         *
         * @param result 账户应用结果（Account application result）。
         * @return 账户快照 DTO（Account snapshot DTO）。
         */
        public static CreditCardAccountSnapshotDto from(final CreditCardAccountResult result) {
            final CreditCardAccountResult normalizedResult = Objects.requireNonNull(result, "result must not be null");
            return new CreditCardAccountSnapshotDto(
                    normalizedResult.creditCardAccountId(),
                    normalizedResult.creditLimit(),
                    normalizedResult.availableCredit(),
                    normalizedResult.usedCredit(),
                    normalizedResult.cashAdvanceLimit(),
                    normalizedResult.billingCycleDay(),
                    normalizedResult.paymentDueDay(),
                    normalizedResult.interestRateDecimal(),
                    normalizedResult.accountCurrencyCode());
        }
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
}
