package com.moesegfault.banking.domain.ledger;

/**
 * @brief 分录方向枚举（Entry Direction Enum），对齐 `account_entry.entry_direction` 校验集合；
 *        Entry-direction enum aligned with `account_entry.entry_direction` check constraints.
 */
public enum EntryDirection {

    /**
     * @brief 借方方向（Debit Direction），通常表示资产减少或负债增加；
     *        Debit direction, typically asset decrease or liability increase.
     */
    DEBIT,

    /**
     * @brief 贷方方向（Credit Direction），通常表示资产增加或负债减少；
     *        Credit direction, typically asset increase or liability decrease.
     */
    CREDIT,

    /**
     * @brief 增加方向（Increase Direction），语义化表示余额增加；
     *        Increase direction, semantic indicator for balance increment.
     */
    INCREASE,

    /**
     * @brief 减少方向（Decrease Direction），语义化表示余额减少；
     *        Decrease direction, semantic indicator for balance decrement.
     */
    DECREASE;

    /**
     * @brief 判断是否为增加型方向（Check Increase-like Direction）；
     *        Check whether direction increases balance.
     *
     * @return 增加型返回 true（true when increase-like）。
     */
    public boolean isIncreaseLike() {
        return this == CREDIT || this == INCREASE;
    }

    /**
     * @brief 判断是否为减少型方向（Check Decrease-like Direction）；
     *        Check whether direction decreases balance.
     *
     * @return 减少型返回 true（true when decrease-like）。
     */
    public boolean isDecreaseLike() {
        return this == DEBIT || this == DECREASE;
    }
}
