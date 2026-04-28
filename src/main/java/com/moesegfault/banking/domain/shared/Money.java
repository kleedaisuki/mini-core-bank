package com.moesegfault.banking.domain.shared;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @brief 金额值对象（Money Value Object），对齐多数金额字段 `NUMERIC(19,4)` 约束；
 *        Money value object aligned with most amount fields using
 *        `NUMERIC(19,4)` constraints.
 */
public final class Money implements Comparable<Money>, Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief 金额统一小数位（Unified Money Scale）；
     *        Unified decimal scale for money values.
     */
    public static final int SCALE = 4;

    /**
     * @brief 金额最大精度（Maximum Precision）对应 `NUMERIC(19,4)`；
     *        Maximum precision aligned to `NUMERIC(19,4)`.
     */
    public static final int MAX_PRECISION = 19;

    /**
     * @brief 金额值（Amount Value）；
     *        Amount value.
     */
    private final BigDecimal amount;

    /**
     * @brief 币种代码（Currency Code）；
     *        Currency code.
     */
    private final CurrencyCode currencyCode;

    /**
     * @brief 构造金额对象（Construct Money）；
     *        Construct a money object.
     *
     * @param currencyCode 币种代码（Currency code）。
     * @param amount       金额值（Amount value）。
     */
    private Money(final CurrencyCode currencyCode, final BigDecimal amount) {
        this.currencyCode = Objects.requireNonNull(currencyCode, "Currency code must not be null");
        this.amount = normalize(amount);
    }

    /**
     * @brief 创建金额值对象（Factory Method）；
     *        Create a money value object.
     *
     * @param currencyCode 币种代码（Currency code）。
     * @param amount       金额（Amount）。
     * @return 金额值对象（Money value object）。
     */
    public static Money of(final CurrencyCode currencyCode, final BigDecimal amount) {
        return new Money(currencyCode, amount);
    }

    /**
     * @brief 创建零金额（Create Zero Amount）；
     *        Create a zero amount.
     *
     * @param currencyCode 币种代码（Currency code）。
     * @return 零金额对象（Zero money object）。
     */
    public static Money zero(final CurrencyCode currencyCode) {
        return new Money(currencyCode, BigDecimal.ZERO);
    }

    /**
     * @brief 返回币种代码（Return Currency Code）；
     *        Return currency code.
     *
     * @return 币种代码（Currency code）。
     */
    public CurrencyCode currencyCode() {
        return currencyCode;
    }

    /**
     * @brief 返回金额值（Return Amount Value）；
     *        Return amount value.
     *
     * @return 金额值（Amount value）。
     */
    public BigDecimal amount() {
        return amount;
    }

    /**
     * @brief 金额相加（Add Money）；
     *        Add money values.
     *
     * @param other 另一个金额（Another money amount）。
     * @return 相加结果（Sum result）。
     */
    public Money add(final Money other) {
        requireSameCurrency(other);
        return new Money(currencyCode, amount.add(other.amount));
    }

    /**
     * @brief 金额相减（Subtract Money）；
     *        Subtract money values.
     *
     * @param other 另一个金额（Another money amount）。
     * @return 相减结果（Difference result）。
     */
    public Money subtract(final Money other) {
        requireSameCurrency(other);
        return new Money(currencyCode, amount.subtract(other.amount));
    }

    /**
     * @brief 按比例计算金额（Apply Percentage）；
     *        Apply a percentage to this money amount.
     *
     * @param percentage 比例值（Percentage value）。
     * @return 计算后的金额（Calculated money）。
     */
    public Money multiply(final Percentage percentage) {
        Objects.requireNonNull(percentage, "Percentage must not be null");
        final BigDecimal calculated = amount.multiply(percentage.decimalValue());
        return new Money(currencyCode, calculated.setScale(SCALE, RoundingMode.HALF_UP));
    }

    /**
     * @brief 取相反数（Negate Amount）；
     *        Negate this amount.
     *
     * @return 相反数金额（Negated money）。
     */
    public Money negate() {
        return new Money(currencyCode, amount.negate());
    }

    /**
     * @brief 取绝对值（Absolute Amount）；
     *        Return absolute amount.
     *
     * @return 绝对值金额（Absolute money）。
     */
    public Money abs() {
        return new Money(currencyCode, amount.abs());
    }

    /**
     * @brief 判断是否为正数金额（Check Positive Amount）；
     *        Check whether the amount is positive.
     *
     * @return 大于 0 返回 true（true when amount > 0）。
     */
    public boolean isPositive() {
        return amount.signum() > 0;
    }

    /**
     * @brief 判断是否为负数金额（Check Negative Amount）；
     *        Check whether the amount is negative.
     *
     * @return 小于 0 返回 true（true when amount < 0）。
     */
    public boolean isNegative() {
        return amount.signum() < 0;
    }

    /**
     * @brief 判断是否为零金额（Check Zero Amount）；
     *        Check whether the amount is zero.
     *
     * @return 等于 0 返回 true（true when amount == 0）。
     */
    public boolean isZero() {
        return amount.signum() == 0;
    }

    /**
     * @brief 比较金额大小（Compare Money）；
     *        Compare money amounts.
     *
     * @param other 另一个金额（Another money）。
     * @return 比较结果（Comparison result）。
     */
    @Override
    public int compareTo(final Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount);
    }

    /**
     * @brief 值对象相等判定（Value Object Equality）；
     *        Value-object equality check.
     *
     * @param other 对比对象（Object to compare）。
     * @return 币种与金额同值返回 true（true when currency and amount are equal）。
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Money money)) {
            return false;
        }
        return amount.equals(money.amount) && currencyCode.equals(money.currencyCode);
    }

    /**
     * @brief 计算哈希值（Compute Hash Code）；
     *        Compute hash code.
     *
     * @return 哈希值（Hash code）。
     */
    @Override
    public int hashCode() {
        return Objects.hash(amount, currencyCode);
    }

    /**
     * @brief 返回字符串表示（String Representation）；
     *        Return string representation.
     *
     * @return `CURRENCY amount` 格式字符串（String in `CURRENCY amount` format）。
     */
    @Override
    public String toString() {
        return currencyCode + " " + amount.toPlainString();
    }

    /**
     * @brief 要求币种一致（Require Same Currency）；
     *        Require same currency for monetary operations.
     *
     * @param other 另一个金额（Another money）。
     */
    private void requireSameCurrency(final Money other) {
        Objects.requireNonNull(other, "Money must not be null");
        if (!currencyCode.equals(other.currencyCode)) {
            throw new BusinessRuleViolation(
                    "Money currency mismatch: " + currencyCode + " vs " + other.currencyCode);
        }
    }

    /**
     * @brief 标准化并校验金额（Normalize and Validate Amount）；
     *        Normalize and validate amount.
     *
     * @param rawAmount 原始金额（Raw amount）。
     * @return 标准化金额（Normalized amount）。
     */
    private static BigDecimal normalize(final BigDecimal rawAmount) {
        if (rawAmount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        final BigDecimal normalized = rawAmount.setScale(SCALE, RoundingMode.UNNECESSARY);
        if (normalized.precision() > MAX_PRECISION) {
            throw new IllegalArgumentException(
                    "Amount precision exceeds NUMERIC(19,4): " + rawAmount.toPlainString());
        }
        return normalized;
    }
}
