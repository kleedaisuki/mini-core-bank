package com.moesegfault.banking.domain.business;

/**
 * @brief 业务类型状态枚举（Business Type Status Enum），严格对齐 `business_type.status` 约束；
 *        Business type status enum strictly aligned with `business_type.status` constraints.
 */
public enum BusinessTypeStatus {

    /**
     * @brief 激活状态（Active Status）；
     *        Active status.
     */
    ACTIVE,

    /**
     * @brief 停用状态（Inactive Status）；
     *        Inactive status.
     */
    INACTIVE;

    /**
     * @brief 判断是否可用（Check Availability）；
     *        Check whether type is available for new transactions.
     *
     * @return `ACTIVE` 返回 true（true for `ACTIVE`）。
     */
    public boolean isActive() {
        return this == ACTIVE;
    }
}
