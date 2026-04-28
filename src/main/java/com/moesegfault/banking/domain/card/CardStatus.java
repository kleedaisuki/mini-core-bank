package com.moesegfault.banking.domain.card;

/**
 * @brief 卡片状态枚举（Card Status Enum），严格对齐 `card_status` 检查约束；
 *        Card status enum strictly aligned with `card_status` check constraints.
 */
public enum CardStatus {

    /**
     * @brief 激活状态（Active Status）；
     *        Active card status.
     */
    ACTIVE,

    /**
     * @brief 已阻断状态（Blocked Status）；
     *        Blocked card status.
     */
    BLOCKED,

    /**
     * @brief 已关闭状态（Closed Status）；
     *        Closed card status.
     */
    CLOSED,

    /**
     * @brief 已过期状态（Expired Status）；
     *        Expired card status.
     */
    EXPIRED;

    /**
     * @brief 是否可用于交易（Check Transaction Availability）；
     *        Check whether card can be used for transactions.
     *
     * @return 仅 `ACTIVE` 返回 true；true only for `ACTIVE`.
     */
    public boolean isUsable() {
        return this == ACTIVE;
    }

    /**
     * @brief 是否终态（Check Terminal State）；
     *        Check whether status is terminal.
     *
     * @return `CLOSED` 或 `EXPIRED` 返回 true；true for `CLOSED` or `EXPIRED`.
     */
    public boolean isTerminal() {
        return this == CLOSED || this == EXPIRED;
    }
}
