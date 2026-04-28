package com.moesegfault.banking.domain.credit;

import com.moesegfault.banking.domain.account.CreditCardAccountId;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 信用卡账户实体（Credit Card Account Entity），对齐 `credit_card_account` 核心字段与约束；
 *        Credit-card-account entity aligned with core fields and constraints in `credit_card_account`.
 */
public final class CreditCardAccount {

    /**
     * @brief 信用卡账户 ID（Credit Card Account ID）；
     *        Credit-card-account identifier.
     */
    private final CreditCardAccountId creditCardAccountId;

    /**
     * @brief 信用额度（Credit Limit）；
     *        Credit-limit object.
     */
    private CreditLimit creditLimit;

    /**
     * @brief 账单周期（Billing Cycle）；
     *        Billing-cycle object.
     */
    private final BillingCycle billingCycle;

    /**
     * @brief 利率（Interest Rate）；
     *        Interest-rate object.
     */
    private final InterestRate interestRate;

    /**
     * @brief 账户币种（Account Currency Code）；
     *        Account currency code.
     */
    private final CurrencyCode accountCurrencyCode;

    /**
     * @brief 构造信用卡账户实体（Construct Credit Card Account Entity）；
     *        Construct credit-card-account entity.
     *
     * @param creditCardAccountId 账户 ID（Account ID）。
     * @param creditLimit         信用额度（Credit limit）。
     * @param billingCycle        账单周期（Billing cycle）。
     * @param interestRate        利率（Interest rate）。
     * @param accountCurrencyCode 账户币种（Account currency）。
     */
    private CreditCardAccount(
            final CreditCardAccountId creditCardAccountId,
            final CreditLimit creditLimit,
            final BillingCycle billingCycle,
            final InterestRate interestRate,
            final CurrencyCode accountCurrencyCode
    ) {
        this.creditCardAccountId = Objects.requireNonNull(creditCardAccountId, "Credit-card-account ID must not be null");
        this.creditLimit = Objects.requireNonNull(creditLimit, "Credit limit must not be null");
        this.billingCycle = Objects.requireNonNull(billingCycle, "Billing cycle must not be null");
        this.interestRate = Objects.requireNonNull(interestRate, "Interest rate must not be null");
        this.accountCurrencyCode = Objects.requireNonNull(accountCurrencyCode, "Account currency must not be null");
        if (!this.creditLimit.currencyCode().equals(this.accountCurrencyCode)) {
            throw new BusinessRuleViolation("Credit-limit currency must match account currency");
        }
    }

    /**
     * @brief 开立信用卡账户（Open Credit Card Account）；
     *        Open a credit-card account with full available credit.
     *
     * @param creditCardAccountId 账户 ID（Account ID）。
     * @param totalCreditLimit    总信用额度（Total credit limit）。
     * @param billingCycle        账单周期（Billing cycle）。
     * @param interestRate        利率（Interest rate）。
     * @param cashAdvanceLimit    预借现金额度（Cash-advance limit）。
     * @param accountCurrencyCode 账户币种（Account currency）。
     * @return 新建账户实体（New account entity）。
     */
    public static CreditCardAccount open(
            final CreditCardAccountId creditCardAccountId,
            final Money totalCreditLimit,
            final BillingCycle billingCycle,
            final InterestRate interestRate,
            final Money cashAdvanceLimit,
            final CurrencyCode accountCurrencyCode
    ) {
        final CreditLimit initializedLimit = CreditLimit.initialize(
                Objects.requireNonNull(totalCreditLimit, "Total credit limit must not be null"),
                Objects.requireNonNull(cashAdvanceLimit, "Cash-advance limit must not be null"));
        return new CreditCardAccount(
                creditCardAccountId,
                initializedLimit,
                billingCycle,
                interestRate,
                accountCurrencyCode);
    }

    /**
     * @brief 从持久化状态重建信用卡账户（Restore Credit Card Account）；
     *        Restore credit-card account from persistence state.
     *
     * @param creditCardAccountId 账户 ID（Account ID）。
     * @param creditLimit         信用额度（Credit limit）。
     * @param billingCycle        账单周期（Billing cycle）。
     * @param interestRate        利率（Interest rate）。
     * @param accountCurrencyCode 账户币种（Account currency）。
     * @return 重建后的账户实体（Restored account entity）。
     */
    public static CreditCardAccount restore(
            final CreditCardAccountId creditCardAccountId,
            final CreditLimit creditLimit,
            final BillingCycle billingCycle,
            final InterestRate interestRate,
            final CurrencyCode accountCurrencyCode
    ) {
        return new CreditCardAccount(
                creditCardAccountId,
                creditLimit,
                billingCycle,
                interestRate,
                accountCurrencyCode);
    }

    /**
     * @brief 授权消费并占用额度（Authorize Charge and Consume Credit）；
     *        Authorize a charge and consume available credit.
     *
     * @param amount 金额（Amount）。
     */
    public void authorizeCharge(final Money amount) {
        creditLimit = creditLimit.consume(amount);
    }

    /**
     * @brief 释放额度（Release Credit）；
     *        Release previously consumed credit.
     *
     * @param amount 金额（Amount）。
     */
    public void releaseCredit(final Money amount) {
        creditLimit = creditLimit.release(amount);
    }

    /**
     * @brief 处理还款回补额度（Receive Repayment and Restore Credit）；
     *        Receive repayment and restore available credit.
     *
     * @param amount 还款金额（Repayment amount）。
     */
    public void receiveRepayment(final Money amount) {
        creditLimit = creditLimit.release(amount);
    }

    /**
     * @brief 计提利息并占用额度（Accrue Interest and Consume Credit）；
     *        Accrue interest and consume available credit.
     *
     * @param principal 计息本金（Interest principal）。
     * @return 计提利息金额（Accrued interest amount）。
     */
    public Money accrueInterestOn(final Money principal) {
        final Money interest = interestRate.accrue(principal);
        if (interest.isZero()) {
            return interest;
        }
        creditLimit = creditLimit.consume(interest);
        return interest;
    }

    /**
     * @brief 调整额度（Adjust Credit Limit）；
     *        Adjust total and cash-advance limits.
     *
     * @param newTotalLimit       新总额度（New total limit）。
     * @param newCashAdvanceLimit 新预借现金额度（New cash-advance limit）。
     */
    public void adjustCreditLimit(
            final Money newTotalLimit,
            final Money newCashAdvanceLimit
    ) {
        creditLimit = creditLimit.adjust(newTotalLimit, newCashAdvanceLimit);
    }

    /**
     * @brief 判断是否可授权指定金额（Check Charge Authorization Feasibility）；
     *        Check whether a charge amount can be authorized.
     *
     * @param amount 金额（Amount）。
     * @return 可授权返回 true（true when authorizable）。
     */
    public boolean canAuthorize(final Money amount) {
        final Money normalized = Objects.requireNonNull(amount, "Amount must not be null");
        if (!normalized.currencyCode().equals(accountCurrencyCode)) {
            return false;
        }
        if (!normalized.isPositive()) {
            return false;
        }
        return normalized.compareTo(creditLimit.availableLimit()) <= 0;
    }

    /**
     * @brief 构建账户开立事件（Build Account Opened Event）；
     *        Build credit-card-account-opened domain event.
     *
     * @return 账户开立事件（Account-opened event）。
     */
    public CreditCardAccountOpened openedEvent() {
        return new CreditCardAccountOpened(
                creditCardAccountId,
                creditLimit.totalLimit(),
                creditLimit.availableLimit(),
                accountCurrencyCode,
                Instant.now());
    }

    /**
     * @brief 返回账户 ID（Return Account ID）；
     *        Return account identifier.
     *
     * @return 账户 ID（Account ID）。
     */
    public CreditCardAccountId creditCardAccountId() {
        return creditCardAccountId;
    }

    /**
     * @brief 返回额度对象（Return Credit Limit）；
     *        Return credit-limit object.
     *
     * @return 信用额度（Credit limit）。
     */
    public CreditLimit creditLimit() {
        return creditLimit;
    }

    /**
     * @brief 返回账单周期（Return Billing Cycle）；
     *        Return billing-cycle object.
     *
     * @return 账单周期（Billing cycle）。
     */
    public BillingCycle billingCycle() {
        return billingCycle;
    }

    /**
     * @brief 返回利率（Return Interest Rate）；
     *        Return interest-rate object.
     *
     * @return 利率（Interest rate）。
     */
    public InterestRate interestRate() {
        return interestRate;
    }

    /**
     * @brief 返回账户币种（Return Account Currency）；
     *        Return account currency code.
     *
     * @return 账户币种（Account currency code）。
     */
    public CurrencyCode accountCurrencyCode() {
        return accountCurrencyCode;
    }
}
