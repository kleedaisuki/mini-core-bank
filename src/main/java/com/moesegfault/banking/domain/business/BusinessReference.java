package com.moesegfault.banking.domain.business;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @brief 业务参考号值对象（Business Reference Value Object），对齐 `business_transaction.reference_no` 约束；
 *        Business reference value object aligned with `business_transaction.reference_no` constraints.
 */
public final class BusinessReference implements Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief 参考号最大长度（Maximum Reference Length），对齐 `VARCHAR(64)`；
     *        Maximum reference length aligned to `VARCHAR(64)`.
     */
    private static final int MAX_LENGTH = 64;

    /**
     * @brief 参考号校验模式（Reference Pattern）；
     *        Validation pattern for business reference.
     */
    private static final Pattern REFERENCE_PATTERN = Pattern.compile("^[A-Z0-9_-]+$");

    /**
     * @brief 标准化参考号（Normalized Reference Value）；
     *        Normalized business reference value.
     */
    private final String value;

    /**
     * @brief 构造业务参考号（Construct Business Reference）；
     *        Construct business reference.
     *
     * @param value 标准化参考号（Normalized reference value）。
     */
    private BusinessReference(final String value) {
        this.value = value;
    }

    /**
     * @brief 从原始字符串创建业务参考号（Factory from Raw String）；
     *        Create business reference from raw string.
     *
     * @param rawValue 原始参考号（Raw reference value）。
     * @return 业务参考号值对象（Business reference value object）。
     */
    public static BusinessReference of(final String rawValue) {
        return new BusinessReference(normalize(rawValue));
    }

    /**
     * @brief 返回参考号字符串（Return Reference String）；
     *        Return business reference string.
     *
     * @return 参考号字符串（Reference string）。
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
        if (!(other instanceof BusinessReference that)) {
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
     * @return 参考号字符串（Reference string）。
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * @brief 标准化并校验参考号（Normalize and Validate Reference）；
     *        Normalize and validate reference value.
     *
     * @param rawValue 原始参考号（Raw reference value）。
     * @return 标准化参考号（Normalized reference value）。
     */
    private static String normalize(final String rawValue) {
        if (rawValue == null) {
            throw new IllegalArgumentException("Business reference must not be null");
        }
        final String normalized = rawValue.trim().toUpperCase();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Business reference must not be blank");
        }
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Business reference length must be <= " + MAX_LENGTH);
        }
        if (!REFERENCE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Business reference format is invalid: " + rawValue);
        }
        return normalized;
    }
}
