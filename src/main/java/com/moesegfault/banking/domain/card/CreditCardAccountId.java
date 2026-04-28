package com.moesegfault.banking.domain.card;

import com.moesegfault.banking.domain.shared.EntityId;

/**
 * @brief 信用卡账户标识值对象（Credit Card Account Identifier Value Object），对应 `credit_card_account.account_id`；
 *        Credit card account identifier value object mapped to `credit_card_account.account_id`.
 */
public final class CreditCardAccountId extends EntityId<CreditCardAccountId> {

    /**
     * @brief 构造信用卡账户标识（Construct Credit Card Account Identifier）；
     *        Construct credit card account identifier.
     *
     * @param value 信用卡账户 ID（Credit card account ID）。
     */
    private CreditCardAccountId(final String value) {
        super(value);
    }

    /**
     * @brief 从原始字符串创建信用卡账户标识（Factory from Raw String）；
     *        Create credit-card-account identifier from raw string.
     *
     * @param rawValue 原始 ID（Raw ID value）。
     * @return 信用卡账户标识（Credit card account identifier）。
     */
    public static CreditCardAccountId of(final String rawValue) {
        return new CreditCardAccountId(rawValue);
    }
}
