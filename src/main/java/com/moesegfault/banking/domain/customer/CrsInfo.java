package com.moesegfault.banking.domain.customer;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @brief CRS 信息值对象（CRS Information Value Object），对应 `customer.crs_info`；
 *        CRS information value object mapped to `customer.crs_info`.
 *
 * @note CRS 是 Common Reporting Standard（通用报告准则）；
 *       CRS means Common Reporting Standard.
 */
public final class CrsInfo implements Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief CRS 文本（CRS Text）；
     *        CRS information text.
     */
    private final String value;

    /**
     * @brief 构造 CRS 信息对象（Construct CRS Information）；
     *        Construct CRS information object.
     *
     * @param value CRS 文本（CRS text value）。
     */
    private CrsInfo(final String value) {
        this.value = value;
    }

    /**
     * @brief 从原始文本创建 CRS 信息（Factory from Raw Text）；
     *        Create CRS information from raw text.
     *
     * @param rawValue 原始 CRS 文本（Raw CRS text）。
     * @return CRS 信息值对象（CRS information value object）。
     */
    public static CrsInfo of(final String rawValue) {
        if (rawValue == null) {
            throw new IllegalArgumentException("CRS information must not be null");
        }
        final String normalized = rawValue.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("CRS information must not be blank");
        }
        return new CrsInfo(normalized);
    }

    /**
     * @brief 返回 CRS 文本（Return CRS Text）；
     *        Return CRS text.
     *
     * @return CRS 文本（CRS text）。
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
        if (!(other instanceof CrsInfo crsInfo)) {
            return false;
        }
        return value.equals(crsInfo.value);
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
     * @return CRS 文本字符串（CRS text string）。
     */
    @Override
    public String toString() {
        return value;
    }
}
