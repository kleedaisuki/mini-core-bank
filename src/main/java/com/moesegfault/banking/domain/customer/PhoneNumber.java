package com.moesegfault.banking.domain.customer;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @brief 手机号值对象（Phone Number Value Object），映射 `customer.mobile_phone` 字段；
 *        Phone number value object mapped to `customer.mobile_phone`.
 *
 * @note 采用 E.164（E.164）风格的宽松校验并归一化到 `+digits` 或 `digits`；
 *       Uses relaxed E.164-style validation and normalizes to `+digits` or `digits`.
 */
public final class PhoneNumber implements Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief 数据库字段最大长度（Database Column Max Length）；
     *        Maximum length aligned to `VARCHAR(32)`.
     */
    private static final int MAX_LENGTH = 32;

    /**
     * @brief 标准号码格式（Normalized Number Pattern）；
     *        Pattern for normalized phone numbers.
     */
    private static final Pattern NORMALIZED_PATTERN = Pattern.compile("^\\+?[0-9]{6,15}$");

    /**
     * @brief 归一化后的手机号（Normalized Phone Number）；
     *        Normalized phone number.
     */
    private final String value;

    /**
     * @brief 构造手机号（Construct Phone Number）；
     *        Construct a phone number.
     *
     * @param value 归一化号码（Normalized number）。
     */
    private PhoneNumber(final String value) {
        this.value = value;
    }

    /**
     * @brief 从原始输入创建手机号（Factory from Raw Input）；
     *        Create phone number from raw input.
     *
     * @param rawValue 原始手机号（Raw phone number）。
     * @return 手机号值对象（Phone number value object）。
     */
    public static PhoneNumber of(final String rawValue) {
        return new PhoneNumber(normalize(rawValue));
    }

    /**
     * @brief 返回手机号字符串（Return Phone Number String）；
     *        Return phone number string.
     *
     * @return 归一化手机号（Normalized phone number）。
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
        if (!(other instanceof PhoneNumber that)) {
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
     * @return 手机号字符串（Phone number string）。
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * @brief 标准化并校验手机号（Normalize and Validate Phone Number）；
     *        Normalize and validate phone number.
     *
     * @param rawValue 原始输入（Raw input）。
     * @return 归一化手机号（Normalized phone number）。
     */
    private static String normalize(final String rawValue) {
        if (rawValue == null) {
            throw new IllegalArgumentException("Phone number must not be null");
        }
        final String trimmed = rawValue.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Phone number must not be blank");
        }
        final String normalized = trimmed.replaceAll("[\\s()\\-]", "");
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Phone number length must be <= " + MAX_LENGTH);
        }
        if (!NORMALIZED_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Phone number format is invalid: " + rawValue);
        }
        return normalized;
    }
}
