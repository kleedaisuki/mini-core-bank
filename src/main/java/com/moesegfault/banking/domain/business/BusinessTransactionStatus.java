package com.moesegfault.banking.domain.business;

/**
 * @brief 业务交易状态枚举（Business Transaction Status Enum），严格对齐 `transaction_status` 约束；
 *        Business transaction status enum strictly aligned with `transaction_status` constraints.
 */
public enum BusinessTransactionStatus {

    /**
     * @brief 待处理状态（Pending Status）；
     *        Pending status.
     */
    PENDING,

    /**
     * @brief 成功状态（Success Status）；
     *        Success status.
     */
    SUCCESS,

    /**
     * @brief 失败状态（Failed Status）；
     *        Failed status.
     */
    FAILED,

    /**
     * @brief 已冲正状态（Reversed Status）；
     *        Reversed status.
     */
    REVERSED;

    /**
     * @brief 判断是否终态（Check Terminal State）；
     *        Check whether status is terminal.
     *
     * @return 终态返回 true（true when terminal）。
     */
    public boolean isTerminal() {
        return this == SUCCESS || this == FAILED || this == REVERSED;
    }

    /**
     * @brief 判断是否成功态（Check Successful State）；
     *        Check whether status is successful.
     *
     * @return `SUCCESS` 返回 true（true for `SUCCESS`）。
     */
    public boolean isSuccessful() {
        return this == SUCCESS;
    }

    /**
     * @brief 判断是否可终结（Check Completion Eligibility）；
     *        Check whether transaction can transition to a completed state.
     *
     * @return 仅 `PENDING` 返回 true（true only when `PENDING`）。
     */
    public boolean isCompletable() {
        return this == PENDING;
    }
}
