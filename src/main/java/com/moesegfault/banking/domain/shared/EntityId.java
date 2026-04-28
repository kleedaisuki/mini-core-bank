package com.moesegfault.banking.domain.shared;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @brief 通用实体标识基类（Entity Identifier Base Class），统一 ID 值对象行为；
 * Generic entity identifier base class that unifies value-object ID behavior.
 *
 * @param <T> 具体 ID 子类型（Concrete ID subtype）。
 */
public abstract class EntityId<T extends EntityId<T>> implements Comparable<T>, Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     * Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief ID 原始值（Raw Identifier Value）；
     * Raw identifier value.
     */
    private final String value;

    /**
     * @brief 创建实体标识（Create Entity Identifier）；
     * Create an entity identifier.
     *
     * @param value ID 字符串值（ID string value）。
     */
    protected EntityId(final String value) {
        this.value = normalize(value);
    }

    /**
     * @brief 返回 ID 字符串值（Return ID String Value）；
     * Return the ID string value.
     *
     * @return ID 字符串值（ID string value）。
     */
    public final String value() {
        return this.value;
    }

    /**
     * @brief 判断是否与另一个同类型 ID 语义相同（Semantic Equality Check）；
     * Check whether this ID is semantically equal to another same-type ID.
     *
     * @param other 另一个 ID（Another ID）。
     * @return 若同值返回 true，否则 false；true when equal by value, otherwise false.
     */
    public final boolean sameValueAs(final T other) {
        return other != null && this.value.equals(other.value());
    }

    /**
     * @brief 按字典序比较 ID（Lexicographic Comparison）；
     * Compare IDs lexicographically.
     *
     * @param other 另一个同类型 ID（Another same-type ID）。
     * @return 比较结果（Comparison result）。
     */
    @Override
    public final int compareTo(final T other) {
        Objects.requireNonNull(other, "Other identifier must not be null");
        return this.value.compareTo(other.value());
    }

    /**
     * @brief 值对象相等判定（Value Object Equality）；
     * Value-object equality check.
     *
     * @param other 对比对象（Object to compare）。
     * @return 同类且同值返回 true；true when same class and same value.
     */
    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final EntityId<?> that = (EntityId<?>) other;
        return value.equals(that.value);
    }

    /**
     * @brief 计算哈希值（Compute Hash Code）；
     * Compute hash code.
     *
     * @return 哈希值（Hash code）。
     */
    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), value);
    }

    /**
     * @brief 返回字符串表示（String Representation）；
     * Return string representation.
     *
     * @return ID 字符串（ID string）。
     */
    @Override
    public final String toString() {
        return value;
    }

    /**
     * @brief 标准化并校验 ID 输入（Normalize and Validate ID Input）；
     * Normalize and validate ID input.
     *
     * @param raw 原始输入（Raw input）。
     * @return 标准化后的值（Normalized value）。
     */
    private static String normalize(final String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Identifier must not be null");
        }
        final String normalized = raw.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Identifier must not be blank");
        }
        return normalized;
    }
}
