package com.moesegfault.banking.domain.account;

import com.moesegfault.banking.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 外汇账户开立事件（FX Account Opened Event）；
 *        FX-account-opened domain event.
 */
public final class FxAccountOpened implements DomainEvent {

    /**
     * @brief 外汇账户 ID（FX Account ID）；
     *        FX-account identifier.
     */
    private final FxAccountId fxAccountId;

    /**
     * @brief 客户 ID（Customer ID）；
     *        Owner customer identifier.
     */
    private final CustomerId customerId;

    /**
     * @brief 绑定储蓄账户 ID（Linked Savings Account ID）；
     *        Linked savings-account identifier.
     */
    private final SavingsAccountId linkedSavingsAccountId;

    /**
     * @brief 账户号（Account Number）；
     *        Account number.
     */
    private final AccountNumber accountNo;

    /**
     * @brief 事件时间（Occurred Timestamp）；
     *        Event occurred timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造外汇账户开立事件（Construct FX Account Opened Event）；
     *        Construct FX-account-opened event.
     *
     * @param fxAccountId             外汇账户 ID（FX-account ID）。
     * @param customerId              客户 ID（Customer ID）。
     * @param linkedSavingsAccountId  绑定储蓄账户 ID（Linked savings-account ID）。
     * @param accountNo               账户号（Account number）。
     * @param occurredAt              事件时间（Occurred timestamp）。
     */
    public FxAccountOpened(
            final FxAccountId fxAccountId,
            final CustomerId customerId,
            final SavingsAccountId linkedSavingsAccountId,
            final AccountNumber accountNo,
            final Instant occurredAt
    ) {
        this.fxAccountId = Objects.requireNonNull(fxAccountId, "FX account ID must not be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID must not be null");
        this.linkedSavingsAccountId = Objects.requireNonNull(
                linkedSavingsAccountId,
                "Linked savings account ID must not be null");
        this.accountNo = Objects.requireNonNull(accountNo, "Account number must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
    }

    /**
     * @brief 返回外汇账户 ID（Return FX Account ID）；
     *        Return FX-account identifier.
     *
     * @return 外汇账户 ID（FX-account ID）。
     */
    public FxAccountId fxAccountId() {
        return fxAccountId;
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
     * @brief 返回绑定储蓄账户 ID（Return Linked Savings Account ID）；
     *        Return linked savings-account identifier.
     *
     * @return 绑定储蓄账户 ID（Linked savings-account ID）。
     */
    public SavingsAccountId linkedSavingsAccountId() {
        return linkedSavingsAccountId;
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
