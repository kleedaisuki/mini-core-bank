package com.moesegfault.banking.domain.card;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @brief 卡号值对象（Card Number Value Object），封装卡号归一化与脱敏显示；
 *        Card number value object encapsulating normalization and masked rendering.
 */
public final class CardNumber implements Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief 卡号最小长度（Minimum Card Number Length）；
     *        Minimum card number length.
     */
    private static final int MIN_LENGTH = 12;

    /**
     * @brief 卡号最大长度（Maximum Card Number Length）；
     *        Maximum card number length.
     */
    private static final int MAX_LENGTH = 32;

    /**
     * @brief 卡号格式校验模式（Card Number Pattern）；
     *        Validation pattern for card number.
     */
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]{12,32}$");

    /**
     * @brief 卡号标准值（Normalized Card Number）；
     *        Normalized card number value.
     */
    private final String value;

    /**
     * @brief 构造卡号（Construct Card Number）；
     *        Construct card number.
     *
     * @param value 标准卡号（Normalized card number）。
     */
    private CardNumber(final String value) {
        this.value = value;
    }

    /**
     * @brief 从原始字符串创建卡号（Factory from Raw String）；
     *        Create card number from raw string.
     *
     * @param rawValue 原始卡号（Raw card number）。
     * @return 卡号值对象（Card number value object）。
     */
    public static CardNumber of(final String rawValue) {
        return new CardNumber(normalize(rawValue));
    }

    /**
     * @brief 返回标准卡号（Return Normalized Card Number）；
     *        Return normalized card number.
     *
     * @return 标准卡号（Normalized card number）。
     */
    public String value() {
        return value;
    }

    /**
     * @brief 返回脱敏卡号（Return Masked Card Number）；
     *        Return masked card number.
     *
     * @return 脱敏后卡号字符串（Masked card number string）。
     */
    public String masked() {
        final int visibleDigits = 4;
        if (value.length() <= visibleDigits) {
            return value;
        }
        final String suffix = value.substring(value.length() - visibleDigits);
        final String maskedPrefix = "*".repeat(value.length() - visibleDigits);
        return maskedPrefix + suffix;
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
        if (!(other instanceof CardNumber that)) {
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
     * @brief 返回脱敏字符串表示（Masked String Representation）；
     *        Return masked string representation.
     *
     * @return 脱敏卡号（Masked card number）。
     */
    @Override
    public String toString() {
        return masked();
    }

    /**
     * @brief 标准化并校验卡号（Normalize and Validate Card Number）；
     *        Normalize and validate card number.
     *
     * @param rawValue 原始卡号（Raw card number）。
     * @return 标准卡号（Normalized card number）。
     */
    private static String normalize(final String rawValue) {
        if (rawValue == null) {
            throw new IllegalArgumentException("Card number must not be null");
        }
        final String normalized = rawValue.trim().replaceAll("[\\s-]", "");
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Card number must not be blank");
        }
        if (normalized.length() < MIN_LENGTH || normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Card number length must be between " + MIN_LENGTH + " and " + MAX_LENGTH);
        }
        if (!NUMBER_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Card number format is invalid: " + rawValue);
        }
        return normalized;
    }
}
