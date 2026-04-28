package com.moesegfault.banking.domain.account;

import com.moesegfault.banking.domain.shared.EntityId;

/**
 * @brief 账户域客户标识值对象（Account-Domain Customer Identifier Value Object）；
 *        Account-domain customer identifier value object.
 */
public final class CustomerId extends EntityId<CustomerId> {

    /**
     * @brief 构造客户标识（Construct Customer Identifier）；
     *        Construct customer identifier.
     *
     * @param value 客户 ID（Customer ID）。
     */
    private CustomerId(final String value) {
        super(value);
    }

    /**
     * @brief 从原始字符串创建客户标识（Factory from Raw String）；
     *        Create customer identifier from raw string.
     *
     * @param rawValue 原始 ID（Raw ID value）。
     * @return 客户标识（Customer identifier）。
     */
    public static CustomerId of(final String rawValue) {
        return new CustomerId(rawValue);
    }
}
