package com.moesegfault.banking.application.credit.result;

import com.moesegfault.banking.domain.credit.CreditCardStatement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @brief 信用卡账单应用结果（Credit Card Statement Application Result），用于向展示层输出账单快照；
 *        Credit-card-statement application result for exposing statement snapshot to presentation layer.
 */
public final class CreditCardStatementResult {

    /**
     * @brief 账单 ID（Statement ID）；
     *        Statement identifier.
     */
    private final String statementId;

    /**
     * @brief 信用卡账户 ID（Credit Card Account ID）；
     *        Credit-card-account identifier.
     */
    private final String creditCardAccountId;

    /**
     * @brief 账期开始日期（Statement Period Start）；
     *        Statement period start date.
     */
    private final LocalDate statementPeriodStart;

    /**
     * @brief 账期结束日期（Statement Period End）；
     *        Statement period end date.
     */
    private final LocalDate statementPeriodEnd;

    /**
     * @brief 出账日（Statement Date）；
     *        Statement date.
     */
    private final LocalDate statementDate;

    /**
     * @brief 到期还款日（Payment Due Date）；
     *        Payment-due date.
     */
    private final LocalDate paymentDueDate;

    /**
     * @brief 应还总额（Total Amount Due）；
     *        Total amount due.
     */
    private final BigDecimal totalAmountDue;

    /**
     * @brief 最低还款额（Minimum Amount Due）；
     *        Minimum amount due.
     */
    private final BigDecimal minimumAmountDue;

    /**
     * @brief 已还金额（Paid Amount）；
     *        Paid amount.
     */
    private final BigDecimal paidAmount;

    /**
     * @brief 未还金额（Outstanding Amount）；
     *        Outstanding amount.
     */
    private final BigDecimal outstandingAmount;

    /**
     * @brief 账单状态（Statement Status）；
     *        Statement status.
     */
    private final String statementStatus;

    /**
     * @brief 币种代码（Currency Code）；
     *        Currency code.
     */
    private final String currencyCode;

    /**
     * @brief 构造信用卡账单应用结果（Construct Credit Card Statement Result）；
     *        Construct credit-card-statement application result.
     *
     * @param statementId 账单 ID（Statement ID）。
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @param statementPeriodStart 账期开始日期（Statement period start）。
     * @param statementPeriodEnd 账期结束日期（Statement period end）。
     * @param statementDate 出账日（Statement date）。
     * @param paymentDueDate 到期还款日（Payment-due date）。
     * @param totalAmountDue 应还总额（Total amount due）。
     * @param minimumAmountDue 最低还款额（Minimum amount due）。
     * @param paidAmount 已还金额（Paid amount）。
     * @param outstandingAmount 未还金额（Outstanding amount）。
     * @param statementStatus 账单状态（Statement status）。
     * @param currencyCode 币种代码（Currency code）。
     */
    public CreditCardStatementResult(
            final String statementId,
            final String creditCardAccountId,
            final LocalDate statementPeriodStart,
            final LocalDate statementPeriodEnd,
            final LocalDate statementDate,
            final LocalDate paymentDueDate,
            final BigDecimal totalAmountDue,
            final BigDecimal minimumAmountDue,
            final BigDecimal paidAmount,
            final BigDecimal outstandingAmount,
            final String statementStatus,
            final String currencyCode
    ) {
        this.statementId = requireText(statementId, "statementId");
        this.creditCardAccountId = requireText(creditCardAccountId, "creditCardAccountId");
        this.statementPeriodStart = Objects.requireNonNull(statementPeriodStart, "statementPeriodStart must not be null");
        this.statementPeriodEnd = Objects.requireNonNull(statementPeriodEnd, "statementPeriodEnd must not be null");
        this.statementDate = Objects.requireNonNull(statementDate, "statementDate must not be null");
        this.paymentDueDate = Objects.requireNonNull(paymentDueDate, "paymentDueDate must not be null");
        this.totalAmountDue = Objects.requireNonNull(totalAmountDue, "totalAmountDue must not be null");
        this.minimumAmountDue = Objects.requireNonNull(minimumAmountDue, "minimumAmountDue must not be null");
        this.paidAmount = Objects.requireNonNull(paidAmount, "paidAmount must not be null");
        this.outstandingAmount = Objects.requireNonNull(outstandingAmount, "outstandingAmount must not be null");
        this.statementStatus = requireText(statementStatus, "statementStatus");
        this.currencyCode = requireText(currencyCode, "currencyCode");
    }

    /**
     * @brief 由领域实体映射应用结果（Map from Domain Entity）；
     *        Map application result from domain entity.
     *
     * @param creditCardStatement 账单实体（Statement entity）。
     * @return 账单应用结果（Statement application result）。
     */
    public static CreditCardStatementResult from(final CreditCardStatement creditCardStatement) {
        final CreditCardStatement normalized = Objects.requireNonNull(
                creditCardStatement,
                "creditCardStatement must not be null");
        return new CreditCardStatementResult(
                normalized.statementId().value(),
                normalized.creditCardAccountId().value(),
                normalized.statementPeriod().start(),
                normalized.statementPeriod().end(),
                normalized.statementDate(),
                normalized.paymentDueDate(),
                normalized.totalAmountDue().amount(),
                normalized.minimumAmountDue().amount(),
                normalized.paidAmount().amount(),
                normalized.outstandingAmount().amount(),
                normalized.statementStatus().name(),
                normalized.currencyCode().value());
    }

    /**
     * @brief 返回账单 ID（Return Statement ID）；
     *        Return statement identifier.
     *
     * @return 账单 ID（Statement ID）。
     */
    public String statementId() {
        return statementId;
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
     * @brief 返回账期开始日期（Return Statement Period Start）；
     *        Return statement period start date.
     *
     * @return 账期开始日期（Statement period start）。
     */
    public LocalDate statementPeriodStart() {
        return statementPeriodStart;
    }

    /**
     * @brief 返回账期结束日期（Return Statement Period End）；
     *        Return statement period end date.
     *
     * @return 账期结束日期（Statement period end）。
     */
    public LocalDate statementPeriodEnd() {
        return statementPeriodEnd;
    }

    /**
     * @brief 返回出账日（Return Statement Date）；
     *        Return statement date.
     *
     * @return 出账日（Statement date）。
     */
    public LocalDate statementDate() {
        return statementDate;
    }

    /**
     * @brief 返回到期还款日（Return Payment Due Date）；
     *        Return payment-due date.
     *
     * @return 到期还款日（Payment-due date）。
     */
    public LocalDate paymentDueDate() {
        return paymentDueDate;
    }

    /**
     * @brief 返回应还总额（Return Total Amount Due）；
     *        Return total amount due.
     *
     * @return 应还总额（Total amount due）。
     */
    public BigDecimal totalAmountDue() {
        return totalAmountDue;
    }

    /**
     * @brief 返回最低还款额（Return Minimum Amount Due）；
     *        Return minimum amount due.
     *
     * @return 最低还款额（Minimum amount due）。
     */
    public BigDecimal minimumAmountDue() {
        return minimumAmountDue;
    }

    /**
     * @brief 返回已还金额（Return Paid Amount）；
     *        Return paid amount.
     *
     * @return 已还金额（Paid amount）。
     */
    public BigDecimal paidAmount() {
        return paidAmount;
    }

    /**
     * @brief 返回未还金额（Return Outstanding Amount）；
     *        Return outstanding amount.
     *
     * @return 未还金额（Outstanding amount）。
     */
    public BigDecimal outstandingAmount() {
        return outstandingAmount;
    }

    /**
     * @brief 返回账单状态（Return Statement Status）；
     *        Return statement status.
     *
     * @return 账单状态（Statement status）。
     */
    public String statementStatus() {
        return statementStatus;
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
