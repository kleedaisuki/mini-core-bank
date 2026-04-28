package com.moesegfault.banking.presentation.gui.card;

import com.moesegfault.banking.application.card.result.CardResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import java.util.Objects;

/**
 * @brief 卡详情页面模型（Show Card Page Model），保存 `card show` 查询条件、加载状态与结果快照；
 *        Model for `card show` page storing query input, loading state, and result snapshot.
 */
public final class ShowCardModel extends AbstractGuiModel {

    /**
     * @brief 卡片 ID 输入（Card ID Input）；
     *        Canonical `card_id` query input.
     */
    private String cardId = "";

    /**
     * @brief 查询中标记（Loading Flag）；
     *        Whether one card query is in-flight.
     */
    private boolean loading;

    /**
     * @brief 全局错误消息（Global Error Message）；
     *        User-facing error message.
     */
    private String errorMessage = "";

    /**
     * @brief 查询结果快照（Card Result Snapshot）；
     *        Last successful card query result.
     */
    private CardResult cardResult;

    /**
     * @brief 设置卡片 ID（Set Card ID）；
     *        Set query card identifier.
     *
     * @param cardId 卡片 ID（Card ID）。
     */
    public void setCardId(final String cardId) {
        this.cardId = normalizeNullableText(cardId);
        fireChanged("cardId");
    }

    /**
     * @brief 获取卡片 ID（Get Card ID）；
     *        Get query card identifier.
     *
     * @return 卡片 ID（Card ID）。
     */
    public String cardId() {
        return cardId;
    }

    /**
     * @brief 设置加载状态（Set Loading Flag）；
     *        Set query loading flag.
     *
     * @param loading 是否加载中（Whether query is in-flight）。
     */
    public void setLoading(final boolean loading) {
        this.loading = loading;
        fireChanged("loading");
    }

    /**
     * @brief 是否加载中（Is Loading）；
     *        Whether query is in-flight.
     *
     * @return 是否加载中（Loading flag）。
     */
    public boolean loading() {
        return loading;
    }

    /**
     * @brief 设置全局错误消息（Set Global Error Message）；
     *        Set user-facing error message.
     *
     * @param errorMessage 错误消息（Error message）。
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = normalizeNullableText(errorMessage);
        fireChanged("errorMessage");
    }

    /**
     * @brief 获取全局错误消息（Get Global Error Message）；
     *        Get user-facing error message.
     *
     * @return 错误消息（Error message）。
     */
    public String errorMessage() {
        return errorMessage;
    }

    /**
     * @brief 设置卡查询结果（Set Card Query Result）；
     *        Set result snapshot of one card query.
     *
     * @param cardResult 查询结果（Card query result）。
     */
    public void setCardResult(final CardResult cardResult) {
        this.cardResult = Objects.requireNonNull(cardResult, "cardResult must not be null");
        fireChanged("cardResult");
    }

    /**
     * @brief 获取卡查询结果（Get Card Query Result）；
     *        Get result snapshot of one card query.
     *
     * @return 查询结果（Card query result），可能为 null。
     */
    public CardResult cardResultOrNull() {
        return cardResult;
    }

    /**
     * @brief 清理查询反馈（Clear Query Feedback）；
     *        Clear user-facing message and stale result.
     */
    public void clearFeedback() {
        this.errorMessage = "";
        this.cardResult = null;
        fireChanged("errorMessage", "cardResult");
    }

    /**
     * @brief 规范化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text by trimming and null-to-empty conversion.
     *
     * @param rawText 原始文本（Raw text）。
     * @return 规范化文本（Normalized text）。
     */
    private static String normalizeNullableText(final String rawText) {
        if (rawText == null) {
            return "";
        }
        return rawText.trim();
    }
}
