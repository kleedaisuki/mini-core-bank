package com.moesegfault.banking.domain.investment;

import com.moesegfault.banking.domain.shared.EntityId;

/**
 * @brief 持仓标识值对象（Holding Identifier Value Object），对应 `investment_holding.holding_id`；
 *        Holding identifier value object mapped to `investment_holding.holding_id`.
 */
public final class HoldingId extends EntityId<HoldingId> {

    /**
     * @brief 构造持仓标识（Construct Holding Identifier）；
     *        Construct holding identifier.
     *
     * @param value 持仓 ID（Holding ID）。
     */
    private HoldingId(final String value) {
        super(value);
    }

    /**
     * @brief 从原始字符串创建持仓标识（Factory from Raw String）；
     *        Create holding identifier from raw string.
     *
     * @param rawValue 原始 ID（Raw ID value）。
     * @return 持仓标识值对象（Holding identifier value object）。
     */
    public static HoldingId of(final String rawValue) {
        return new HoldingId(rawValue);
    }
}
