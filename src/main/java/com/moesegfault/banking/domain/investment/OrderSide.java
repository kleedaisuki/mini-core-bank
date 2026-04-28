package com.moesegfault.banking.domain.investment;

/**
 * @brief 订单方向枚举（Order Side Enum），严格对齐 `investment_order_detail.order_side` 检查约束；
 *        Order-side enum strictly aligned with `investment_order_detail.order_side` check constraints.
 */
public enum OrderSide {

    /**
     * @brief 买入（Buy）；
     *        Buy order side.
     */
    BUY,

    /**
     * @brief 卖出（Sell）；
     *        Sell order side.
     */
    SELL,

    /**
     * @brief 赎回（Redemption）；
     *        Redemption order side.
     */
    REDEMPTION,

    /**
     * @brief 分红（Dividend）；
     *        Dividend order side.
     */
    DIVIDEND;

    /**
     * @brief 是否增加持仓（Check Holding Increase）；
     *        Check whether side increases holding quantity.
     *
     * @return `BUY` 返回 true（true for `BUY`）。
     */
    public boolean increasesHolding() {
        return this == BUY;
    }

    /**
     * @brief 是否减少持仓（Check Holding Decrease）；
     *        Check whether side decreases holding quantity.
     *
     * @return `SELL` 或 `REDEMPTION` 返回 true（true for `SELL` or `REDEMPTION`）。
     */
    public boolean decreasesHolding() {
        return this == SELL || this == REDEMPTION;
    }
}
