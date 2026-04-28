package com.moesegfault.banking.domain.shared;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @brief 百分比值对象（Percentage Value Object），用于利率与费率建模；
 * Percentage value object for modeling rates and ratios.
 *
 * @note 内部采用十进制小数（Decimal Fraction）表示，例如 0.0350 表示 3.50%。
 * Internal representation is decimal fraction, e.g., 0.0350 means 3.50%.
 */
public final class Percentage implements Comparable<Percentage>, Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     * Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief 比例小数位（Percentage Scale），对齐 `NUMERIC(...,6)`；
     * Percentage scale aligned to `NUMERIC(...,6)`.
     */
    public static final int SCALE = 6;

    /**
     * @brief 比例小数值（Decimal Fraction Value）；
     * Decimal fraction value.
     */
    private final BigDecimal decimalValue;

    /**
     * @brief 构造百分比对象（Construct Percentage）；
     * Construct a percentage object.
     *
     * @param decimalValue 小数形式比例（Decimal fraction）。
     */
    private Percentage(final BigDecimal decimalValue) {
        this.decimalValue = normalize(decimalValue);
    }

    /**
     * @brief 以小数形式创建百分比（Factory from Decimal）；
     * Create percentage from decimal fraction.
     *
     * @param decimalValue 小数形式比例值（Decimal fraction value）。
     * @return 百分比值对象（Percentage value object）。
     */
    public static Percentage ofDecimal(final BigDecimal decimalValue) {
        return new Percentage(decimalValue);
    }

    /**
     * @brief 以百分数形式创建百分比（Factory from Percent）；
     * Create percentage from percent value.
     *
     * @param percentValue 百分数值（Percent value），例如 3.5 表示 3.5%。
     * @return 百分比值对象（Percentage value object）。
     */
    public static Percentage ofPercent(final BigDecimal percentValue) {
        Objects.requireNonNull(percentValue, "Percent value must not be null");
        final BigDecimal decimal = percentValue.divide(BigDecimal.valueOf(100), SCALE, RoundingMode.HALF_UP);
        return new Percentage(decimal);
    }

    /**
     * @brief 返回小数形式值（Return Decimal Fraction）；
     * Return decimal fraction value.
     *
     * @return 小数形式比例（Decimal fraction）。
     */
    public BigDecimal decimalValue() {
        return decimalValue;
    }

    /**
     * @brief 返回百分数形式值（Return Percent Value）；
     * Return percent value.
     *
     * @return 百分数值（Percent value）。
     */
    public BigDecimal percentValue() {
        return decimalValue.multiply(BigDecimal.valueOf(100)).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * @brief 将比例应用到金额（Apply to Money）；
     * Apply this percentage to a money value.
     *
     * @param money 金额对象（Money object）。
     * @return 计算后金额（Calculated money）。
     */
    public Money applyTo(final Money money) {
        Objects.requireNonNull(money, "Money must not be null");
        return money.multiply(this);
    }

    /**
     * @brief 比较比例大小（Compare Percentages）；
     * Compare percentage values.
     *
     * @param other 另一个比例（Another percentage）。
     * @return 比较结果（Comparison result）。
     */
    @Override
    public int compareTo(final Percentage other) {
        Objects.requireNonNull(other, "Other percentage must not be null");
        return decimalValue.compareTo(other.decimalValue);
    }

    /**
     * @brief 值对象相等判定（Value Object Equality）；
     * Value-object equality check.
     *
     * @param other 对比对象（Object to compare）。
     * @return 同值返回 true（true when equal）。
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Percentage that)) {
            return false;
        }
        return decimalValue.equals(that.decimalValue);
    }

    /**
     * @brief 计算哈希值（Compute Hash Code）；
     * Compute hash code.
     *
     * @return 哈希值（Hash code）。
     */
    @Override
    public int hashCode() {
        return Objects.hash(decimalValue);
    }

    /**
     * @brief 返回字符串表示（String Representation）；
     * Return string representation.
     *
     * @return 百分数字符串（Percent string）。
     */
    @Override
    public String toString() {
        return percentValue().stripTrailingZeros().toPlainString() + "%";
    }

    /**
     * @brief 标准化并校验比例值（Normalize and Validate Decimal Fraction）；
     * Normalize and validate decimal fraction value.
     *
     * @param rawValue 原始比例值（Raw fraction value）。
     * @return 标准化比例值（Normalized fraction value）。
     */
    private static BigDecimal normalize(final BigDecimal rawValue) {
        Objects.requireNonNull(rawValue, "Percentage decimal value must not be null");
        return rawValue.setScale(SCALE, RoundingMode.UNNECESSARY);
    }
}
