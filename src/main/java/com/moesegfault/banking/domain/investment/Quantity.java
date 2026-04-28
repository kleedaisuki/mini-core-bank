package com.moesegfault.banking.domain.investment;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @brief 份额值对象（Quantity Value Object），对齐 `NUMERIC(19,6)` 且禁止负数；
 *        Quantity value object aligned with `NUMERIC(19,6)` and forbidding negative values.
 */
public final class Quantity implements Comparable<Quantity>, Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief 小数位（Scale）对齐 `NUMERIC(19,6)`；
     *        Decimal scale aligned with `NUMERIC(19,6)`.
     */
    public static final int SCALE = 6;

    /**
     * @brief 最大精度（Max Precision）对齐 `NUMERIC(19,6)`；
     *        Maximum precision aligned with `NUMERIC(19,6)`.
     */
    public static final int MAX_PRECISION = 19;

    /**
     * @brief 份额值（Quantity Value）；
     *        Quantity decimal value.
     */
    private final BigDecimal value;

    /**
     * @brief 构造份额值对象（Construct Quantity Value Object）；
     *        Construct quantity value object.
     *
     * @param value 标准化份额（Normalized quantity）。
     */
    private Quantity(final BigDecimal value) {
        this.value = normalize(value);
        if (this.value.signum() < 0) {
            throw new BusinessRuleViolation("Quantity must not be negative");
        }
    }

    /**
     * @brief 创建份额值对象（Factory Method）；
     *        Create quantity value object.
     *
     * @param rawValue 原始份额（Raw quantity）。
     * @return 份额值对象（Quantity value object）。
     */
    public static Quantity of(final BigDecimal rawValue) {
        return new Quantity(rawValue);
    }

    /**
     * @brief 创建零份额（Create Zero Quantity）；
     *        Create zero quantity.
     *
     * @return 零份额值对象（Zero quantity value object）。
     */
    public static Quantity zero() {
        return new Quantity(BigDecimal.ZERO);
    }

    /**
     * @brief 返回份额值（Return Quantity Value）；
     *        Return quantity decimal value.
     *
     * @return 份额值（Quantity value）。
     */
    public BigDecimal value() {
        return value;
    }

    /**
     * @brief 份额相加（Add Quantity）；
     *        Add quantity values.
     *
     * @param other 另一份额（Another quantity）。
     * @return 相加结果（Sum result）。
     */
    public Quantity add(final Quantity other) {
        final Quantity normalized = Objects.requireNonNull(other, "Other quantity must not be null");
        return new Quantity(value.add(normalized.value));
    }

    /**
     * @brief 份额相减（Subtract Quantity）；
     *        Subtract quantity values.
     *
     * @param other 另一份额（Another quantity）。
     * @return 相减结果（Difference result）。
     */
    public Quantity subtract(final Quantity other) {
        final Quantity normalized = Objects.requireNonNull(other, "Other quantity must not be null");
        final BigDecimal result = value.subtract(normalized.value);
        if (result.signum() < 0) {
            throw new BusinessRuleViolation("Quantity must not become negative after subtraction");
        }
        return new Quantity(result);
    }

    /**
     * @brief 判断是否为零（Check Zero Quantity）；
     *        Check whether quantity is zero.
     *
     * @return 等于 0 返回 true（true when equal to 0）。
     */
    public boolean isZero() {
        return value.signum() == 0;
    }

    /**
     * @brief 判断是否为正数（Check Positive Quantity）；
     *        Check whether quantity is positive.
     *
     * @return 大于 0 返回 true（true when > 0）。
     */
    public boolean isPositive() {
        return value.signum() > 0;
    }

    /**
     * @brief 比较份额（Compare Quantity）；
     *        Compare quantity values.
     *
     * @param other 另一份额（Another quantity）。
     * @return 比较结果（Comparison result）。
     */
    @Override
    public int compareTo(final Quantity other) {
        final Quantity normalized = Objects.requireNonNull(other, "Other quantity must not be null");
        return value.compareTo(normalized.value);
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
        if (!(other instanceof Quantity quantity)) {
            return false;
        }
        return value.equals(quantity.value);
    }

    /**
     * @brief 计算哈希值（Compute Hash Code）；
     *        Compute hash code.
     *
     * @return 哈希值（Hash code）。
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * @brief 返回字符串表示（String Representation）；
     *        Return string representation.
     *
     * @return 份额字符串（Quantity string）。
     */
    @Override
    public String toString() {
        return value.stripTrailingZeros().toPlainString();
    }

    /**
     * @brief 标准化并校验份额精度（Normalize and Validate Quantity Precision）；
     *        Normalize and validate quantity precision.
     *
     * @param rawValue 原始份额（Raw quantity）。
     * @return 标准化份额（Normalized quantity）。
     */
    private static BigDecimal normalize(final BigDecimal rawValue) {
        Objects.requireNonNull(rawValue, "Quantity must not be null");
        final BigDecimal normalized = rawValue.setScale(SCALE, RoundingMode.UNNECESSARY);
        if (normalized.precision() > MAX_PRECISION) {
            throw new IllegalArgumentException(
                    "Quantity precision must be <= " + MAX_PRECISION + " for NUMERIC(19,6)");
        }
        return normalized;
    }
}
