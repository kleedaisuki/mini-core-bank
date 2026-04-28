package com.moesegfault.banking.domain.customer;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @brief 地址值对象（Address Value Object），映射 `residential_address/mailing_address`；
 *        Address value object mapped to `residential_address/mailing_address`.
 */
public final class Address implements Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief 地址文本（Address Text）；
     *        Address text value.
     */
    private final String value;

    /**
     * @brief 构造地址对象（Construct Address）；
     *        Construct an address.
     *
     * @param value 地址文本（Address text）。
     */
    private Address(final String value) {
        this.value = value;
    }

    /**
     * @brief 从原始文本创建地址（Factory from Raw Text）；
     *        Create an address from raw text.
     *
     * @param rawValue 原始地址文本（Raw address text）。
     * @return 地址值对象（Address value object）。
     */
    public static Address of(final String rawValue) {
        if (rawValue == null) {
            throw new IllegalArgumentException("Address must not be null");
        }
        final String normalized = rawValue.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Address must not be blank");
        }
        return new Address(normalized);
    }

    /**
     * @brief 返回地址文本（Return Address Text）；
     *        Return address text.
     *
     * @return 地址文本（Address text）。
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
        if (!(other instanceof Address address)) {
            return false;
        }
        return value.equals(address.value);
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
     * @return 地址字符串（Address string）。
     */
    @Override
    public String toString() {
        return value;
    }
}
