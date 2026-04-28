package com.moesegfault.banking.domain.business;

import com.moesegfault.banking.domain.shared.EntityId;

/**
 * @brief 业务交易标识值对象（Business Transaction Identifier Value Object），封装统一业务流水主键语义；
 *        Business transaction identifier value object encapsulating unified business-flow primary-key semantics.
 */
public final class BusinessTransactionId extends EntityId<BusinessTransactionId> {

    /**
     * @brief 构造业务交易标识（Construct Business Transaction Identifier）；
     *        Construct business transaction identifier.
     *
     * @param value 业务交易 ID 值（Business transaction ID value）。
     */
    private BusinessTransactionId(final String value) {
        super(value);
    }

    /**
     * @brief 从原始字符串创建业务交易标识（Factory from Raw String）；
     *        Create business transaction identifier from raw string.
     *
     * @param rawValue 原始 ID 输入（Raw ID input）。
     * @return 业务交易标识（Business transaction identifier）。
     */
    public static BusinessTransactionId of(final String rawValue) {
        return new BusinessTransactionId(rawValue);
    }
}
