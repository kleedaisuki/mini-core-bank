package com.moesegfault.banking.application.credit.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @brief 信用卡还款命令（Repay Credit Card Command），定义还款操作输入参数；
 *        Repay-credit-card command defining input parameters for repayment operation.
 */
public final class RepayCreditCardCommand {

    /**
     * @brief 信用卡账户 ID（Credit Card Account ID）；
     *        Credit-card-account identifier.
     */
    private final String creditCardAccountId;

    /**
     * @brief 还款金额（Repayment Amount）；
     *        Repayment amount.
     */
    private final BigDecimal repaymentAmount;

    /**
     * @brief 还款币种代码（Repayment Currency Code）；
     *        Repayment currency code.
     */
    private final String repaymentCurrencyCode;

    /**
     * @brief 定向账单 ID（可空）（Target Statement ID, Nullable）；
     *        Target statement identifier, nullable.
     */
    private final String statementIdOrNull;

    /**
     * @brief 来源账户 ID（可空）（Source Account ID, Nullable）；
     *        Source account identifier, nullable.
     */
    private final String sourceAccountIdOrNull;

    /**
     * @brief 业务日期（可空）（As-of Date, Nullable）；
     *        Business as-of date, nullable.
     */
    private final LocalDate asOfDateOrNull;

    /**
     * @brief 构造还款命令（Construct Repay Credit Card Command）；
     *        Construct repay-credit-card command.
     *
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @param repaymentAmount 还款金额（Repayment amount）。
     * @param repaymentCurrencyCode 还款币种代码（Repayment currency code）。
     * @param statementIdOrNull 定向账单 ID（可空）（Target statement ID, nullable）。
     * @param sourceAccountIdOrNull 来源账户 ID（可空）（Source account ID, nullable）。
     * @param asOfDateOrNull 业务日期（可空）（As-of date, nullable）。
     */
    public RepayCreditCardCommand(
            final String creditCardAccountId,
            final BigDecimal repaymentAmount,
            final String repaymentCurrencyCode,
            final String statementIdOrNull,
            final String sourceAccountIdOrNull,
            final LocalDate asOfDateOrNull
    ) {
        this.creditCardAccountId = requireText(creditCardAccountId, "creditCardAccountId");
        this.repaymentAmount = Objects.requireNonNull(repaymentAmount, "repaymentAmount must not be null");
        this.repaymentCurrencyCode = requireText(repaymentCurrencyCode, "repaymentCurrencyCode");
        this.statementIdOrNull = normalizeNullableText(statementIdOrNull);
        this.sourceAccountIdOrNull = normalizeNullableText(sourceAccountIdOrNull);
        this.asOfDateOrNull = asOfDateOrNull;
    }

    /**
     * @brief 返回信用卡账户 ID（Return Credit Card Account ID）；
     *        Return credit-card-account identifier.
     *
     * @return 信用卡账户 ID（Credit-card-account ID）。
     */
    public String creditCardAccountId() {
        return creditCardAccountId;
    }

    /**
     * @brief 返回还款金额（Return Repayment Amount）；
     *        Return repayment amount.
     *
     * @return 还款金额（Repayment amount）。
     */
    public BigDecimal repaymentAmount() {
        return repaymentAmount;
    }

    /**
     * @brief 返回还款币种代码（Return Repayment Currency Code）；
     *        Return repayment currency code.
     *
     * @return 还款币种代码（Repayment currency code）。
     */
    public String repaymentCurrencyCode() {
        return repaymentCurrencyCode;
    }

    /**
     * @brief 返回定向账单 ID（可空）（Return Target Statement ID, Nullable）；
     *        Return target statement identifier, nullable.
     *
     * @return 定向账单 ID 或 null（Target statement ID or null）。
     */
    public String statementIdOrNull() {
        return statementIdOrNull;
    }

    /**
     * @brief 返回来源账户 ID（可空）（Return Source Account ID, Nullable）；
     *        Return source account identifier, nullable.
     *
     * @return 来源账户 ID 或 null（Source account ID or null）。
     */
    public String sourceAccountIdOrNull() {
        return sourceAccountIdOrNull;
    }

    /**
     * @brief 返回业务日期（可空）（Return As-of Date, Nullable）；
     *        Return business as-of date, nullable.
     *
     * @return 业务日期或 null（As-of date or null）。
     */
    public LocalDate asOfDateOrNull() {
        return asOfDateOrNull;
    }

    /**
     * @brief 校验非空文本（Require Non-blank Text）；
     *        Require non-blank text value.
     *
     * @param value 输入值（Input value）。
     * @param fieldName 字段名（Field name）。
     * @return 归一化文本（Normalized text）。
     */
    private static String requireText(
            final String value,
            final String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    /**
     * @brief 标准化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text.
     *
     * @param value 输入值（Input value）。
     * @return 标准化文本或 null（Normalized text or null）。
     */
    private static String normalizeNullableText(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
