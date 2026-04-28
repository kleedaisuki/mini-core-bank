package com.moesegfault.banking.presentation.web.card;

/**
 * @brief Card Web schema 常量（Card Web Schema Constants），统一管理 card REST API 路径与字段名称；
 *        Card web schema constants centralizing card REST API paths and field names.
 */
final class CardWebSchema {

    /**
     * @brief 卡资源根路径（Cards Root Path）；
     *        Root path for card resources.
     */
    static final String PATH_CARDS = "/cards";

    /**
     * @brief 主借记卡发卡路径（Issue Debit Card Path）；
     *        Path for issuing primary debit card.
     */
    static final String PATH_ISSUE_DEBIT = "/cards/debit";

    /**
     * @brief 借记附属卡发卡路径（Issue Supplementary Debit Card Path）；
     *        Path for issuing supplementary debit card.
     */
    static final String PATH_ISSUE_SUPPLEMENTARY_DEBIT = "/cards/supplementary-debit";

    /**
     * @brief 主信用卡发卡路径（Issue Credit Card Path）；
     *        Path for issuing primary credit card.
     */
    static final String PATH_ISSUE_CREDIT = "/cards/credit";

    /**
     * @brief 信用附属卡发卡路径（Issue Supplementary Credit Card Path）；
     *        Path for issuing supplementary credit card.
     */
    static final String PATH_ISSUE_SUPPLEMENTARY_CREDIT = "/cards/supplementary-credit";

    /**
     * @brief 卡详情路径模式（Card Detail Path Pattern）；
     *        Path pattern for card detail retrieval.
     */
    static final String PATH_CARD_DETAIL = "/cards/{cardId}";

    /**
     * @brief 路径参数名：卡 ID（Path Parameter Name: Card ID）；
     *        Path parameter name for card identifier.
     */
    static final String PATH_PARAM_CARD_ID = "cardId";

    /**
     * @brief `card_id` 字段名（`card_id` Field Name）；
     *        Canonical schema field name `card_id`.
     */
    static final String FIELD_CARD_ID = "card_id";

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for constants holder.
     */
    private CardWebSchema() {
    }
}

