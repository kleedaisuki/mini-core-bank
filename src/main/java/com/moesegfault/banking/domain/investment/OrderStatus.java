package com.moesegfault.banking.domain.investment;

/**
 * @brief 订单状态枚举（Order Status Enum），对应投资订单生命周期语义；
 *        Order-status enum representing investment-order lifecycle semantics.
 */
public enum OrderStatus {

    /**
     * @brief 已提交（Placed）；
     *        Order is placed.
     */
    PLACED,

    /**
     * @brief 已结算（Settled）；
     *        Order is settled.
     */
    SETTLED,

    /**
     * @brief 已失败（Failed）；
     *        Order is failed.
     */
    FAILED,

    /**
     * @brief 已取消（Cancelled）；
     *        Order is cancelled.
     */
    CANCELLED;

    /**
     * @brief 是否终态（Check Terminal State）；
     *        Check whether order status is terminal.
     *
     * @return 非 `PLACED` 返回 true（true when status is not `PLACED`）。
     */
    public boolean isTerminal() {
        return this != PLACED;
    }
}
