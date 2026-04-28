package com.moesegfault.banking.domain.ledger;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 账务分录实体（Ledger Entry Entity），对齐 `account_entry` 字段与不可变分录语义；
 *        Ledger-entry entity aligned with `account_entry` fields and immutable posting semantics.
 */
public final class LedgerEntry {

    /**
     * @brief 分录 ID（Entry ID）；
     *        Entry identifier.
     */
    private final LedgerEntryId entryId;

    /**
     * @brief 交易 ID（Transaction ID），对应 `account_entry.transaction_id`；
     *        Transaction identifier mapped to `account_entry.transaction_id`.
     */
    private final String transactionId;

    /**
     * @brief 批次 ID（可空）（Batch ID, Nullable）；
     *        Posting-batch identifier, nullable.
     */
    private final PostingBatchId batchId;

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
     * @brief 分录金额（Entry Amount），必须为正；
     *        Entry amount, must be positive.
     */
    private final Money amount;

    /**
     * @brief 入账后账面余额（可空）（Ledger Balance After Posting, Nullable）；
     *        Ledger balance after posting, nullable.
     */
    private final Money ledgerBalanceAfter;

    /**
     * @brief 入账后可用余额（可空）（Available Balance After Posting, Nullable）；
     *        Available balance after posting, nullable.
     */
    private final Money availableBalanceAfter;

    /**
     * @brief 分录类型（Entry Type）；
     *        Entry type.
     */
    private final EntryType entryType;

    /**
     * @brief 入账时间（Posted Timestamp）；
     *        Posted timestamp.
     */
    private final Instant postedAt;

    /**
     * @brief 构造账务分录（Construct Ledger Entry）；
     *        Construct ledger entry.
     *
     * @param entryId               分录 ID（Entry ID）。
     * @param transactionId         交易 ID（Transaction ID）。
     * @param batchId               批次 ID（Batch ID, nullable）。
     * @param accountId             账户 ID（Account ID）。
     * @param currencyCode          币种代码（Currency code）。
     * @param entryDirection        分录方向（Entry direction）。
     * @param amount                分录金额（Entry amount）。
     * @param ledgerBalanceAfter    入账后账面余额（Ledger balance after, nullable）。
     * @param availableBalanceAfter 入账后可用余额（Available balance after, nullable）。
     * @param entryType             分录类型（Entry type）。
     * @param postedAt              入账时间（Posted timestamp）。
     */
    private LedgerEntry(
            final LedgerEntryId entryId,
            final String transactionId,
            final PostingBatchId batchId,
            final String accountId,
            final CurrencyCode currencyCode,
            final EntryDirection entryDirection,
            final Money amount,
            final Money ledgerBalanceAfter,
            final Money availableBalanceAfter,
            final EntryType entryType,
            final Instant postedAt
    ) {
        this.entryId = Objects.requireNonNull(entryId, "Entry ID must not be null");
        this.transactionId = normalizeRequiredId(transactionId, "Transaction ID");
        this.batchId = batchId;
        this.accountId = normalizeRequiredId(accountId, "Account ID");
        this.currencyCode = Objects.requireNonNull(currencyCode, "Currency code must not be null");
        this.entryDirection = Objects.requireNonNull(entryDirection, "Entry direction must not be null");
        this.amount = Objects.requireNonNull(amount, "Amount must not be null");
        this.ledgerBalanceAfter = ledgerBalanceAfter;
        this.availableBalanceAfter = availableBalanceAfter;
        this.entryType = Objects.requireNonNull(entryType, "Entry type must not be null");
        this.postedAt = Objects.requireNonNull(postedAt, "Posted-at must not be null");
        ensureCoreInvariants();
    }

    /**
     * @brief 创建新分录（Create New Entry）；
     *        Create a new ledger entry without snapshot balances.
     *
     * @param entryId        分录 ID（Entry ID）。
     * @param transactionId  交易 ID（Transaction ID）。
     * @param accountId      账户 ID（Account ID）。
     * @param currencyCode   币种代码（Currency code）。
     * @param entryDirection 分录方向（Entry direction）。
     * @param amount         分录金额（Entry amount）。
     * @param entryType      分录类型（Entry type）。
     * @param postedAt       入账时间（Posted timestamp）。
     * @return 新分录实体（New ledger-entry entity）。
     */
    public static LedgerEntry create(
            final LedgerEntryId entryId,
            final String transactionId,
            final String accountId,
            final CurrencyCode currencyCode,
            final EntryDirection entryDirection,
            final Money amount,
            final EntryType entryType,
            final Instant postedAt
    ) {
        return new LedgerEntry(
                entryId,
                transactionId,
                null,
                accountId,
                currencyCode,
                entryDirection,
                amount,
                null,
                null,
                entryType,
                postedAt);
    }

    /**
     * @brief 从持久化状态重建分录（Restore Entry from Persistence）；
     *        Restore ledger entry from persistence state.
     *
     * @param entryId               分录 ID（Entry ID）。
     * @param transactionId         交易 ID（Transaction ID）。
     * @param batchId               批次 ID（Batch ID, nullable）。
     * @param accountId             账户 ID（Account ID）。
     * @param currencyCode          币种代码（Currency code）。
     * @param entryDirection        分录方向（Entry direction）。
     * @param amount                分录金额（Entry amount）。
     * @param ledgerBalanceAfter    入账后账面余额（Ledger balance after, nullable）。
     * @param availableBalanceAfter 入账后可用余额（Available balance after, nullable）。
     * @param entryType             分录类型（Entry type）。
     * @param postedAt              入账时间（Posted timestamp）。
     * @return 重建后的分录实体（Restored ledger-entry entity）。
     */
    public static LedgerEntry restore(
            final LedgerEntryId entryId,
            final String transactionId,
            final PostingBatchId batchId,
            final String accountId,
            final CurrencyCode currencyCode,
            final EntryDirection entryDirection,
            final Money amount,
            final Money ledgerBalanceAfter,
            final Money availableBalanceAfter,
            final EntryType entryType,
            final Instant postedAt
    ) {
        return new LedgerEntry(
                entryId,
                transactionId,
                batchId,
                accountId,
                currencyCode,
                entryDirection,
                amount,
                ledgerBalanceAfter,
                availableBalanceAfter,
                entryType,
                postedAt);
    }

    /**
     * @brief 绑定入账批次（Attach Posting Batch）；
     *        Attach posting batch to this entry immutably.
     *
     * @param postingBatchId 批次 ID（Posting batch ID）。
     * @return 绑定后的分录实体（Entry entity with batch attached）。
     */
    public LedgerEntry attachBatch(final PostingBatchId postingBatchId) {
        final PostingBatchId normalizedBatchId = Objects.requireNonNull(postingBatchId, "Posting batch ID must not be null");
        if (batchId != null && !batchId.equals(normalizedBatchId)) {
            throw new BusinessRuleViolation("Ledger entry already attached to another posting batch");
        }
        if (batchId != null) {
            return this;
        }
        return new LedgerEntry(
                entryId,
                transactionId,
                normalizedBatchId,
                accountId,
                currencyCode,
                entryDirection,
                amount,
                ledgerBalanceAfter,
                availableBalanceAfter,
                entryType,
                postedAt);
    }

    /**
     * @brief 填充分录后的余额快照（Attach Balance-After Snapshot）；
     *        Attach balance-after snapshot for this entry immutably.
     *
     * @param balanceAfter 分录后的余额（Balance after posting）。
     * @return 带余额快照的分录实体（Entry entity with snapshot）。
     */
    public LedgerEntry withBalanceAfter(final Balance balanceAfter) {
        final Balance normalizedBalance = Objects.requireNonNull(balanceAfter, "Balance must not be null");
        if (!accountId.equals(normalizedBalance.accountId())) {
            throw new BusinessRuleViolation("Balance account ID must match entry account ID");
        }
        if (!currencyCode.equals(normalizedBalance.currencyCode())) {
            throw new BusinessRuleViolation("Balance currency must match entry currency");
        }
        return new LedgerEntry(
                entryId,
                transactionId,
                batchId,
                accountId,
                currencyCode,
                entryDirection,
                amount,
                normalizedBalance.ledgerBalance(),
                normalizedBalance.availableBalance(),
                entryType,
                postedAt);
    }

    /**
     * @brief 构建分录入账事件（Build Ledger Entry Posted Event）；
     *        Build ledger-entry-posted domain event.
     *
     * @return 分录入账事件（Ledger-entry-posted event）。
     */
    public LedgerEntryPosted postedEvent() {
        return new LedgerEntryPosted(
                entryId,
                transactionId,
                batchId,
                accountId,
                currencyCode,
                entryDirection,
                amount,
                entryType,
                postedAt);
    }

    /**
     * @brief 返回分录 ID（Return Entry ID）；
     *        Return entry identifier.
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
    public PostingBatchId batchIdOrNull() {
        return batchId;
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
     * @brief 返回入账后账面余额（可空）（Return Ledger Balance After, Nullable）；
     *        Return ledger balance after posting, nullable.
     *
     * @return 账面余额快照或 null（Ledger balance snapshot or null）。
     */
    public Money ledgerBalanceAfterOrNull() {
        return ledgerBalanceAfter;
    }

    /**
     * @brief 返回入账后可用余额（可空）（Return Available Balance After, Nullable）；
     *        Return available balance after posting, nullable.
     *
     * @return 可用余额快照或 null（Available balance snapshot or null）。
     */
    public Money availableBalanceAfterOrNull() {
        return availableBalanceAfter;
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
     * @brief 返回入账时间（Return Posted Timestamp）；
     *        Return posted timestamp.
     *
     * @return 入账时间（Posted timestamp）。
     */
    public Instant postedAt() {
        return postedAt;
    }

    /**
     * @brief 统一校验核心不变量（Ensure Core Invariants）；
     *        Ensure core invariants.
     */
    private void ensureCoreInvariants() {
        if (!amount.isPositive()) {
            throw new BusinessRuleViolation("Ledger entry amount must be positive");
        }
        if (!amount.currencyCode().equals(currencyCode)) {
            throw new BusinessRuleViolation("Ledger entry amount currency must match entry currency");
        }
        if (ledgerBalanceAfter != null && !ledgerBalanceAfter.currencyCode().equals(currencyCode)) {
            throw new BusinessRuleViolation("Ledger balance-after currency must match entry currency");
        }
        if (availableBalanceAfter != null && !availableBalanceAfter.currencyCode().equals(currencyCode)) {
            throw new BusinessRuleViolation("Available balance-after currency must match entry currency");
        }
        if (ledgerBalanceAfter != null && availableBalanceAfter != null
                && availableBalanceAfter.compareTo(ledgerBalanceAfter) > 0) {
            throw new BusinessRuleViolation("Available balance-after must not exceed ledger balance-after");
        }
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
