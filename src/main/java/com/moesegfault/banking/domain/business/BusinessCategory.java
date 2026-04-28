package com.moesegfault.banking.domain.business;

/**
 * @brief 业务分类枚举（Business Category Enum），对齐 `business_type.business_category` 通用语言；
 *        Business category enum aligned with ubiquitous language of `business_type.business_category`.
 */
public enum BusinessCategory {

    /**
     * @brief 账户类业务（Account Category）；
     *        Account-related category.
     */
    ACCOUNT,

    /**
     * @brief 卡类业务（Card Category）；
     *        Card-related category.
     */
    CARD,

    /**
     * @brief 转账类业务（Transfer Category）；
     *        Transfer-related category.
     */
    TRANSFER,

    /**
     * @brief 投资类业务（Investment Category）；
     *        Investment-related category.
     */
    INVESTMENT,

    /**
     * @brief 信用卡类业务（Credit Card Category）；
     *        Credit-card-related category.
     */
    CREDIT_CARD
}
