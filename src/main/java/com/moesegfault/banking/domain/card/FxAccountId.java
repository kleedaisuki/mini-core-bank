package com.moesegfault.banking.domain.card;

import com.moesegfault.banking.domain.shared.EntityId;

/**
 * @brief 外汇账户标识值对象（FX Account Identifier Value Object），对应 `fx_account.account_id`；
 *        FX account identifier value object mapped to `fx_account.account_id`.
 */
public final class FxAccountId extends EntityId<FxAccountId> {

    /**
     * @brief 构造外汇账户标识（Construct FX Account Identifier）；
     *        Construct FX account identifier.
     *
     * @param value 外汇账户 ID（FX account ID）。
     */
    private FxAccountId(final String value) {
        super(value);
    }

    /**
     * @brief 从原始字符串创建外汇账户标识（Factory from Raw String）；
     *        Create FX account identifier from raw string.
     *
     * @param rawValue 原始 ID（Raw ID value）。
     * @return 外汇账户标识（FX account identifier）。
     */
    public static FxAccountId of(final String rawValue) {
        return new FxAccountId(rawValue);
    }
}
