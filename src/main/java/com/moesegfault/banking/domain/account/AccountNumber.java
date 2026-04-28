package com.moesegfault.banking.domain.account;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @brief 账户号值对象（Account Number Value Object），对齐 `account.account_no` 长度约束；
 *        Account-number value object aligned with `account.account_no` length constraints.
 */
public final class AccountNumber implements Comparable<AccountNumber>, Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief 账户号最大长度（Maximum Account Number Length）；
     *        Maximum account-number length.
     */
    private static final int MAX_LENGTH = 32;

    /**
     * @brief 账户号值（Account Number Value）；
     *        Account-number value.
     */
    private final String value;

    /**
     * @brief 构造账户号（Construct Account Number）；
     *        Construct account number.
     *
     * @param value 账户号（Account number）。
     */
    private AccountNumber(final String value) {
        this.value = normalize(value);
    }

    /**
     * @brief 从原始字符串创建账户号（Factory from Raw String）；
     *        Create account number from raw string.
     *
     * @param rawValue 原始账户号（Raw account number）。
     * @return 账户号值对象（Account-number value object）。
     */
    public static AccountNumber of(final String rawValue) {
        return new AccountNumber(rawValue);
    }

    /**
     * @brief 返回账户号字符串（Return Account Number String）；
     *        Return account-number string.
     *
     * @return 账户号（Account number）。
     */
    public String value() {
        return value;
    }

    /**
     * @brief 比较账户号（Compare Account Number）；
     *        Compare account numbers.
     *
     * @param other 另一个账户号（Another account number）。
     * @return 比较结果（Comparison result）。
     */
    @Override
    public int compareTo(final AccountNumber other) {
        Objects.requireNonNull(other, "Other account number must not be null");
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
        if (!(other instanceof AccountNumber that)) {
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
     * @return 账户号字符串（Account-number string）。
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * @brief 标准化并校验账户号（Normalize and Validate Account Number）；
     *        Normalize and validate account number.
     *
     * @param raw 原始输入（Raw input）。
     * @return 标准化值（Normalized value）。
     */
    private static String normalize(final String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Account number must not be null");
        }
        final String normalized = raw.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Account number must not be blank");
        }
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Account number must be at most 32 characters");
        }
        return normalized;
    }
}
