package com.moesegfault.banking.domain.credit;

import com.moesegfault.banking.domain.shared.EntityId;

/**
 * @brief 账单标识值对象（Statement Identifier Value Object），对应 `credit_card_statement.statement_id`；
 *        Statement identifier value object mapped to `credit_card_statement.statement_id`.
 */
public final class StatementId extends EntityId<StatementId> {

    /**
     * @brief 构造账单标识（Construct Statement Identifier）；
     *        Construct statement identifier.
     *
     * @param value 账单 ID 值（Statement ID value）。
     */
    private StatementId(final String value) {
        super(value);
    }

    /**
     * @brief 从原始字符串创建账单标识（Factory from Raw String）；
     *        Create statement identifier from raw string.
     *
     * @param rawValue 原始 ID 值（Raw ID value）。
     * @return 账单标识值对象（Statement identifier value object）。
     */
    public static StatementId of(final String rawValue) {
        return new StatementId(rawValue);
    }
}
