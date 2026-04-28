package com.moesegfault.banking.domain.credit;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import java.util.Objects;

/**
 * @brief 信用额度值对象（Credit Limit Value Object），对齐 `credit_card_account` 的额度字段；
 *        Credit-limit value object aligned with limit fields in `credit_card_account`.
 */
public final class CreditLimit {

    /**
     * @brief 总信用额度（Total Credit Limit）；
     *        Total credit limit.
     */
    private final Money totalLimit;

    /**
     * @brief 可用信用额度（Available Credit Limit）；
     *        Available credit limit.
     */
    private final Money availableLimit;

    /**
     * @brief 预借现金额度（Cash Advance Limit）；
     *        Cash advance limit.
     */
    private final Money cashAdvanceLimit;

    /**
     * @brief 构造信用额度值对象（Construct Credit Limit Value Object）；
     *        Construct a credit-limit value object.
     *
     * @param totalLimit       总额度（Total limit）。
     * @param availableLimit   可用额度（Available limit）。
     * @param cashAdvanceLimit 预借现金额度（Cash advance limit）。
     */
    private CreditLimit(
            final Money totalLimit,
            final Money availableLimit,
            final Money cashAdvanceLimit
    ) {
        this.totalLimit = Objects.requireNonNull(totalLimit, "Total limit must not be null");
        this.availableLimit = Objects.requireNonNull(availableLimit, "Available limit must not be null");
        this.cashAdvanceLimit = Objects.requireNonNull(cashAdvanceLimit, "Cash-advance limit must not be null");
        ensureConsistentCurrency(this.totalLimit, this.availableLimit, this.cashAdvanceLimit);
        ensureNonNegative(this.totalLimit, "Total credit limit");
        ensureNonNegative(this.availableLimit, "Available credit limit");
        ensureNonNegative(this.cashAdvanceLimit, "Cash-advance limit");
        if (this.availableLimit.compareTo(this.totalLimit) > 0) {
            throw new BusinessRuleViolation("Available credit must not exceed total credit limit");
        }
        if (this.cashAdvanceLimit.compareTo(this.totalLimit) > 0) {
            throw new BusinessRuleViolation("Cash-advance limit must not exceed total credit limit");
        }
    }

    /**
     * @brief 创建信用额度值对象（Factory Method）；
     *        Create a credit-limit value object.
     *
     * @param totalLimit       总额度（Total limit）。
     * @param availableLimit   可用额度（Available limit）。
     * @param cashAdvanceLimit 预借现金额度（Cash advance limit）。
     * @return 信用额度值对象（Credit-limit value object）。
     */
    public static CreditLimit of(
            final Money totalLimit,
            final Money availableLimit,
            final Money cashAdvanceLimit
    ) {
        return new CreditLimit(totalLimit, availableLimit, cashAdvanceLimit);
    }

    /**
     * @brief 初始化额度（Initialize Credit Limit）；
     *        Initialize credit limit with full available amount.
     *
     * @param totalLimit       总额度（Total limit）。
     * @param cashAdvanceLimit 预借现金额度（Cash advance limit）。
     * @return 初始化后的信用额度（Initialized credit limit）。
     */
    public static CreditLimit initialize(
            final Money totalLimit,
            final Money cashAdvanceLimit
    ) {
        return new CreditLimit(totalLimit, totalLimit, cashAdvanceLimit);
    }

    /**
     * @brief 授信消费占用额度（Consume Available Credit）；
     *        Consume available credit by an authorized amount.
     *
     * @param amount 占用金额（Consumed amount）。
     * @return 更新后的额度值对象（Updated credit-limit value object）。
     */
    public CreditLimit consume(final Money amount) {
        final Money normalized = requirePositiveSameCurrency(amount, "Consumed amount");
        if (normalized.compareTo(availableLimit) > 0) {
            throw new BusinessRuleViolation("Consumed amount exceeds available credit");
        }
        return new CreditLimit(totalLimit, availableLimit.subtract(normalized), cashAdvanceLimit);
    }

    /**
     * @brief 释放可用额度（Release Available Credit）；
     *        Release previously occupied credit.
     *
     * @param amount 释放金额（Released amount）。
     * @return 更新后的额度值对象（Updated credit-limit value object）。
     */
    public CreditLimit release(final Money amount) {
        final Money normalized = requirePositiveSameCurrency(amount, "Released amount");
        final Money increased = availableLimit.add(normalized);
        if (increased.compareTo(totalLimit) > 0) {
            throw new BusinessRuleViolation("Released amount would exceed total credit limit");
        }
        return new CreditLimit(totalLimit, increased, cashAdvanceLimit);
    }

    /**
     * @brief 调整总额度和预借现金额度（Adjust Total and Cash-advance Limits）；
     *        Adjust total limit and cash-advance limit while preserving used amount.
     *
     * @param newTotalLimit       新总额度（New total limit）。
     * @param newCashAdvanceLimit 新预借现金额度（New cash-advance limit）。
     * @return 更新后的额度值对象（Updated credit-limit value object）。
     */
    public CreditLimit adjust(
            final Money newTotalLimit,
            final Money newCashAdvanceLimit
    ) {
        final Money normalizedTotal = Objects.requireNonNull(newTotalLimit, "New total limit must not be null");
        final Money normalizedCashAdvance = Objects.requireNonNull(
                newCashAdvanceLimit,
                "New cash-advance limit must not be null");
        ensureConsistentCurrency(totalLimit, normalizedTotal, normalizedCashAdvance);
        ensureNonNegative(normalizedTotal, "New total credit limit");
        ensureNonNegative(normalizedCashAdvance, "New cash-advance limit");
        final Money usedAmount = usedAmount();
        if (normalizedTotal.compareTo(usedAmount) < 0) {
            throw new BusinessRuleViolation("New total credit limit must be >= used credit");
        }
        final Money adjustedAvailable = normalizedTotal.subtract(usedAmount);
        return new CreditLimit(normalizedTotal, adjustedAvailable, normalizedCashAdvance);
    }

    /**
     * @brief 返回已用额度（Return Used Credit）；
     *        Return used credit amount.
     *
     * @return 已用额度（Used credit amount）。
     */
    public Money usedAmount() {
        return totalLimit.subtract(availableLimit);
    }

    /**
     * @brief 返回总额度（Return Total Limit）；
     *        Return total credit limit.
     *
     * @return 总额度（Total limit）。
     */
    public Money totalLimit() {
        return totalLimit;
    }

    /**
     * @brief 返回可用额度（Return Available Limit）；
     *        Return available credit limit.
     *
     * @return 可用额度（Available limit）。
     */
    public Money availableLimit() {
        return availableLimit;
    }

    /**
     * @brief 返回预借现金额度（Return Cash-advance Limit）；
     *        Return cash-advance limit.
     *
     * @return 预借现金额度（Cash-advance limit）。
     */
    public Money cashAdvanceLimit() {
        return cashAdvanceLimit;
    }

    /**
     * @brief 返回额度币种（Return Limit Currency）；
     *        Return currency of this credit-limit object.
     *
     * @return 币种代码（Currency code）。
     */
    public CurrencyCode currencyCode() {
        return totalLimit.currencyCode();
    }

    /**
     * @brief 值对象相等判定（Value Object Equality）；
     *        Value-object equality check.
     *
     * @param other 对比对象（Object to compare）。
     * @return 同值返回 true（true when equal）。
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CreditLimit that)) {
            return false;
        }
        return totalLimit.equals(that.totalLimit)
                && availableLimit.equals(that.availableLimit)
                && cashAdvanceLimit.equals(that.cashAdvanceLimit);
    }

    /**
     * @brief 计算哈希值（Compute Hash Code）；
     *        Compute hash code.
     *
     * @return 哈希值（Hash code）。
     */
    @Override
    public int hashCode() {
        return Objects.hash(totalLimit, availableLimit, cashAdvanceLimit);
    }

    /**
     * @brief 返回字符串表示（String Representation）；
     *        Return string representation.
     *
     * @return 字符串表示（String representation）。
     */
    @Override
    public String toString() {
        return "CreditLimit{total="
                + totalLimit
                + ", available="
                + availableLimit
                + ", cashAdvance="
                + cashAdvanceLimit
                + "}";
    }

    /**
     * @brief 校验金额非负（Ensure Non-negative Money）；
     *        Ensure money amount is non-negative.
     *
     * @param money     金额（Money amount）。
     * @param fieldName 字段名称（Field name）。
     */
    private static void ensureNonNegative(
            final Money money,
            final String fieldName
    ) {
        if (money.isNegative()) {
            throw new BusinessRuleViolation(fieldName + " must not be negative");
        }
    }

    /**
     * @brief 校验三者币种一致（Ensure Currency Consistency）；
     *        Ensure all given money amounts share the same currency.
     *
     * @param first  第一个金额（First amount）。
     * @param second 第二个金额（Second amount）。
     * @param third  第三个金额（Third amount）。
     */
    private static void ensureConsistentCurrency(
            final Money first,
            final Money second,
            final Money third
    ) {
        if (!first.currencyCode().equals(second.currencyCode()) || !first.currencyCode().equals(third.currencyCode())) {
            throw new BusinessRuleViolation("Credit-limit currencies must be consistent");
        }
    }

    /**
     * @brief 校验正数且币种一致（Require Positive Same-currency Amount）；
     *        Require a positive amount with consistent currency.
     *
     * @param amount    金额（Amount）。
     * @param fieldName 字段名称（Field name）。
     * @return 标准化金额（Normalized amount）。
     */
    private Money requirePositiveSameCurrency(
            final Money amount,
            final String fieldName
    ) {
        final Money normalized = Objects.requireNonNull(amount, fieldName + " must not be null");
        if (!normalized.currencyCode().equals(currencyCode())) {
            throw new BusinessRuleViolation(fieldName + " currency must match credit-limit currency");
        }
        if (!normalized.isPositive()) {
            throw new BusinessRuleViolation(fieldName + " must be positive");
        }
        return normalized;
    }
}
