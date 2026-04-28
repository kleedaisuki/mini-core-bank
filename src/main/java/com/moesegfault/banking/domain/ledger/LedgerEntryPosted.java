package com.moesegfault.banking.domain.ledger;

import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DomainEvent;
import com.moesegfault.banking.domain.shared.Money;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 账务分录已入账事件（Ledger Entry Posted Event），表示分录已被确认入账；
 *        Ledger-entry-posted event indicating entry posting confirmation.
 */
public final class LedgerEntryPosted implements DomainEvent {

    /**
     * @brief 分录 ID（Entry ID）；
     *        Ledger-entry identifier.
     */
    private final LedgerEntryId entryId;

    /**
     * @brief 交易 ID（Transaction ID）；
     *        Transaction identifier.
     */
    private final String transactionId;

    /**
     * @brief 批次 ID（可空）（Batch ID, Nullable）；
     *        Posting-batch identifier, nullable.
     */
    private final PostingBatchId postingBatchId;

    /**
     * @brief 账户 ID（Account ID）；
     *        Account identifier.
     */
    private final String accountId;

    /**
     * @brief 币种代码（Currency Code）；
     *        Currency code.
     */
    private final CurrencyCode currencyCode;

    /**
     * @brief 分录方向（Entry Direction）；
     *        Entry direction.
     */
    private final EntryDirection entryDirection;

    /**
     * @brief 分录金额（Entry Amount）；
     *        Entry amount.
     */
    private final Money amount;

    /**
     * @brief 分录类型（Entry Type）；
     *        Entry type.
     */
    private final EntryType entryType;

    /**
     * @brief 事件时间（Occurred Timestamp）；
     *        Event occurred timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造分录入账事件（Construct Ledger Entry Posted Event）；
     *        Construct ledger-entry-posted event.
     *
     * @param entryId        分录 ID（Entry ID）。
     * @param transactionId  交易 ID（Transaction ID）。
     * @param postingBatchId 批次 ID（Batch ID, nullable）。
     * @param accountId      账户 ID（Account ID）。
     * @param currencyCode   币种代码（Currency code）。
     * @param entryDirection 分录方向（Entry direction）。
     * @param amount         分录金额（Entry amount）。
     * @param entryType      分录类型（Entry type）。
     * @param occurredAt     事件时间（Occurred timestamp）。
     */
    public LedgerEntryPosted(
            final LedgerEntryId entryId,
            final String transactionId,
            final PostingBatchId postingBatchId,
            final String accountId,
            final CurrencyCode currencyCode,
            final EntryDirection entryDirection,
            final Money amount,
            final EntryType entryType,
            final Instant occurredAt
    ) {
        this.entryId = Objects.requireNonNull(entryId, "Entry ID must not be null");
        this.transactionId = normalizeRequiredId(transactionId, "Transaction ID");
        this.postingBatchId = postingBatchId;
        this.accountId = normalizeRequiredId(accountId, "Account ID");
        this.currencyCode = Objects.requireNonNull(currencyCode, "Currency code must not be null");
        this.entryDirection = Objects.requireNonNull(entryDirection, "Entry direction must not be null");
        this.amount = Objects.requireNonNull(amount, "Amount must not be null");
        this.entryType = Objects.requireNonNull(entryType, "Entry type must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
        if (!this.amount.currencyCode().equals(this.currencyCode)) {
            throw new IllegalArgumentException("Event amount currency must match event currency code");
        }
    }

    /**
     * @brief 返回分录 ID（Return Entry ID）；
     *        Return ledger-entry identifier.
     *
     * @return 分录 ID（Entry ID）。
     */
    public LedgerEntryId entryId() {
        return entryId;
    }

    /**
     * @brief 返回交易 ID（Return Transaction ID）；
     *        Return transaction identifier.
     *
     * @return 交易 ID（Transaction ID）。
     */
    public String transactionId() {
        return transactionId;
    }

    /**
     * @brief 返回批次 ID（可空）（Return Batch ID, Nullable）；
     *        Return posting-batch identifier, nullable.
     *
     * @return 批次 ID 或 null（Batch ID or null）。
     */
    public PostingBatchId postingBatchIdOrNull() {
        return postingBatchId;
    }

    /**
     * @brief 返回账户 ID（Return Account ID）；
     *        Return account identifier.
     *
     * @return 账户 ID（Account ID）。
     */
    public String accountId() {
        return accountId;
    }

    /**
     * @brief 返回币种代码（Return Currency Code）；
     *        Return currency code.
     *
     * @return 币种代码（Currency code）。
     */
    public CurrencyCode currencyCode() {
        return currencyCode;
    }

    /**
     * @brief 返回分录方向（Return Entry Direction）；
     *        Return entry direction.
     *
     * @return 分录方向（Entry direction）。
     */
    public EntryDirection entryDirection() {
        return entryDirection;
    }

    /**
     * @brief 返回分录金额（Return Entry Amount）；
     *        Return entry amount.
     *
     * @return 分录金额（Entry amount）。
     */
    public Money amount() {
        return amount;
    }

    /**
     * @brief 返回分录类型（Return Entry Type）；
     *        Return entry type.
     *
     * @return 分录类型（Entry type）。
     */
    public EntryType entryType() {
        return entryType;
    }

    /**
     * @brief 返回事件时间（Return Occurred Timestamp）；
     *        Return event occurred timestamp.
     *
     * @return 事件时间（Occurred timestamp）。
     */
    @Override
    public Instant occurredAt() {
        return occurredAt;
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
}
