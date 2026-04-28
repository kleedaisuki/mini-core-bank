package com.moesegfault.banking.domain.investment;

/**
 * @brief 产品状态枚举（Product Status Enum），严格对齐 `investment_product.status` 检查约束；
 *        Product-status enum strictly aligned with `investment_product.status` check constraints.
 */
public enum ProductStatus {

    /**
     * @brief 可交易状态（Active Status）；
     *        Active tradable status.
     */
    ACTIVE,

    /**
     * @brief 暂停状态（Inactive Status）；
     *        Inactive suspended status.
     */
    INACTIVE,

    /**
     * @brief 已关闭状态（Closed Status）；
     *        Closed terminal status.
     */
    CLOSED;

    /**
     * @brief 是否可交易（Check Tradability）；
     *        Check whether status allows trading.
     *
     * @return 仅 `ACTIVE` 返回 true（true only for `ACTIVE`）。
     */
    public boolean isTradable() {
        return this == ACTIVE;
    }

    /**
     * @brief 是否终态（Check Terminal State）；
     *        Check whether status is terminal.
     *
     * @return `CLOSED` 返回 true（true for `CLOSED`）。
     */
    public boolean isTerminal() {
        return this == CLOSED;
    }
}
