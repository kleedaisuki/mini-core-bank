package com.moesegfault.banking.domain.card;

import com.moesegfault.banking.domain.shared.EntityId;

/**
 * @brief 卡片标识值对象（Card Identifier Value Object），封装卡实体主键语义；
 *        Card identifier value object encapsulating card primary-key semantics.
 */
public final class CardId extends EntityId<CardId> {

    /**
     * @brief 构造卡片标识（Construct Card Identifier）；
     *        Construct card identifier.
     *
     * @param value 卡片 ID 值（Card ID value）。
     */
    private CardId(final String value) {
        super(value);
    }

    /**
     * @brief 从原始字符串创建卡片标识（Factory from Raw String）；
     *        Create card identifier from raw string.
     *
     * @param rawValue 原始 ID 输入（Raw ID input）。
     * @return 卡片标识（Card identifier）。
     */
    public static CardId of(final String rawValue) {
        return new CardId(rawValue);
    }
}
