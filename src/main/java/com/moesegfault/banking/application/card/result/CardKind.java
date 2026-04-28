package com.moesegfault.banking.application.card.result;

/**
 * @brief 卡片种类枚举（Card Kind Enum），用于 application 返回模型区分卡实体形态；
 *        Card kind enum for distinguishing card entity shapes in application result models.
 */
public enum CardKind {

    /**
     * @brief 主借记卡（Primary Debit Card）；
     *        Primary debit card kind.
     */
    DEBIT,

    /**
     * @brief 借记附属卡（Supplementary Debit Card）；
     *        Supplementary debit card kind.
     */
    SUPPLEMENTARY_DEBIT,

    /**
     * @brief 主信用卡（Primary Credit Card）；
     *        Primary credit card kind.
     */
    PRIMARY_CREDIT,

    /**
     * @brief 信用附属卡（Supplementary Credit Card）；
     *        Supplementary credit card kind.
     */
    SUPPLEMENTARY_CREDIT
}
