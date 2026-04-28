package com.moesegfault.banking.domain.credit;

/**
 * @brief 账单状态枚举（Statement Status Enum），严格对齐 `statement_status` 检查约束；
 *        Statement-status enum strictly aligned with `statement_status` check constraints.
 */
public enum StatementStatus {

    /**
     * @brief 开放状态（Open Status）；
     *        Open statement status.
     */
    OPEN,

    /**
     * @brief 已还清状态（Paid Status）；
     *        Paid statement status.
     */
    PAID,

    /**
     * @brief 已逾期状态（Overdue Status）；
     *        Overdue statement status.
     */
    OVERDUE,

    /**
     * @brief 已关闭状态（Closed Status）；
     *        Closed statement status.
     */
    CLOSED;

    /**
     * @brief 判断账单是否可继续接受还款（Check Repayment Acceptance）；
     *        Check whether statement can still accept repayments.
     *
     * @return `OPEN` 或 `OVERDUE` 返回 true；true for `OPEN` or `OVERDUE`.
     */
    public boolean canAcceptRepayment() {
        return this == OPEN || this == OVERDUE;
    }

    /**
     * @brief 判断账单是否已完成结算（Check Settled State）；
     *        Check whether statement is settled.
     *
     * @return `PAID` 或 `CLOSED` 返回 true；true for `PAID` or `CLOSED`.
     */
    public boolean isSettled() {
        return this == PAID || this == CLOSED;
    }
}
