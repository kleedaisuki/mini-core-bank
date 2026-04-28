package com.moesegfault.banking.domain.customer;

import com.moesegfault.banking.domain.shared.EntityId;

/**
 * @brief 客户标识值对象（Customer Identifier Value Object），封装 `customer.customer_id` 语义；
 *        Customer identifier value object encapsulating `customer.customer_id` semantics.
 */
public final class CustomerId extends EntityId<CustomerId> {

    /**
     * @brief 构造客户标识（Construct Customer Identifier）；
     *        Construct a customer identifier.
     *
     * @param value 客户 ID 字符串（Customer ID string value）。
     */
    private CustomerId(final String value) {
        super(value);
    }

    /**
     * @brief 由原始字符串创建客户标识（Factory from Raw String）；
     *        Create a customer identifier from raw string.
     *
     * @param rawValue 原始 ID 输入（Raw ID input）。
     * @return 客户标识值对象（Customer identifier value object）。
     */
    public static CustomerId of(final String rawValue) {
        return new CustomerId(rawValue);
    }
}
