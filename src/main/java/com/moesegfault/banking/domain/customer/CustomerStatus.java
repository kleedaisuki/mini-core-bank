package com.moesegfault.banking.domain.customer;

/**
 * @brief 客户状态枚举（Customer Status Enum），对齐 `customer.customer_status` 约束；
 *        Customer status enum aligned with `customer.customer_status` constraints.
 */
public enum CustomerStatus {

    /**
     * @brief 已激活状态（Active Status）；
     *        Active status.
     */
    ACTIVE,

    /**
     * @brief 已冻结状态（Frozen Status）；
     *        Frozen status.
     */
    FROZEN,

    /**
     * @brief 已关闭状态（Closed Status）；
     *        Closed status.
     */
    CLOSED;

    /**
     * @brief 是否可进行新业务（Check If New Operations Are Allowed）；
     *        Check whether new operations are allowed.
     *
     * @return 仅 `ACTIVE` 返回 true；true only for `ACTIVE`.
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
