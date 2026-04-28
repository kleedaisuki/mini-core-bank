package com.moesegfault.banking.domain.card;

import com.moesegfault.banking.domain.shared.EntityId;

/**
 * @brief 储蓄账户标识值对象（Savings Account Identifier Value Object），对应 `savings_account.account_id`；
 *        Savings account identifier value object mapped to `savings_account.account_id`.
 */
public final class SavingsAccountId extends EntityId<SavingsAccountId> {

    /**
     * @brief 构造储蓄账户标识（Construct Savings Account Identifier）；
     *        Construct savings account identifier.
     *
     * @param value 储蓄账户 ID（Savings account ID）。
     */
    private SavingsAccountId(final String value) {
        super(value);
    }

    /**
     * @brief 从原始字符串创建储蓄账户标识（Factory from Raw String）；
     *        Create savings account identifier from raw string.
     *
     * @param rawValue 原始 ID（Raw ID value）。
     * @return 储蓄账户标识（Savings account identifier）。
     */
    public static SavingsAccountId of(final String rawValue) {
        return new SavingsAccountId(rawValue);
    }
}
