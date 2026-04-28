package com.moesegfault.banking.domain.account;

/**
 * @brief 账户类型枚举（Account Type Enum），严格对齐 `account.account_type` 检查约束；
 *        Account-type enum strictly aligned with `account.account_type` check constraints.
 */
public enum AccountType {

    /**
     * @brief 储蓄账户类型（Savings Account Type）；
     *        Savings account type.
     */
    SAVINGS,

    /**
     * @brief 外汇账户类型（FX Account Type）；
     *        FX account type.
     */
    FX,

    /**
     * @brief 投资账户类型（Investment Account Type）；
     *        Investment account type.
     */
    INVESTMENT,

    /**
     * @brief 信用卡账户类型（Credit Card Account Type）；
     *        Credit-card account type.
     */
    CREDIT_CARD
}
