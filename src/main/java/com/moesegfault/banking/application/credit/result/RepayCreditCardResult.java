package com.moesegfault.banking.application.credit.result;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @brief 信用卡还款应用结果（Repay Credit Card Application Result），输出还款分配与账户快照；
 *        Repay-credit-card application result exposing repayment allocation and account snapshot.
 */
public final class RepayCreditCardResult {

    /**
     * @brief 账户快照（Account Snapshot）；
     *        Credit-card-account snapshot.
     */
    private final CreditCardAccountResult creditCardAccount;

    /**
     * @brief 释放到额度的金额（Applied-to-account Amount）；
     *        Amount applied to restore account available credit.
     */
    private final BigDecimal appliedToAccountAmount;

    /**
     * @brief 分配到账单的金额（Applied-to-statement Amount）；
     *        Amount allocated across statements.
     */
    private final BigDecimal appliedToStatementAmount;

    /**
     * @brief 未分配金额（Unapplied Amount）；
     *        Unapplied remainder amount.
     */
    private final BigDecimal unappliedAmount;

    /**
     * @brief 币种代码（Currency Code）；
     *        Currency code.
     */
    private final String currencyCode;

    /**
     * @brief 定向还款账单 ID（可空）（Target Statement ID, Nullable）；
     *        Target statement identifier, nullable for automatic allocation.
     */
    private final String statementIdOrNull;

    /**
     * @brief 受影响账单列表（Affected Statements）；
     *        Affected statement snapshots.
     */
    private final List<CreditCardStatementResult> affectedStatements;

    /**
     * @brief 构造还款结果（Construct Repayment Result）；
     *        Construct repayment application result.
     *
     * @param creditCardAccount 账户快照（Account snapshot）。
     * @param appliedToAccountAmount 释放到额度的金额（Applied-to-account amount）。
     * @param appliedToStatementAmount 分配到账单的金额（Applied-to-statement amount）。
     * @param unappliedAmount 未分配金额（Unapplied amount）。
     * @param currencyCode 币种代码（Currency code）。
     * @param statementIdOrNull 定向账单 ID（可空）（Target statement ID, nullable）。
     * @param affectedStatements 受影响账单列表（Affected statement list）。
     */
    public RepayCreditCardResult(
            final CreditCardAccountResult creditCardAccount,
            final BigDecimal appliedToAccountAmount,
            final BigDecimal appliedToStatementAmount,
            final BigDecimal unappliedAmount,
            final String currencyCode,
            final String statementIdOrNull,
            final List<CreditCardStatementResult> affectedStatements
    ) {
        this.creditCardAccount = Objects.requireNonNull(creditCardAccount, "creditCardAccount must not be null");
        this.appliedToAccountAmount = Objects.requireNonNull(
                appliedToAccountAmount,
                "appliedToAccountAmount must not be null");
        this.appliedToStatementAmount = Objects.requireNonNull(
                appliedToStatementAmount,
                "appliedToStatementAmount must not be null");
        this.unappliedAmount = Objects.requireNonNull(unappliedAmount, "unappliedAmount must not be null");
        this.currencyCode = requireText(currencyCode, "currencyCode");
        this.statementIdOrNull = statementIdOrNull == null || statementIdOrNull.isBlank()
                ? null
                : statementIdOrNull.trim();
        this.affectedStatements = List.copyOf(Objects.requireNonNull(affectedStatements, "affectedStatements must not be null"));
    }

    /**
     * @brief 返回账户快照（Return Account Snapshot）；
     *        Return account snapshot.
     *
     * @return 账户快照（Account snapshot）。
     */
    public CreditCardAccountResult creditCardAccount() {
        return creditCardAccount;
    }

    /**
     * @brief 返回释放到额度金额（Return Applied-to-account Amount）；
     *        Return applied-to-account amount.
     *
     * @return 释放到额度金额（Applied-to-account amount）。
     */
    public BigDecimal appliedToAccountAmount() {
        return appliedToAccountAmount;
    }

    /**
     * @brief 返回分配到账单金额（Return Applied-to-statement Amount）；
     *        Return applied-to-statement amount.
     *
     * @return 分配到账单金额（Applied-to-statement amount）。
     */
    public BigDecimal appliedToStatementAmount() {
        return appliedToStatementAmount;
    }

    /**
     * @brief 返回未分配金额（Return Unapplied Amount）；
     *        Return unapplied amount.
     *
     * @return 未分配金额（Unapplied amount）。
     */
    public BigDecimal unappliedAmount() {
        return unappliedAmount;
    }

    /**
     * @brief 返回币种代码（Return Currency Code）；
     *        Return currency code.
     *
     * @return 币种代码（Currency code）。
     */
    public String currencyCode() {
        return currencyCode;
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
     * @brief 返回受影响账单列表（Return Affected Statements）；
     *        Return affected statement snapshots.
     *
     * @return 账单列表（Statement list）。
     */
    public List<CreditCardStatementResult> affectedStatements() {
        return affectedStatements;
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
}
