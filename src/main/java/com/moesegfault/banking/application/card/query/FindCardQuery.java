package com.moesegfault.banking.application.card.query;

import java.util.Objects;

/**
 * @brief 卡片查询请求（Find Card Query），按统一 `card_id` 查询卡实体；
 *        Card query request for loading card entities by unified `card_id`.
 */
public final class FindCardQuery {

    /**
     * @brief 卡片 ID（Card ID）；
     *        Card identifier.
     */
    private final String cardId;

    /**
     * @brief 构造查询请求（Construct Query Request）；
     *        Construct query request.
     *
     * @param cardId 卡片 ID（Card ID）。
     */
    public FindCardQuery(final String cardId) {
        this.cardId = normalize(cardId);
    }

    /**
     * @brief 返回卡片 ID（Return Card ID）；
     *        Return card identifier.
     *
     * @return 卡片 ID（Card ID）。
     */
    public String cardId() {
        return cardId;
    }

    /**
     * @brief 标准化 cardId（Normalize Card ID）；
     *        Normalize card-id input.
     *
     * @param rawCardId 原始 cardId（Raw card-id value）。
     * @return 标准化 cardId（Normalized card-id value）。
     */
    private static String normalize(final String rawCardId) {
        final String normalized = Objects.requireNonNull(rawCardId, "cardId must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("cardId must not be blank");
        }
        return normalized;
    }
}
