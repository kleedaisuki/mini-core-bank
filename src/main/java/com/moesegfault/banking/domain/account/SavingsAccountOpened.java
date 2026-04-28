package com.moesegfault.banking.domain.account;

import com.moesegfault.banking.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 储蓄账户开立事件（Savings Account Opened Event）；
 *        Savings-account-opened domain event.
 */
public final class SavingsAccountOpened implements DomainEvent {

    /**
     * @brief 储蓄账户 ID（Savings Account ID）；
     *        Savings-account identifier.
     */
    private final SavingsAccountId savingsAccountId;

    /**
     * @brief 客户 ID（Customer ID）；
     *        Owner customer identifier.
     */
    private final CustomerId customerId;

    /**
     * @brief 账户号（Account Number）；
     *        Account number.
     */
    private final AccountNumber accountNo;

    /**
     * @brief 事件发生时间（Occurred Timestamp）；
     *        Event occurred timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造储蓄账户开立事件（Construct Savings Account Opened Event）；
     *        Construct savings-account-opened event.
     *
     * @param savingsAccountId 储蓄账户 ID（Savings-account ID）。
     * @param customerId       客户 ID（Customer ID）。
     * @param accountNo        账户号（Account number）。
     * @param occurredAt       事件时间（Occurred timestamp）。
     */
    public SavingsAccountOpened(
            final SavingsAccountId savingsAccountId,
            final CustomerId customerId,
            final AccountNumber accountNo,
            final Instant occurredAt
    ) {
        this.savingsAccountId = Objects.requireNonNull(savingsAccountId, "Savings account ID must not be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID must not be null");
        this.accountNo = Objects.requireNonNull(accountNo, "Account number must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
    }

    /**
     * @brief 返回储蓄账户 ID（Return Savings Account ID）；
     *        Return savings-account identifier.
     *
     * @return 储蓄账户 ID（Savings-account ID）。
     */
    public SavingsAccountId savingsAccountId() {
        return savingsAccountId;
    }

    /**
     * @brief 返回客户 ID（Return Customer ID）；
     *        Return owner customer identifier.
     *
     * @return 客户 ID（Customer ID）。
     */
    public CustomerId customerId() {
        return customerId;
    }

    /**
     * @brief 返回账户号（Return Account Number）；
     *        Return account number.
     *
     * @return 账户号（Account number）。
     */
    public AccountNumber accountNo() {
        return accountNo;
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
