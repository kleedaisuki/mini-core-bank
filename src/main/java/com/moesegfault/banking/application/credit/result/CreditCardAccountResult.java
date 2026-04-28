package com.moesegfault.banking.application.credit.result;

import com.moesegfault.banking.domain.credit.CreditCardAccount;
import com.moesegfault.banking.domain.credit.CreditLimit;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @brief 信用卡账户应用结果（Credit Card Account Application Result），用于向展示层输出稳定账户视图；
 *        Credit-card-account application result for exposing stable account view to presentation layer.
 */
public final class CreditCardAccountResult {

    /**
     * @brief 信用卡账户 ID（Credit Card Account ID）；
     *        Credit-card-account identifier.
     */
    private final String creditCardAccountId;

    /**
     * @brief 总信用额度（Total Credit Limit）；
     *        Total credit limit amount.
     */
    private final BigDecimal creditLimit;

    /**
     * @brief 可用额度（Available Credit）；
     *        Available credit amount.
     */
    private final BigDecimal availableCredit;

    /**
     * @brief 已用额度（Used Credit）；
     *        Used credit amount.
     */
    private final BigDecimal usedCredit;

    /**
     * @brief 预借现金额度（Cash Advance Limit）；
     *        Cash-advance limit amount.
     */
    private final BigDecimal cashAdvanceLimit;

    /**
     * @brief 账单日（Billing Cycle Day）；
     *        Billing-cycle day.
     */
    private final int billingCycleDay;

    /**
     * @brief 到期还款日（Payment Due Day）；
     *        Payment-due day.
     */
    private final int paymentDueDay;

    /**
     * @brief 利率小数值（Interest Rate Decimal）；
     *        Interest-rate decimal value.
     */
    private final BigDecimal interestRateDecimal;

    /**
     * @brief 账户币种（Account Currency Code）；
     *        Account currency code.
     */
    private final String accountCurrencyCode;

    /**
     * @brief 构造信用卡账户应用结果（Construct Credit Card Account Result）；
     *        Construct credit-card-account application result.
     *
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @param creditLimit 总信用额度（Total credit limit）。
     * @param availableCredit 可用额度（Available credit）。
     * @param usedCredit 已用额度（Used credit）。
     * @param cashAdvanceLimit 预借现金额度（Cash-advance limit）。
     * @param billingCycleDay 账单日（Billing-cycle day）。
     * @param paymentDueDay 到期还款日（Payment-due day）。
     * @param interestRateDecimal 利率小数值（Interest-rate decimal value）。
     * @param accountCurrencyCode 账户币种（Account currency code）。
     */
    public CreditCardAccountResult(
            final String creditCardAccountId,
            final BigDecimal creditLimit,
            final BigDecimal availableCredit,
            final BigDecimal usedCredit,
            final BigDecimal cashAdvanceLimit,
            final int billingCycleDay,
            final int paymentDueDay,
            final BigDecimal interestRateDecimal,
            final String accountCurrencyCode
    ) {
        this.creditCardAccountId = requireText(creditCardAccountId, "creditCardAccountId");
        this.creditLimit = Objects.requireNonNull(creditLimit, "creditLimit must not be null");
        this.availableCredit = Objects.requireNonNull(availableCredit, "availableCredit must not be null");
        this.usedCredit = Objects.requireNonNull(usedCredit, "usedCredit must not be null");
        this.cashAdvanceLimit = Objects.requireNonNull(cashAdvanceLimit, "cashAdvanceLimit must not be null");
        this.billingCycleDay = billingCycleDay;
        this.paymentDueDay = paymentDueDay;
        this.interestRateDecimal = Objects.requireNonNull(interestRateDecimal, "interestRateDecimal must not be null");
        this.accountCurrencyCode = requireText(accountCurrencyCode, "accountCurrencyCode");
    }

    /**
     * @brief 由领域实体映射应用结果（Map from Domain Entity）；
     *        Map application result from domain entity.
     *
     * @param creditCardAccount 信用卡账户实体（Credit-card-account entity）。
     * @return 账户应用结果（Account application result）。
     */
    public static CreditCardAccountResult from(final CreditCardAccount creditCardAccount) {
        final CreditCardAccount normalized = Objects.requireNonNull(
                creditCardAccount,
                "creditCardAccount must not be null");
        final CreditLimit creditLimit = normalized.creditLimit();
        return new CreditCardAccountResult(
                normalized.creditCardAccountId().value(),
                creditLimit.totalLimit().amount(),
                creditLimit.availableLimit().amount(),
                creditLimit.usedAmount().amount(),
                creditLimit.cashAdvanceLimit().amount(),
                normalized.billingCycle().billingCycleDay(),
                normalized.billingCycle().paymentDueDay(),
                normalized.interestRate().rate().decimalValue(),
                normalized.accountCurrencyCode().value());
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
     * @brief 返回总信用额度（Return Total Credit Limit）；
     *        Return total credit limit.
     *
     * @return 总信用额度（Total credit limit）。
     */
    public BigDecimal creditLimit() {
        return creditLimit;
    }

    /**
     * @brief 返回可用额度（Return Available Credit）；
     *        Return available credit.
     *
     * @return 可用额度（Available credit）。
     */
    public BigDecimal availableCredit() {
        return availableCredit;
    }

    /**
     * @brief 返回已用额度（Return Used Credit）；
     *        Return used credit.
     *
     * @return 已用额度（Used credit）。
     */
    public BigDecimal usedCredit() {
        return usedCredit;
    }

    /**
     * @brief 返回预借现金额度（Return Cash Advance Limit）；
     *        Return cash-advance limit.
     *
     * @return 预借现金额度（Cash-advance limit）。
     */
    public BigDecimal cashAdvanceLimit() {
        return cashAdvanceLimit;
    }

    /**
     * @brief 返回账单日（Return Billing Cycle Day）；
     *        Return billing-cycle day.
     *
     * @return 账单日（Billing-cycle day）。
     */
    public int billingCycleDay() {
        return billingCycleDay;
    }

    /**
     * @brief 返回到期还款日（Return Payment Due Day）；
     *        Return payment-due day.
     *
     * @return 到期还款日（Payment-due day）。
     */
    public int paymentDueDay() {
        return paymentDueDay;
    }

    /**
     * @brief 返回利率小数值（Return Interest Rate Decimal）；
     *        Return interest-rate decimal value.
     *
     * @return 利率小数值（Interest-rate decimal value）。
     */
    public BigDecimal interestRateDecimal() {
        return interestRateDecimal;
    }

    /**
     * @brief 返回账户币种（Return Account Currency Code）；
     *        Return account currency code.
     *
     * @return 账户币种（Account currency code）。
     */
    public String accountCurrencyCode() {
        return accountCurrencyCode;
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
