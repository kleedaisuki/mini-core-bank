package com.moesegfault.banking.domain.card;

/**
 * @brief 卡域客户状态枚举（Card-Domain Customer Status Enum）；
 *        Card-domain customer status enum.
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
     * @brief 是否可发卡（Check Card-Issuing Eligibility）；
     *        Check whether card issuing is allowed.
     *
     * @return 仅 `ACTIVE` 返回 true；true only for `ACTIVE`.
     */
    public boolean canIssueCard() {
        return this == ACTIVE;
    }
}
