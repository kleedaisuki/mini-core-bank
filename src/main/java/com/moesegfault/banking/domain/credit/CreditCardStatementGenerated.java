package com.moesegfault.banking.domain.credit;

import com.moesegfault.banking.domain.account.CreditCardAccountId;
import com.moesegfault.banking.domain.shared.DateRange;
import com.moesegfault.banking.domain.shared.DomainEvent;
import com.moesegfault.banking.domain.shared.Money;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @brief 信用卡账单生成事件（Credit Card Statement Generated Event）；
 *        Credit-card-statement-generated event.
 */
public final class CreditCardStatementGenerated implements DomainEvent {

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
     * @brief 到期还款日（Payment Due Date）；
     *        Payment due date.
     */
    private final LocalDate paymentDueDate;

    /**
     * @brief 事件时间（Occurred Timestamp）；
     *        Event occurred timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造账单生成事件（Construct Statement Generated Event）；
     *        Construct credit-card-statement-generated event.
     *
     * @param statementId         账单 ID（Statement ID）。
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @param statementPeriod     账期范围（Statement period）。
     * @param totalAmountDue      应还总额（Total amount due）。
     * @param minimumAmountDue    最低还款额（Minimum amount due）。
     * @param paymentDueDate      到期还款日（Payment due date）。
     * @param occurredAt          事件时间（Occurred timestamp）。
     */
    public CreditCardStatementGenerated(
            final StatementId statementId,
            final CreditCardAccountId creditCardAccountId,
            final DateRange statementPeriod,
            final Money totalAmountDue,
            final Money minimumAmountDue,
            final LocalDate paymentDueDate,
            final Instant occurredAt
    ) {
        this.statementId = Objects.requireNonNull(statementId, "Statement ID must not be null");
        this.creditCardAccountId = Objects.requireNonNull(creditCardAccountId, "Credit-card-account ID must not be null");
        this.statementPeriod = Objects.requireNonNull(statementPeriod, "Statement period must not be null");
        this.totalAmountDue = Objects.requireNonNull(totalAmountDue, "Total amount due must not be null");
        this.minimumAmountDue = Objects.requireNonNull(minimumAmountDue, "Minimum amount due must not be null");
        this.paymentDueDate = Objects.requireNonNull(paymentDueDate, "Payment due date must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
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
     * @brief 返回账期（Return Statement Period）；
     *        Return statement period range.
     *
     * @return 账期范围（Statement period）。
     */
    public DateRange statementPeriod() {
        return statementPeriod;
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
     * @brief 返回到期还款日（Return Payment Due Date）；
     *        Return payment due date.
     *
     * @return 到期还款日（Payment due date）。
     */
    public LocalDate paymentDueDate() {
        return paymentDueDate;
    }

    /**
     * @brief 返回事件时间（Return Occurred Timestamp）；
     *        Return occurred timestamp.
     *
     * @return 事件时间（Occurred timestamp）。
     */
    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
