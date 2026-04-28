package com.moesegfault.banking.domain.shared;

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @brief 币种代码值对象（Currency Code Value Object），对齐数据库 `currency.currency_code` 语义；
 *        Currency code value object aligned with database
 *        `currency.currency_code` semantics.
 */
public final class CurrencyCode implements Comparable<CurrencyCode>, Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief ISO 风格三位大写字母校验模式（3-letter Uppercase Pattern）；
     *        Validation pattern for ISO-like 3-letter uppercase codes.
     */
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z]{3}$");

    /**
     * @brief 当前迁移脚本预置币种集合（Seeded Currency Set in Migrations）；
     *        Seeded currency set defined by migration scripts.
     */
    private static final Set<String> SEEDED_CODES = Set.of(
            "USD", "CNH", "EUR", "JPY", "SGD", "HKD", "KRW", "AUD", "GBP", "CAD");

    /**
     * @brief 币种代码值（Currency Code Value）；
     *        Currency code value.
     */
    private final String value;

    /**
     * @brief 构造币种代码（Construct Currency Code）；
     *        Construct a currency code.
     *
     * @param value 币种代码（Currency code）。
     */
    private CurrencyCode(final String value) {
        this.value = value;
    }

    /**
     * @brief 从原始字符串创建币种代码（Factory from Raw String）；
     *        Create a currency code from raw string.
     *
     * @param rawCode 原始币种代码（Raw currency code）。
     * @return 币种代码值对象（Currency code value object）。
     */
    public static CurrencyCode of(final String rawCode) {
        if (rawCode == null) {
            throw new IllegalArgumentException("Currency code must not be null");
        }
        final String normalized = rawCode.trim().toUpperCase(Locale.ROOT);
        if (!CODE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Currency code must be 3 uppercase letters, got: " + rawCode);
        }
        return new CurrencyCode(normalized);
    }

    /**
     * @brief 返回币种代码字符串（Return Currency Code String）；
     *        Return currency code string.
     *
     * @return 币种代码（Currency code）。
     */
    public String value() {
        return value;
    }

    /**
     * @brief 判断是否为当前系统预置币种（Check if Seeded Currency）；
     *        Check whether this code is part of the currently seeded currency set.
     *
     * @return 若为预置币种返回 true；true when this code is seeded.
     */
    public boolean isSeededCurrency() {
        return SEEDED_CODES.contains(value);
    }

    /**
     * @brief 获取预置币种集合（Get Seeded Currency Set）；
     *        Get seeded currency set.
     *
     * @return 预置币种不可变集合（Immutable seeded currency set）。
     */
    public static Set<String> seededCodes() {
        return SEEDED_CODES;
    }

    /**
     * @brief 比较币种代码（Compare Currency Codes）；
     *        Compare currency codes.
     *
     * @param other 另一个币种代码（Another currency code）。
     * @return 比较结果（Comparison result）。
     */
    @Override
    public int compareTo(final CurrencyCode other) {
        Objects.requireNonNull(other, "Other currency code must not be null");
        return this.value.compareTo(other.value);
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
        if (!(other instanceof CurrencyCode that)) {
            return false;
        }
        return value.equals(that.value);
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
     * @return 币种代码字符串（Currency code string）。
     */
    @Override
    public String toString() {
        return value;
    }
}
