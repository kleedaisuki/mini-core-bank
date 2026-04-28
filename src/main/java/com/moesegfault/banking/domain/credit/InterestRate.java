package com.moesegfault.banking.domain.credit;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.Money;
import com.moesegfault.banking.domain.shared.Percentage;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @brief 利率值对象（Interest Rate Value Object），对齐 `interest_rate` 非负约束；
 *        Interest-rate value object aligned with non-negative `interest_rate` constraint.
 */
public final class InterestRate {

    /**
     * @brief 利率小数值（Decimal Interest Rate）；
     *        Decimal interest-rate value.
     */
    private final Percentage rate;

    /**
     * @brief 构造利率值对象（Construct Interest Rate Value Object）；
     *        Construct an interest-rate value object.
     *
     * @param rate 利率（Interest rate）。
     */
    private InterestRate(final Percentage rate) {
        this.rate = Objects.requireNonNull(rate, "Interest rate must not be null");
        if (this.rate.decimalValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleViolation("Interest rate must not be negative");
        }
    }

    /**
     * @brief 由比例对象创建利率（Factory from Percentage）；
     *        Create interest rate from percentage object.
     *
     * @param rate 利率比例（Rate percentage）。
     * @return 利率值对象（Interest-rate value object）。
     */
    public static InterestRate of(final Percentage rate) {
        return new InterestRate(rate);
    }

    /**
     * @brief 由小数值创建利率（Factory from Decimal）；
     *        Create interest rate from decimal fraction.
     *
     * @param decimalRate 小数利率（Decimal rate）。
     * @return 利率值对象（Interest-rate value object）。
     */
    public static InterestRate ofDecimal(final BigDecimal decimalRate) {
        return new InterestRate(Percentage.ofDecimal(decimalRate));
    }

    /**
     * @brief 由百分数创建利率（Factory from Percent）；
     *        Create interest rate from percent value.
     *
     * @param percentRate 百分数利率（Percent rate）。
     * @return 利率值对象（Interest-rate value object）。
     */
    public static InterestRate ofPercent(final BigDecimal percentRate) {
        return new InterestRate(Percentage.ofPercent(percentRate));
    }

    /**
     * @brief 对本金计算利息（Accrue Interest on Principal）；
     *        Accrue interest on principal amount.
     *
     * @param principal 本金金额（Principal amount）。
     * @return 利息金额（Interest amount）。
     */
    public Money accrue(final Money principal) {
        final Money normalized = Objects.requireNonNull(principal, "Principal must not be null");
        if (normalized.isNegative()) {
            throw new BusinessRuleViolation("Principal must not be negative when accruing interest");
        }
        return rate.applyTo(normalized);
    }

    /**
     * @brief 返回利率比例对象（Return Rate Percentage）；
     *        Return rate percentage object.
     *
     * @return 利率比例（Rate percentage）。
     */
    public Percentage rate() {
        return rate;
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
        if (!(other instanceof InterestRate that)) {
            return false;
        }
        return rate.equals(that.rate);
    }

    /**
     * @brief 计算哈希值（Compute Hash Code）；
     *        Compute hash code.
     *
     * @return 哈希值（Hash code）。
     */
    @Override
    public int hashCode() {
        return Objects.hash(rate);
    }

    /**
     * @brief 返回字符串表示（String Representation）；
     *        Return string representation.
     *
     * @return 字符串表示（String representation）。
     */
    @Override
    public String toString() {
        return "InterestRate{" + rate + "}";
    }
}
