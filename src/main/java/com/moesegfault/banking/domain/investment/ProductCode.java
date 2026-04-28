package com.moesegfault.banking.domain.investment;

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @brief 产品代码值对象（Product Code Value Object），对齐 `investment_product.product_code` 唯一键语义；
 *        Product-code value object aligned with `investment_product.product_code` unique-key semantics.
 */
public final class ProductCode implements Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief 产品代码最大长度（Maximum Product Code Length）；
     *        Maximum length for product code.
     */
    private static final int MAX_LENGTH = 32;

    /**
     * @brief 产品代码格式（Product Code Pattern）；
     *        Validation pattern for product code.
     */
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z0-9][A-Z0-9_-]{0,31}$");

    /**
     * @brief 产品代码值（Product Code Value）；
     *        Product code value.
     */
    private final String value;

    /**
     * @brief 构造产品代码（Construct Product Code）；
     *        Construct product code.
     *
     * @param value 标准化代码（Normalized code）。
     */
    private ProductCode(final String value) {
        this.value = value;
    }

    /**
     * @brief 从原始字符串创建产品代码（Factory from Raw String）；
     *        Create product code from raw string.
     *
     * @param rawValue 原始代码（Raw code）。
     * @return 产品代码值对象（Product-code value object）。
     */
    public static ProductCode of(final String rawValue) {
        return new ProductCode(normalize(rawValue));
    }

    /**
     * @brief 返回代码字符串（Return Code String）；
     *        Return code string.
     *
     * @return 产品代码（Product code）。
     */
    public String value() {
        return value;
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
        if (!(other instanceof ProductCode that)) {
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
     * @return 产品代码字符串（Product code string）。
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * @brief 标准化并校验产品代码（Normalize and Validate Product Code）；
     *        Normalize and validate product code.
     *
     * @param rawValue 原始代码（Raw code）。
     * @return 标准化代码（Normalized code）。
     */
    private static String normalize(final String rawValue) {
        if (rawValue == null) {
            throw new IllegalArgumentException("Product code must not be null");
        }
        final String normalized = rawValue.trim().toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Product code must not be blank");
        }
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Product code length must be <= " + MAX_LENGTH);
        }
        if (!CODE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Product code format is invalid: " + rawValue);
        }
        return normalized;
    }
}
