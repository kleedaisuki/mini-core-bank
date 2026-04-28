package com.moesegfault.banking.domain.card;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 卡片有效期值对象（Card Expiry Value Object），映射 `issued_at/expired_at` 语义；
 *        Card expiry value object mapped to `issued_at/expired_at` semantics.
 */
public final class CardExpiry implements Serializable {

    /**
     * @brief 序列化版本号（Serialization Version UID）；
     *        Serialization version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @brief 发卡时间（Issued Timestamp）；
     *        Card issued timestamp.
     */
    private final Instant issuedAt;

    /**
     * @brief 过期时间（Expired Timestamp）；
     *        Card expired timestamp.
     */
    private final Instant expiredAt;

    /**
     * @brief 构造卡片有效期（Construct Card Expiry）；
     *        Construct card expiry.
     *
     * @param issuedAt  发卡时间（Issued time）。
     * @param expiredAt 过期时间（Expired time, nullable）。
     */
    private CardExpiry(final Instant issuedAt, final Instant expiredAt) {
        this.issuedAt = Objects.requireNonNull(issuedAt, "Issued-at must not be null");
        if (expiredAt != null && !expiredAt.isAfter(issuedAt)) {
            throw new BusinessRuleViolation("Card expired-at must be after issued-at");
        }
        this.expiredAt = expiredAt;
    }

    /**
     * @brief 创建“当前发卡、未过期”有效期（Create Newly Issued Non-expired Expiry）；
     *        Create card expiry with current issued time and no expired time.
     *
     * @return 卡片有效期（Card expiry）。
     */
    public static CardExpiry issuedNow() {
        return new CardExpiry(Instant.now(), null);
    }

    /**
     * @brief 从指定时间创建有效期（Factory from Explicit Times）；
     *        Create card expiry from explicit issued and expired timestamps.
     *
     * @param issuedAt  发卡时间（Issued time）。
     * @param expiredAt 过期时间（Expired time, nullable）。
     * @return 卡片有效期（Card expiry）。
     */
    public static CardExpiry of(final Instant issuedAt, final Instant expiredAt) {
        return new CardExpiry(issuedAt, expiredAt);
    }

    /**
     * @brief 生成已过期版本（Create Expired Version）；
     *        Create an expired version from this expiry instance.
     *
     * @param newExpiredAt 新过期时间（New expired time）。
     * @return 更新后的有效期（Updated expiry）。
     */
    public CardExpiry expireAt(final Instant newExpiredAt) {
        return new CardExpiry(issuedAt, Objects.requireNonNull(newExpiredAt, "Expired-at must not be null"));
    }

    /**
     * @brief 返回发卡时间（Return Issued Time）；
     *        Return issued timestamp.
     *
     * @return 发卡时间（Issued timestamp）。
     */
    public Instant issuedAt() {
        return issuedAt;
    }

    /**
     * @brief 返回可选过期时间（Return Optional Expired Time）；
     *        Return optional expired timestamp.
     *
     * @return 可选过期时间（Optional expired timestamp）。
     */
    public Optional<Instant> expiredAt() {
        return Optional.ofNullable(expiredAt);
    }

    /**
     * @brief 返回过期时间或 null（Return Expired Time or Null）；
     *        Return expired timestamp or null.
     *
     * @return 过期时间或 null（Expired timestamp or null）。
     */
    public Instant expiredAtOrNull() {
        return expiredAt;
    }

    /**
     * @brief 判断指定时点是否已过期（Check Expired at Instant）；
     *        Check whether card is expired at specified instant.
     *
     * @param at 判断时间（Time instant to evaluate）。
     * @return 已过期返回 true（true when expired）。
     */
    public boolean isExpiredAt(final Instant at) {
        Objects.requireNonNull(at, "Instant must not be null");
        return expiredAt != null && !at.isBefore(expiredAt);
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
        if (!(other instanceof CardExpiry that)) {
            return false;
        }
        return issuedAt.equals(that.issuedAt) && Objects.equals(expiredAt, that.expiredAt);
    }

    /**
     * @brief 计算哈希值（Compute Hash Code）；
     *        Compute hash code.
     *
     * @return 哈希值（Hash code）。
     */
    @Override
    public int hashCode() {
        return Objects.hash(issuedAt, expiredAt);
    }

    /**
     * @brief 返回字符串表示（String Representation）；
     *        Return string representation.
     *
     * @return 有效期字符串（Expiry string）。
     */
    @Override
    public String toString() {
        return "CardExpiry{issuedAt=" + issuedAt + ", expiredAt=" + expiredAt + '}';
    }
}
