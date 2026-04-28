package com.moesegfault.banking.domain.investment;

import com.moesegfault.banking.domain.shared.EntityId;

/**
 * @brief 投资订单标识值对象（Investment Order Identifier Value Object），对应 `investment_order_detail.transaction_id`；
 *        Investment-order identifier value object mapped to `investment_order_detail.transaction_id`.
 */
public final class InvestmentOrderId extends EntityId<InvestmentOrderId> {

    /**
     * @brief 构造投资订单标识（Construct Investment Order Identifier）；
     *        Construct investment order identifier.
     *
     * @param value 投资订单 ID（Investment order ID）。
     */
    private InvestmentOrderId(final String value) {
        super(value);
    }

    /**
     * @brief 从原始字符串创建投资订单标识（Factory from Raw String）；
     *        Create investment-order identifier from raw string.
     *
     * @param rawValue 原始 ID（Raw ID value）。
     * @return 投资订单标识（Investment order identifier）。
     */
    public static InvestmentOrderId of(final String rawValue) {
        return new InvestmentOrderId(rawValue);
    }
}
