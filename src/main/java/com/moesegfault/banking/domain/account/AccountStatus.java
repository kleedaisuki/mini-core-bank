package com.moesegfault.banking.domain.account;

/**
 * @brief 账户状态枚举（Account Status Enum），严格对齐 `account.account_status` 检查约束；
 *        Account-status enum strictly aligned with `account.account_status` check constraints.
 */
public enum AccountStatus {

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
     * @brief 是否可进行资金与业务操作（Check Operational Availability）；
     *        Check whether fund/business operations are allowed.
     *
     * @return 仅 `ACTIVE` 返回 true；true only when status is `ACTIVE`.
     */
    public boolean isOperable() {
        return this == ACTIVE;
    }

    /**
     * @brief 是否为终态（Check Terminal State）；
     *        Check whether this status is terminal.
     *
     * @return `CLOSED` 返回 true；true when status is `CLOSED`.
     */
    public boolean isTerminal() {
        return this == CLOSED;
    }
}
