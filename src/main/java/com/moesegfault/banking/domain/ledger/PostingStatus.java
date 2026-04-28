package com.moesegfault.banking.domain.ledger;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;

/**
 * @brief 入账批次状态枚举（Posting Status Enum），对齐 `posting_batch.batch_status` 校验集合；
 *        Posting-status enum aligned with `posting_batch.batch_status` check constraints.
 */
public enum PostingStatus {

    /**
     * @brief 待入账（Pending）；
     *        Pending posting.
     */
    PENDING,

    /**
     * @brief 已入账（Posted）；
     *        Successfully posted.
     */
    POSTED,

    /**
     * @brief 入账失败（Failed）；
     *        Failed posting.
     */
    FAILED,

    /**
     * @brief 已冲正（Reversed）；
     *        Reversed posting.
     */
    REVERSED;

    /**
     * @brief 判断是否为终态（Check Final State）；
     *        Check whether status is final.
     *
     * @return 终态返回 true（true when final）。
     */
    public boolean isFinal() {
        return this == FAILED || this == REVERSED;
    }

    /**
     * @brief 判断是否允许向目标状态迁移（Transition Feasibility Check）；
     *        Check whether transition to target status is allowed.
     *
     * @param target 目标状态（Target status）。
     * @return 允许迁移返回 true（true when transition is allowed）。
     */
    public boolean canTransitionTo(final PostingStatus target) {
        if (target == null) {
            return false;
        }
        return switch (this) {
            case PENDING -> target == POSTED || target == FAILED;
            case POSTED -> target == REVERSED;
            case FAILED -> false;
            case REVERSED -> false;
        };
    }

    /**
     * @brief 要求状态迁移合法（Require Legal Transition）；
     *        Require status transition to be legal.
     *
     * @param target 目标状态（Target status）。
     */
    public void requireTransitionTo(final PostingStatus target) {
        if (!canTransitionTo(target)) {
            throw new BusinessRuleViolation("Illegal posting-status transition: " + this + " -> " + target);
        }
    }
}
