package com.moesegfault.banking.domain.account;

import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 投资账户开立事件（Investment Account Opened Event）；
 *        Investment-account-opened domain event.
 */
public final class InvestmentAccountOpened implements DomainEvent {

    /**
     * @brief 投资账户 ID（Investment Account ID）；
     *        Investment-account identifier.
     */
    private final InvestmentAccountId investmentAccountId;

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
     * @brief 事件时间（Occurred Timestamp）；
     *        Event occurred timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造投资账户开立事件（Construct Investment Account Opened Event）；
     *        Construct investment-account-opened event.
     *
     * @param investmentAccountId 投资账户 ID（Investment-account ID）。
     * @param customerId          客户 ID（Customer ID）。
     * @param accountNo           账户号（Account number）。
     * @param occurredAt          事件时间（Occurred timestamp）。
     */
    public InvestmentAccountOpened(
            final InvestmentAccountId investmentAccountId,
            final CustomerId customerId,
            final AccountNumber accountNo,
            final Instant occurredAt
    ) {
        this.investmentAccountId = Objects.requireNonNull(
                investmentAccountId,
                "Investment account ID must not be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID must not be null");
        this.accountNo = Objects.requireNonNull(accountNo, "Account number must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
    }

    /**
     * @brief 返回投资账户 ID（Return Investment Account ID）；
     *        Return investment-account identifier.
     *
     * @return 投资账户 ID（Investment-account ID）。
     */
    public InvestmentAccountId investmentAccountId() {
        return investmentAccountId;
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
