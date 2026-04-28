package com.moesegfault.banking.domain.business;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @brief 业务类型码值对象（Business Type Code Value Object），对齐 `business_type.business_type_code` 约束；
 *        Business type code value object aligned with `business_type.business_type_code` constraints.
 */
public final class BusinessTypeCode implements Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief 业务类型码最大长度（Maximum Business Type Code Length），对齐 `VARCHAR(64)`；
     *        Maximum business type code length aligned to `VARCHAR(64)`.
     */
    private static final int MAX_LENGTH = 64;

    /**
     * @brief 业务类型码校验模式（Business Type Code Pattern）；
     *        Validation pattern for business type code.
     */
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z0-9_]+$");

    /**
     * @brief 标准化业务类型码（Normalized Business Type Code）；
     *        Normalized business type code value.
     */
    private final String value;

    /**
     * @brief 构造业务类型码（Construct Business Type Code）；
     *        Construct business type code.
     *
     * @param value 标准化后的业务类型码（Normalized business type code）。
     */
    private BusinessTypeCode(final String value) {
        this.value = value;
    }

    /**
     * @brief 从原始字符串创建业务类型码（Factory from Raw String）；
     *        Create business type code from raw string.
     *
     * @param rawValue 原始业务类型码（Raw business type code）。
     * @return 业务类型码值对象（Business type code value object）。
     */
    public static BusinessTypeCode of(final String rawValue) {
        return new BusinessTypeCode(normalize(rawValue));
    }

    /**
     * @brief 返回业务类型码字符串（Return Business Type Code String）；
     *        Return business type code string value.
     *
     * @return 业务类型码字符串（Business type code string）。
     */
    public String value() {
        return value;
    }

    /**
     * @brief 判断是否为同值业务码（Semantic Equality Check）；
     *        Check whether this code is semantically equal to another code.
     *
     * @param other 另一业务类型码（Another business type code）。
     * @return 同值返回 true（true when equal by value）。
     */
    public boolean sameValueAs(final BusinessTypeCode other) {
        return other != null && value.equals(other.value);
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
        if (!(other instanceof BusinessTypeCode that)) {
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
     * @return 业务类型码字符串（Business type code string）。
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * @brief 标准化并校验业务类型码（Normalize and Validate Business Type Code）；
     *        Normalize and validate business type code.
     *
     * @param rawValue 原始业务类型码（Raw business type code）。
     * @return 标准化后的业务类型码（Normalized business type code）。
     */
    private static String normalize(final String rawValue) {
        if (rawValue == null) {
            throw new IllegalArgumentException("Business type code must not be null");
        }
        final String normalized = rawValue.trim().toUpperCase();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Business type code must not be blank");
        }
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Business type code length must be <= " + MAX_LENGTH);
        }
        if (!CODE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Business type code format is invalid: " + rawValue);
        }
        return normalized;
    }
}
