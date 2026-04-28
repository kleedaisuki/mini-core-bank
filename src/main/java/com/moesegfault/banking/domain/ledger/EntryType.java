package com.moesegfault.banking.domain.ledger;

/**
 * @brief 分录类型枚举（Entry Type Enum），对齐 `account_entry.entry_type` 校验集合；
 *        Entry-type enum aligned with `account_entry.entry_type` check constraints.
 */
public enum EntryType {

    /**
     * @brief 本金类分录（Principal Entry）；
     *        Principal entry.
     */
    PRINCIPAL,

    /**
     * @brief 手续费类分录（Fee Entry）；
     *        Fee entry.
     */
    FEE,

    /**
     * @brief 利息类分录（Interest Entry）；
     *        Interest entry.
     */
    INTEREST,

    /**
     * @brief 分红类分录（Dividend Entry）；
     *        Dividend entry.
     */
    DIVIDEND,

    /**
     * @brief 还款类分录（Repayment Entry）；
     *        Repayment entry.
     */
    REPAYMENT,

    /**
     * @brief 调整类分录（Adjustment Entry）；
     *        Adjustment entry.
     */
    ADJUSTMENT
}
