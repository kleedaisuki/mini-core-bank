package com.moesegfault.banking.application.ledger.result;

import com.moesegfault.banking.domain.ledger.EntryDirection;
import com.moesegfault.banking.domain.ledger.EntryType;
import com.moesegfault.banking.domain.ledger.LedgerEntry;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 分录结果视图（Ledger Entry Result View），统一 application 层分录输出结构；
 *        Ledger-entry result view unifying application-layer entry output structure.
 *
 * @param entryId               分录 ID（Entry ID）。
 * @param transactionId         交易 ID（Transaction ID）。
 * @param batchId               批次 ID（可空）（Batch ID, nullable）。
 * @param accountId             账户 ID（Account ID）。
 * @param currencyCode          币种代码（Currency code）。
 * @param entryDirection        分录方向（Entry direction）。
 * @param amount                分录金额（Entry amount）。
 * @param ledgerBalanceAfter    入账后账面余额（可空）（Ledger balance after, nullable）。
 * @param availableBalanceAfter 入账后可用余额（可空）（Available balance after, nullable）。
 * @param entryType             分录类型（Entry type）。
 * @param postedAt              入账时间（Posted timestamp）。
 */
public record LedgerEntryResult(
        String entryId,
        String transactionId,
        String batchId,
        String accountId,
        CurrencyCode currencyCode,
        EntryDirection entryDirection,
        Money amount,
        Money ledgerBalanceAfter,
        Money availableBalanceAfter,
        EntryType entryType,
        Instant postedAt
) {

    /**
     * @brief 紧凑构造并校验核心字段（Compact Constructor with Invariant Checks）；
     *        Compact constructor validating core fields.
     */
    public LedgerEntryResult {
        entryId = normalizeRequiredId(entryId, "Entry ID");
        transactionId = normalizeRequiredId(transactionId, "Transaction ID");
        batchId = normalizeNullableId(batchId);
        accountId = normalizeRequiredId(accountId, "Account ID");
        currencyCode = Objects.requireNonNull(currencyCode, "Currency code must not be null");
        entryDirection = Objects.requireNonNull(entryDirection, "Entry direction must not be null");
        amount = Objects.requireNonNull(amount, "Amount must not be null");
        validateNullableMoneyCurrency(ledgerBalanceAfter, currencyCode, "Ledger balance-after");
        validateNullableMoneyCurrency(availableBalanceAfter, currencyCode, "Available balance-after");
        entryType = Objects.requireNonNull(entryType, "Entry type must not be null");
        postedAt = Objects.requireNonNull(postedAt, "Posted-at must not be null");
        if (!amount.currencyCode().equals(currencyCode)) {
            throw new IllegalArgumentException("Entry amount currency must match currency code");
        }
    }

    /**
     * @brief 从领域分录转换结果视图（Convert from Domain Ledger Entry）；
     *        Convert domain ledger entry to application result view.
     *
     * @param entry 领域分录实体（Domain ledger-entry entity）。
     * @return 分录结果视图（Ledger-entry result view）。
     */
    public static LedgerEntryResult fromDomain(final LedgerEntry entry) {
        final LedgerEntry normalized = Objects.requireNonNull(entry, "Ledger entry must not be null");
        return new LedgerEntryResult(
                normalized.entryId().value(),
                normalized.transactionId(),
                normalized.batchIdOrNull() == null ? null : normalized.batchIdOrNull().value(),
                normalized.accountId(),
                normalized.currencyCode(),
                normalized.entryDirection(),
                normalized.amount(),
                normalized.ledgerBalanceAfterOrNull(),
                normalized.availableBalanceAfterOrNull(),
                normalized.entryType(),
                normalized.postedAt());
    }

    /**
     * @brief 标准化并校验必填标识（Normalize Required Identifier）；
     *        Normalize and validate required identifier.
     *
     * @param rawValue 原始值（Raw value）。
     * @param label    字段标签（Field label）。
     * @return 标准化标识（Normalized identifier）。
     */
    private static String normalizeRequiredId(final String rawValue, final String label) {
        if (rawValue == null) {
            throw new IllegalArgumentException(label + " must not be null");
        }
        final String normalized = rawValue.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
        return normalized;
    }

    /**
     * @brief 标准化可空标识（Normalize Nullable Identifier）；
     *        Normalize nullable identifier.
     *
     * @param rawValue 原始值（Raw value）。
     * @return 标准化结果或 null（Normalized value or null）。
     */
    private static String normalizeNullableId(final String rawValue) {
        if (rawValue == null) {
            return null;
        }
        final String normalized = rawValue.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    /**
     * @brief 校验可空金额币种一致性（Validate Nullable Money Currency Consistency）；
     *        Validate that nullable money value uses expected currency when present.
     *
     * @param money            可空金额（Nullable money value）。
     * @param expectedCurrency 期望币种（Expected currency code）。
     * @param label            字段标签（Field label）。
     */
    private static void validateNullableMoneyCurrency(
            final Money money,
            final CurrencyCode expectedCurrency,
            final String label
    ) {
        if (money != null && !money.currencyCode().equals(expectedCurrency)) {
            throw new IllegalArgumentException(label + " currency must match currency code");
        }
    }
}
