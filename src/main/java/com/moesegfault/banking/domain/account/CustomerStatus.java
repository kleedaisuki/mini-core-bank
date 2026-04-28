package com.moesegfault.banking.domain.account;

/**
 * @brief 账户域客户状态枚举（Account-Domain Customer Status Enum）；
 *        Account-domain customer status enum.
 */
public enum CustomerStatus {

    /**
     * @brief 激活状态（Active Status）；
     *        Active status.
     */
    ACTIVE,

    /**
     * @brief 冻结状态（Frozen Status）；
     *        Frozen status.
     */
    FROZEN,

    /**
     * @brief 关闭状态（Closed Status）；
     *        Closed status.
     */
    CLOSED;

    /**
     * @brief 是否可开户（Check Account-Opening Eligibility）；
     *        Check whether account opening is allowed.
     *
     * @return 仅 `ACTIVE` 返回 true；true only for `ACTIVE`.
     */
    public boolean canOpenAccount() {
        return this == ACTIVE;
    }
}
