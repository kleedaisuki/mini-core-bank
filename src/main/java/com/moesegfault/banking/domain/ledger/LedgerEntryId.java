package com.moesegfault.banking.domain.ledger;

import com.moesegfault.banking.domain.shared.EntityId;

/**
 * @brief 账务分录标识值对象（Ledger Entry Identifier Value Object），对应 `account_entry.entry_id`；
 *        Ledger-entry identifier value object mapped to `account_entry.entry_id`.
 */
public final class LedgerEntryId extends EntityId<LedgerEntryId> {

    /**
     * @brief 构造分录标识（Construct Ledger Entry Identifier）；
     *        Construct ledger-entry identifier.
     *
     * @param value 分录 ID 值（Entry ID value）。
     */
    private LedgerEntryId(final String value) {
        super(value);
    }

    /**
     * @brief 从原始字符串创建分录标识（Factory from Raw String）；
     *        Create ledger-entry identifier from raw string.
     *
     * @param rawValue 原始 ID（Raw ID value）。
     * @return 分录标识值对象（Ledger-entry identifier value object）。
     */
    public static LedgerEntryId of(final String rawValue) {
        return new LedgerEntryId(rawValue);
    }
}
