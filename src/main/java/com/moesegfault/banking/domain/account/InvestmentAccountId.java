package com.moesegfault.banking.domain.account;

import com.moesegfault.banking.domain.shared.EntityId;

/**
 * @brief 投资账户标识值对象（Investment Account Identifier Value Object），对应 `investment_account.account_id`；
 *        Investment-account identifier value object mapped to `investment_account.account_id`.
 */
public final class InvestmentAccountId extends EntityId<InvestmentAccountId> {

    /**
     * @brief 构造投资账户标识（Construct Investment Account Identifier）；
     *        Construct investment-account identifier.
     *
     * @param value 投资账户 ID（Investment-account ID）。
     */
    private InvestmentAccountId(final String value) {
        super(value);
    }

    /**
     * @brief 从原始字符串创建投资账户标识（Factory from Raw String）；
     *        Create investment-account identifier from raw string.
     *
     * @param rawValue 原始 ID（Raw ID value）。
     * @return 投资账户标识（Investment-account identifier）。
     */
    public static InvestmentAccountId of(final String rawValue) {
        return new InvestmentAccountId(rawValue);
    }
}
