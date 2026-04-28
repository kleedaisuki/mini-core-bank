package com.moesegfault.banking.domain.investment;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @brief 净值值对象（Net Asset Value Value Object），对齐 `product_valuation.nav` 的 `NUMERIC(19,6)` 且必须大于零；
 *        Net-asset-value object aligned with `product_valuation.nav` as `NUMERIC(19,6)` and strictly positive.
 */
public final class NetAssetValue implements Comparable<NetAssetValue>, Serializable {

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
     * @brief 净值数值（NAV Decimal Value）；
     *        NAV decimal value.
     */
    private final BigDecimal value;

    /**
     * @brief 币种代码（Currency Code）；
     *        Currency code.
     */
    private final CurrencyCode currencyCode;

    /**
     * @brief 构造净值值对象（Construct Net Asset Value Value Object）；
     *        Construct net-asset-value object.
     *
     * @param value        净值（NAV value）。
     * @param currencyCode 币种（Currency code）。
     */
    private NetAssetValue(
            final BigDecimal value,
            final CurrencyCode currencyCode
    ) {
        this.value = normalize(value);
        this.currencyCode = Objects.requireNonNull(currencyCode, "Currency code must not be null");
        if (this.value.signum() <= 0) {
            throw new BusinessRuleViolation("Net asset value must be positive");
        }
    }

    /**
     * @brief 创建净值值对象（Factory Method）；
     *        Create net-asset-value object.
     *
     * @param value        净值（NAV value）。
     * @param currencyCode 币种（Currency code）。
     * @return 净值值对象（Net-asset-value object）。
     */
    public static NetAssetValue of(
            final BigDecimal value,
            final CurrencyCode currencyCode
    ) {
        return new NetAssetValue(value, currencyCode);
    }

    /**
     * @brief 返回净值（Return NAV Value）；
     *        Return NAV decimal value.
     *
     * @return 净值（NAV value）。
     */
    public BigDecimal value() {
        return value;
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
     * @brief 计算给定份额的市值（Compute Market Value by Quantity）；
     *        Compute market value for a quantity.
     *
     * @param quantity 份额（Quantity）。
     * @return 市值金额（Market-value amount）。
     */
    public BigDecimal marketValueOf(final Quantity quantity) {
        final Quantity normalized = Objects.requireNonNull(quantity, "Quantity must not be null");
        return normalized.value().multiply(value).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * @brief 比较净值（Compare Net Asset Value）；
     *        Compare net-asset-value objects.
     *
     * @param other 另一净值（Another NAV）。
     * @return 比较结果（Comparison result）。
     */
    @Override
    public int compareTo(final NetAssetValue other) {
        final NetAssetValue normalized = Objects.requireNonNull(other, "Other NAV must not be null");
        if (!currencyCode.equals(normalized.currencyCode)) {
            throw new BusinessRuleViolation("Net asset value currency mismatch: "
                    + currencyCode + " vs " + normalized.currencyCode);
        }
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
        if (!(other instanceof NetAssetValue that)) {
            return false;
        }
        return value.equals(that.value) && currencyCode.equals(that.currencyCode);
    }

    /**
     * @brief 计算哈希值（Compute Hash Code）；
     *        Compute hash code.
     *
     * @return 哈希值（Hash code）。
     */
    @Override
    public int hashCode() {
        return Objects.hash(value, currencyCode);
    }

    /**
     * @brief 返回字符串表示（String Representation）；
     *        Return string representation.
     *
     * @return 净值字符串（NAV string）。
     */
    @Override
    public String toString() {
        return currencyCode + " " + value.stripTrailingZeros().toPlainString();
    }

    /**
     * @brief 标准化并校验净值精度（Normalize and Validate NAV Precision）；
     *        Normalize and validate NAV precision.
     *
     * @param rawValue 原始净值（Raw NAV value）。
     * @return 标准化净值（Normalized NAV value）。
     */
    private static BigDecimal normalize(final BigDecimal rawValue) {
        Objects.requireNonNull(rawValue, "Net asset value must not be null");
        final BigDecimal normalized = rawValue.setScale(SCALE, RoundingMode.UNNECESSARY);
        if (normalized.precision() > MAX_PRECISION) {
            throw new IllegalArgumentException(
                    "Net asset value precision must be <= " + MAX_PRECISION + " for NUMERIC(19,6)");
        }
        return normalized;
    }
}
