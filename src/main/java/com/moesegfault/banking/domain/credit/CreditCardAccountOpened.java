package com.moesegfault.banking.domain.credit;

import com.moesegfault.banking.domain.account.CreditCardAccountId;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DomainEvent;
import com.moesegfault.banking.domain.shared.Money;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 信用卡账户开立事件（Credit Card Account Opened Event）；
 *        Credit-card-account-opened event.
 */
public final class CreditCardAccountOpened implements DomainEvent {

    /**
     * @brief 信用卡账户 ID（Credit Card Account ID）；
     *        Credit-card-account identifier.
     */
    private final CreditCardAccountId creditCardAccountId;

    /**
     * @brief 总信用额度（Total Credit Limit）；
     *        Total credit limit.
     */
    private final Money creditLimit;

    /**
     * @brief 可用信用额度（Available Credit）；
     *        Available credit.
     */
    private final Money availableCredit;

    /**
     * @brief 账户币种（Account Currency）；
     *        Account currency code.
     */
    private final CurrencyCode accountCurrencyCode;

    /**
     * @brief 事件发生时间（Occurred Timestamp）；
     *        Event occurred timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造信用卡账户开立事件（Construct Account Opened Event）；
     *        Construct credit-card-account-opened event.
     *
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @param creditLimit         总额度（Total credit limit）。
     * @param availableCredit     可用额度（Available credit）。
     * @param accountCurrencyCode 账户币种（Account currency）。
     * @param occurredAt          事件时间（Occurred timestamp）。
     */
    public CreditCardAccountOpened(
            final CreditCardAccountId creditCardAccountId,
            final Money creditLimit,
            final Money availableCredit,
            final CurrencyCode accountCurrencyCode,
            final Instant occurredAt
    ) {
        this.creditCardAccountId = Objects.requireNonNull(creditCardAccountId, "Credit-card-account ID must not be null");
        this.creditLimit = Objects.requireNonNull(creditLimit, "Credit limit must not be null");
        this.availableCredit = Objects.requireNonNull(availableCredit, "Available credit must not be null");
        this.accountCurrencyCode = Objects.requireNonNull(accountCurrencyCode, "Account currency must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
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
     * @brief 返回总额度（Return Total Credit Limit）；
     *        Return total credit limit.
     *
     * @return 总额度（Total credit limit）。
     */
    public Money creditLimit() {
        return creditLimit;
    }

    /**
     * @brief 返回可用额度（Return Available Credit）；
     *        Return available credit.
     *
     * @return 可用额度（Available credit）。
     */
    public Money availableCredit() {
        return availableCredit;
    }

    /**
     * @brief 返回账户币种（Return Account Currency）；
     *        Return account currency code.
     *
     * @return 账户币种（Account currency）。
     */
    public CurrencyCode accountCurrencyCode() {
        return accountCurrencyCode;
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
