package com.moesegfault.banking.presentation.gui.card;

/**
 * @brief 卡模块 schema 字段常量（Card Module Schema Field Constants），统一复用 canonical schema 名称；
 *        Canonical schema field-name constants reused by card GUI models, views, and controllers.
 */
final class CardGuiSchema {

    /**
     * @brief 持卡客户 ID 字段（Holder Customer ID Field）；
     *        Canonical `holder_customer_id` field name.
     */
    static final String HOLDER_CUSTOMER_ID = "holder_customer_id";

    /**
     * @brief 储蓄账户 ID 字段（Savings Account ID Field）；
     *        Canonical `savings_account_id` field name.
     */
    static final String SAVINGS_ACCOUNT_ID = "savings_account_id";

    /**
     * @brief 外汇账户 ID 字段（FX Account ID Field）；
     *        Canonical `fx_account_id` field name.
     */
    static final String FX_ACCOUNT_ID = "fx_account_id";

    /**
     * @brief 主借记卡 ID 字段（Primary Debit Card ID Field）；
     *        Canonical `primary_debit_card_id` field name.
     */
    static final String PRIMARY_DEBIT_CARD_ID = "primary_debit_card_id";

    /**
     * @brief 主信用卡 ID 字段（Primary Credit Card ID Field）；
     *        Canonical `primary_credit_card_id` field name.
     */
    static final String PRIMARY_CREDIT_CARD_ID = "primary_credit_card_id";

    /**
     * @brief 信用卡账户 ID 字段（Credit Card Account ID Field）；
     *        Canonical `credit_card_account_id` field name.
     */
    static final String CREDIT_CARD_ACCOUNT_ID = "credit_card_account_id";

    /**
     * @brief 卡号字段（Card Number Field）；
     *        Canonical `card_no` field name.
     */
    static final String CARD_NO = "card_no";

    /**
     * @brief 卡片 ID 字段（Card ID Field）；
     *        Canonical `card_id` field name.
     */
    static final String CARD_ID = "card_id";

    /**
     * @brief 字段名属性键（Field Name Attribute Key）；
     *        Attribute key for field name in view events.
     */
    static final String FIELD_NAME = "field_name";

    /**
     * @brief 字段值属性键（Field Value Attribute Key）；
     *        Attribute key for field value in view events.
     */
    static final String FIELD_VALUE = "field_value";

    /**
     * @brief 提交事件类型（Submit Event Type）；
     *        View event type for form submission.
     */
    static final String EVENT_SUBMIT = "submit";

    /**
     * @brief 字段变更事件类型（Field-changed Event Type）；
     *        View event type for one field change.
     */
    static final String EVENT_FIELD_CHANGED = "field_changed";

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for constants holder.
     */
    private CardGuiSchema() {
    }
}
