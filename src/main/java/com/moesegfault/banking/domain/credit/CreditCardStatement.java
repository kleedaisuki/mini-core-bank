package com.moesegfault.banking.domain.credit;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DateRange;
import com.moesegfault.banking.domain.shared.Money;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @brief 信用卡账单实体（Credit Card Statement Entity），对齐 `credit_card_statement` 结构与状态流转；
 *        Credit-card-statement entity aligned with `credit_card_statement` schema and state transitions.
 */
public final class CreditCardStatement {

    /**
     * @brief 账单 ID（Statement ID）；
     *        Statement identifier.
     */
    private final StatementId statementId;

    /**
     * @brief 信用卡账户 ID（Credit Card Account ID）；
     *        Credit-card-account identifier.
     */
    private final CreditCardAccountId creditCardAccountId;

    /**
     * @brief 账期范围（Statement Period）；
     *        Statement period range.
     */
    private final DateRange statementPeriod;

    /**
     * @brief 出账日期（Statement Date）；
     *        Statement date.
     */
    private final LocalDate statementDate;

    /**
     * @brief 到期还款日（Payment Due Date）；
     *        Payment due date.
     */
    private final LocalDate paymentDueDate;

    /**
     * @brief 应还总额（Total Amount Due）；
     *        Total amount due.
     */
    private final Money totalAmountDue;

    /**
     * @brief 最低还款额（Minimum Amount Due）；
     *        Minimum amount due.
     */
    private final Money minimumAmountDue;

    /**
     * @brief 已还金额（Paid Amount）；
     *        Paid amount.
     */
    private Money paidAmount;

    /**
     * @brief 账单状态（Statement Status）；
     *        Statement status.
     */
    private StatementStatus statementStatus;

    /**
     * @brief 账单币种（Statement Currency）；
     *        Statement currency code.
     */
    private final CurrencyCode currencyCode;

    /**
     * @brief 构造信用卡账单实体（Construct Credit Card Statement Entity）；
     *        Construct credit-card-statement entity.
     *
     * @param statementId         账单 ID（Statement ID）。
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @param statementPeriod     账期范围（Statement period）。
     * @param statementDate       出账日期（Statement date）。
     * @param paymentDueDate      到期还款日（Payment due date）。
     * @param totalAmountDue      应还总额（Total amount due）。
     * @param minimumAmountDue    最低还款额（Minimum amount due）。
     * @param paidAmount          已还金额（Paid amount）。
     * @param statementStatus     账单状态（Statement status）。
     * @param currencyCode        账单币种（Statement currency）。
     */
    private CreditCardStatement(
            final StatementId statementId,
            final CreditCardAccountId creditCardAccountId,
            final DateRange statementPeriod,
            final LocalDate statementDate,
            final LocalDate paymentDueDate,
            final Money totalAmountDue,
            final Money minimumAmountDue,
            final Money paidAmount,
            final StatementStatus statementStatus,
            final CurrencyCode currencyCode
    ) {
        this.statementId = Objects.requireNonNull(statementId, "Statement ID must not be null");
        this.creditCardAccountId = Objects.requireNonNull(creditCardAccountId, "Credit-card-account ID must not be null");
        this.statementPeriod = Objects.requireNonNull(statementPeriod, "Statement period must not be null");
        this.statementDate = Objects.requireNonNull(statementDate, "Statement date must not be null");
        this.paymentDueDate = Objects.requireNonNull(paymentDueDate, "Payment due date must not be null");
        this.totalAmountDue = Objects.requireNonNull(totalAmountDue, "Total amount due must not be null");
        this.minimumAmountDue = Objects.requireNonNull(minimumAmountDue, "Minimum amount due must not be null");
        this.paidAmount = Objects.requireNonNull(paidAmount, "Paid amount must not be null");
        this.statementStatus = Objects.requireNonNull(statementStatus, "Statement status must not be null");
        this.currencyCode = Objects.requireNonNull(currencyCode, "Currency code must not be null");
        ensureCoreInvariants();
    }

    /**
     * @brief 生成新账单（Generate New Statement）；
     *        Generate a new credit-card statement.
     *
     * @param statementId         账单 ID（Statement ID）。
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @param statementPeriod     账期范围（Statement period）。
     * @param statementDate       出账日期（Statement date）。
     * @param paymentDueDate      到期还款日（Payment due date）。
     * @param totalAmountDue      应还总额（Total amount due）。
     * @param minimumAmountDue    最低还款额（Minimum amount due）。
     * @param currencyCode        账单币种（Statement currency）。
     * @return 新账单实体（New statement entity）。
     */
    public static CreditCardStatement generate(
            final StatementId statementId,
            final CreditCardAccountId creditCardAccountId,
            final DateRange statementPeriod,
            final LocalDate statementDate,
            final LocalDate paymentDueDate,
            final Money totalAmountDue,
            final Money minimumAmountDue,
            final CurrencyCode currencyCode
    ) {
        final Money normalizedTotal = Objects.requireNonNull(totalAmountDue, "Total amount due must not be null");
        final StatementStatus initialStatus = normalizedTotal.isZero() ? StatementStatus.PAID : StatementStatus.OPEN;
        return new CreditCardStatement(
                statementId,
                creditCardAccountId,
                statementPeriod,
                statementDate,
                paymentDueDate,
                normalizedTotal,
                minimumAmountDue,
                Money.zero(normalizedTotal.currencyCode()),
                initialStatus,
                currencyCode);
    }

    /**
     * @brief 从持久化状态重建账单（Restore Statement from Persistence）；
     *        Restore statement from persistence state.
     *
     * @param statementId         账单 ID（Statement ID）。
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @param statementPeriod     账期范围（Statement period）。
     * @param statementDate       出账日期（Statement date）。
     * @param paymentDueDate      到期还款日（Payment due date）。
     * @param totalAmountDue      应还总额（Total amount due）。
     * @param minimumAmountDue    最低还款额（Minimum amount due）。
     * @param paidAmount          已还金额（Paid amount）。
     * @param statementStatus     账单状态（Statement status）。
     * @param currencyCode        账单币种（Statement currency）。
     * @return 重建后的账单实体（Restored statement entity）。
     */
    public static CreditCardStatement restore(
            final StatementId statementId,
            final CreditCardAccountId creditCardAccountId,
            final DateRange statementPeriod,
            final LocalDate statementDate,
            final LocalDate paymentDueDate,
            final Money totalAmountDue,
            final Money minimumAmountDue,
            final Money paidAmount,
            final StatementStatus statementStatus,
            final CurrencyCode currencyCode
    ) {
        return new CreditCardStatement(
                statementId,
                creditCardAccountId,
                statementPeriod,
                statementDate,
                paymentDueDate,
                totalAmountDue,
                minimumAmountDue,
                paidAmount,
                statementStatus,
                currencyCode);
    }

    /**
     * @brief 应用还款并返回剩余金额（Apply Repayment and Return Remainder）；
     *        Apply repayment and return unallocated remainder.
     *
     * @param repaymentAmount 还款金额（Repayment amount）。
     * @return 未分配剩余金额（Unallocated remainder）。
     */
    public Money applyRepayment(final Money repaymentAmount) {
        final Money normalizedRepayment = Objects.requireNonNull(repaymentAmount, "Repayment amount must not be null");
        if (!statementStatus.canAcceptRepayment()) {
            throw new BusinessRuleViolation("Statement status does not allow repayment: " + statementStatus);
        }
        requireSameCurrency(normalizedRepayment, "Repayment amount");
        if (!normalizedRepayment.isPositive()) {
            throw new BusinessRuleViolation("Repayment amount must be positive");
        }
        final Money outstanding = outstandingAmount();
        if (outstanding.isZero()) {
            statementStatus = StatementStatus.PAID;
            return normalizedRepayment;
        }
        final Money applied = normalizedRepayment.compareTo(outstanding) > 0 ? outstanding : normalizedRepayment;
        paidAmount = paidAmount.add(applied);
        if (paidAmount.compareTo(totalAmountDue) == 0) {
            statementStatus = StatementStatus.PAID;
        }
        return normalizedRepayment.subtract(applied);
    }

    /**
     * @brief 按给定日期标记逾期（Mark Overdue by As-of Date）；
     *        Mark statement as overdue by as-of date.
     *
     * @param asOfDate 业务日期（As-of date）。
     */
    public void markOverdue(final LocalDate asOfDate) {
        final LocalDate normalizedDate = Objects.requireNonNull(asOfDate, "As-of date must not be null");
        if (statementStatus == StatementStatus.OPEN && normalizedDate.isAfter(paymentDueDate) && outstandingAmount().isPositive()) {
            statementStatus = StatementStatus.OVERDUE;
        }
    }

    /**
     * @brief 关闭账单（Close Statement）；
     *        Close statement when fully paid.
     */
    public void close() {
        if (statementStatus == StatementStatus.CLOSED) {
            return;
        }
        if (outstandingAmount().isPositive()) {
            throw new BusinessRuleViolation("Statement with outstanding amount cannot be CLOSED");
        }
        statementStatus = StatementStatus.CLOSED;
    }

    /**
     * @brief 返回未还金额（Return Outstanding Amount）；
     *        Return outstanding amount.
     *
     * @return 未还金额（Outstanding amount）。
     */
    public Money outstandingAmount() {
        return totalAmountDue.subtract(paidAmount);
    }

    /**
     * @brief 返回剩余最低还款额（Return Remaining Minimum Due）；
     *        Return remaining minimum amount due.
     *
     * @return 剩余最低还款额（Remaining minimum due）。
     */
    public Money remainingMinimumDue() {
        final Money remaining = minimumAmountDue.subtract(paidAmount);
        return remaining.isNegative() ? Money.zero(currencyCode) : remaining;
    }

    /**
     * @brief 构建账单生成事件（Build Statement Generated Event）；
     *        Build credit-card-statement-generated domain event.
     *
     * @return 账单生成事件（Statement generated event）。
     */
    public CreditCardStatementGenerated generatedEvent() {
        return new CreditCardStatementGenerated(
                statementId,
                creditCardAccountId,
                statementPeriod,
                totalAmountDue,
                minimumAmountDue,
                paymentDueDate,
                Instant.now());
    }

    /**
     * @brief 返回账单 ID（Return Statement ID）；
     *        Return statement identifier.
     *
     * @return 账单 ID（Statement ID）。
     */
    public StatementId statementId() {
        return statementId;
    }

    /**
     * @brief 返回信用卡账户 ID（Return Credit Card Account ID）；
     *        Return credit-card-account identifier.
     *
     * @return 信用卡账户 ID（Credit-card-account ID）。
     */
    public CreditCardAccountId creditCardAccountId() {
        return creditCardAccountId;
    }

    /**
     * @brief 返回账期范围（Return Statement Period）；
     *        Return statement period range.
     *
     * @return 账期范围（Statement period）。
     */
    public DateRange statementPeriod() {
        return statementPeriod;
    }

    /**
     * @brief 返回出账日（Return Statement Date）；
     *        Return statement date.
     *
     * @return 出账日期（Statement date）。
     */
    public LocalDate statementDate() {
        return statementDate;
    }

    /**
     * @brief 返回到期还款日（Return Payment Due Date）；
     *        Return payment-due date.
     *
     * @return 到期还款日（Payment due date）。
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
    public Money totalAmountDue() {
        return totalAmountDue;
    }

    /**
     * @brief 返回最低还款额（Return Minimum Amount Due）；
     *        Return minimum amount due.
     *
     * @return 最低还款额（Minimum amount due）。
     */
    public Money minimumAmountDue() {
        return minimumAmountDue;
    }

    /**
     * @brief 返回已还金额（Return Paid Amount）；
     *        Return paid amount.
     *
     * @return 已还金额（Paid amount）。
     */
    public Money paidAmount() {
        return paidAmount;
    }

    /**
     * @brief 返回账单状态（Return Statement Status）；
     *        Return statement status.
     *
     * @return 账单状态（Statement status）。
     */
    public StatementStatus statementStatus() {
        return statementStatus;
    }

    /**
     * @brief 返回币种（Return Currency Code）；
     *        Return statement currency code.
     *
     * @return 币种代码（Currency code）。
     */
    public CurrencyCode currencyCode() {
        return currencyCode;
    }

    /**
     * @brief 校验核心不变量（Ensure Core Invariants）；
     *        Ensure core invariants for statement fields.
     */
    private void ensureCoreInvariants() {
        if (paymentDueDate.isBefore(statementDate)) {
            throw new BusinessRuleViolation("Payment due date must not be before statement date");
        }
        if (statementDate.isBefore(statementPeriod.end())) {
            throw new BusinessRuleViolation("Statement date must be on or after statement period end");
        }
        requireSameCurrency(totalAmountDue, "Total amount due");
        requireSameCurrency(minimumAmountDue, "Minimum amount due");
        requireSameCurrency(paidAmount, "Paid amount");
        ensureNonNegative(totalAmountDue, "Total amount due");
        ensureNonNegative(minimumAmountDue, "Minimum amount due");
        ensureNonNegative(paidAmount, "Paid amount");
        if (minimumAmountDue.compareTo(totalAmountDue) > 0) {
            throw new BusinessRuleViolation("Minimum amount due must not exceed total amount due");
        }
        if (paidAmount.compareTo(totalAmountDue) > 0) {
            throw new BusinessRuleViolation("Paid amount must not exceed total amount due");
        }
        final boolean fullyPaid = paidAmount.compareTo(totalAmountDue) == 0;
        if (statementStatus == StatementStatus.PAID && !fullyPaid) {
            throw new BusinessRuleViolation("PAID statement must have paid_amount equal to total_amount_due");
        }
        if (statementStatus == StatementStatus.CLOSED && !fullyPaid) {
            throw new BusinessRuleViolation("CLOSED statement must have paid_amount equal to total_amount_due");
        }
        if ((statementStatus == StatementStatus.OPEN || statementStatus == StatementStatus.OVERDUE) && fullyPaid) {
            throw new BusinessRuleViolation("OPEN/OVERDUE statement must keep outstanding amount");
        }
    }

    /**
     * @brief 校验币种一致（Require Same Currency）；
     *        Require same statement currency.
     *
     * @param money     金额（Money amount）。
     * @param fieldName 字段名（Field name）。
     */
    private void requireSameCurrency(
            final Money money,
            final String fieldName
    ) {
        if (!money.currencyCode().equals(currencyCode)) {
            throw new BusinessRuleViolation(fieldName + " currency must match statement currency");
        }
    }

    /**
     * @brief 校验金额非负（Ensure Non-negative Amount）；
     *        Ensure money amount is non-negative.
     *
     * @param money     金额（Money amount）。
     * @param fieldName 字段名（Field name）。
     */
    private static void ensureNonNegative(
            final Money money,
            final String fieldName
    ) {
        if (money.isNegative()) {
            throw new BusinessRuleViolation(fieldName + " must not be negative");
        }
    }
}
