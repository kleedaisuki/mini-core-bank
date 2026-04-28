package com.moesegfault.banking.domain.account;

import com.moesegfault.banking.domain.shared.EntityId;

/**
 * @brief 账户标识值对象（Account Identifier Value Object），封装 `account.account_id` 语义；
 *        Account identifier value object encapsulating `account.account_id` semantics.
 */
public final class AccountId extends EntityId<AccountId> {

    /**
     * @brief 构造账户标识（Construct Account Identifier）；
     *        Construct an account identifier.
     *
     * @param value 账户 ID（Account ID）。
     */
    private AccountId(final String value) {
        super(value);
    }

    /**
     * @brief 从原始字符串创建账户标识（Factory from Raw String）；
     *        Create account identifier from raw string.
     *
     * @param rawValue 原始 ID（Raw ID value）。
     * @return 账户标识（Account identifier）。
     */
    public static AccountId of(final String rawValue) {
        return new AccountId(rawValue);
    }
}
