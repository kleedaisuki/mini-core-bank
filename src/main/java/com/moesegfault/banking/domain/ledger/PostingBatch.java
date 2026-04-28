package com.moesegfault.banking.domain.ledger;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @brief 入账批次实体（Posting Batch Entity），对齐 `posting_batch` 并承载一组不可变分录提交；
 *        Posting-batch entity aligned with `posting_batch` and carrying a set of immutable entries.
 */
public final class PostingBatch {

    /**
     * @brief 批次 ID（Batch ID）；
     *        Batch identifier.
     */
    private final PostingBatchId batchId;

    /**
     * @brief 交易 ID（Transaction ID）；
     *        Transaction identifier.
     */
    private final String transactionId;

    /**
     * @brief 幂等键（可空）（Idempotency Key, Nullable）；
     *        Idempotency key, nullable.
     */
    private final String idempotencyKey;

    /**
     * @brief 批次状态（Posting Status）；
     *        Batch posting status.
     */
    private PostingStatus batchStatus;

    /**
     * @brief 入账时间（可空）（Posted Timestamp, Nullable）；
     *        Posted timestamp, nullable.
     */
    private Instant postedAt;

    /**
     * @brief 创建时间（Created Timestamp）；
     *        Created timestamp.
     */
    private final Instant createdAt;

    /**
     * @brief 批次分录集合（Batch Entries）；
     *        Entries belonging to the batch.
     */
    private final List<LedgerEntry> entries;

    /**
     * @brief 构造入账批次实体（Construct Posting Batch Entity）；
     *        Construct posting-batch entity.
     *
     * @param batchId        批次 ID（Batch ID）。
     * @param transactionId  交易 ID（Transaction ID）。
     * @param idempotencyKey 幂等键（Idempotency key, nullable）。
     * @param batchStatus    批次状态（Batch status）。
     * @param postedAt       入账时间（Posted timestamp, nullable）。
     * @param createdAt      创建时间（Created timestamp）。
     * @param entries        分录集合（Entry list）。
     */
    private PostingBatch(
            final PostingBatchId batchId,
            final String transactionId,
            final String idempotencyKey,
            final PostingStatus batchStatus,
            final Instant postedAt,
            final Instant createdAt,
            final List<LedgerEntry> entries
    ) {
        this.batchId = Objects.requireNonNull(batchId, "Batch ID must not be null");
        this.transactionId = normalizeRequiredId(transactionId, "Transaction ID");
        this.idempotencyKey = normalizeNullableIdempotencyKey(idempotencyKey);
        this.batchStatus = Objects.requireNonNull(batchStatus, "Batch status must not be null");
        this.postedAt = postedAt;
        this.createdAt = Objects.requireNonNull(createdAt, "Created-at must not be null");
        this.entries = new ArrayList<>(Objects.requireNonNull(entries, "Entries must not be null"));
        ensureCoreInvariants();
    }

    /**
     * @brief 创建待入账批次（Create Pending Posting Batch）；
     *        Create a pending posting batch.
     *
     * @param batchId        批次 ID（Batch ID）。
     * @param transactionId  交易 ID（Transaction ID）。
     * @param idempotencyKey 幂等键（Idempotency key, nullable）。
     * @param createdAt      创建时间（Created timestamp）。
     * @return 待入账批次实体（Pending posting-batch entity）。
     */
    public static PostingBatch createPending(
            final PostingBatchId batchId,
            final String transactionId,
            final String idempotencyKey,
            final Instant createdAt
    ) {
        return new PostingBatch(
                batchId,
                transactionId,
                idempotencyKey,
                PostingStatus.PENDING,
                null,
                Objects.requireNonNull(createdAt, "Created-at must not be null"),
                List.of());
    }

    /**
     * @brief 从持久化状态重建批次（Restore Posting Batch from Persistence）；
     *        Restore posting batch from persistence state.
     *
     * @param batchId        批次 ID（Batch ID）。
     * @param transactionId  交易 ID（Transaction ID）。
     * @param idempotencyKey 幂等键（Idempotency key, nullable）。
     * @param batchStatus    批次状态（Batch status）。
     * @param postedAt       入账时间（Posted timestamp, nullable）。
     * @param createdAt      创建时间（Created timestamp）。
     * @param entries        分录集合（Entry list）。
     * @return 重建后的批次实体（Restored posting-batch entity）。
     */
    public static PostingBatch restore(
            final PostingBatchId batchId,
            final String transactionId,
            final String idempotencyKey,
            final PostingStatus batchStatus,
            final Instant postedAt,
            final Instant createdAt,
            final List<LedgerEntry> entries
    ) {
        return new PostingBatch(
                batchId,
                transactionId,
                idempotencyKey,
                batchStatus,
                postedAt,
                createdAt,
                entries == null ? List.of() : entries);
    }

    /**
     * @brief 添加分录到批次（Add Entry to Batch）；
     *        Add an entry to this batch when status is pending.
     *
     * @param entry 分录实体（Ledger entry）。
     */
    public void addEntry(final LedgerEntry entry) {
        requirePendingStatus("add entry");
        final LedgerEntry normalizedEntry = Objects.requireNonNull(entry, "Entry must not be null");
        if (!transactionId.equals(normalizedEntry.transactionId())) {
            throw new BusinessRuleViolation("Entry transaction ID must match posting batch transaction ID");
        }
        final PostingBatchId existingBatchId = normalizedEntry.batchIdOrNull();
        if (existingBatchId != null && !batchId.equals(existingBatchId)) {
            throw new BusinessRuleViolation("Entry already attached to another posting batch");
        }
        entries.add(normalizedEntry.attachBatch(batchId));
    }

    /**
     * @brief 标记批次已入账（Mark Batch as Posted）；
     *        Mark this batch as posted.
     *
     * @param postedTime 入账时间（Posted timestamp）。
     */
    public void markPosted(final Instant postedTime) {
        requirePendingStatus("mark posted");
        if (entries.isEmpty()) {
            throw new BusinessRuleViolation("Posting batch must contain at least one entry before posting");
        }
        final Instant normalizedPostedTime = Objects.requireNonNull(postedTime, "Posted time must not be null");
        if (normalizedPostedTime.isBefore(createdAt)) {
            throw new BusinessRuleViolation("Posted time must not be earlier than created time");
        }
        batchStatus.requireTransitionTo(PostingStatus.POSTED);
        batchStatus = PostingStatus.POSTED;
        postedAt = normalizedPostedTime;
    }

    /**
     * @brief 标记批次失败（Mark Batch as Failed）；
     *        Mark this batch as failed.
     */
    public void markFailed() {
        requirePendingStatus("mark failed");
        batchStatus.requireTransitionTo(PostingStatus.FAILED);
        batchStatus = PostingStatus.FAILED;
    }

    /**
     * @brief 冲正已入账批次（Reverse Posted Batch）；
     *        Reverse this posted batch.
     *
     * @param reversedTime 冲正时间（Reversed timestamp）。
     */
    public void reverse(final Instant reversedTime) {
        Objects.requireNonNull(reversedTime, "Reversed time must not be null");
        if (batchStatus != PostingStatus.POSTED) {
            throw new BusinessRuleViolation("Only POSTED batch can be reversed");
        }
        if (postedAt != null && reversedTime.isBefore(postedAt)) {
            throw new BusinessRuleViolation("Reversed time must not be earlier than posted time");
        }
        batchStatus.requireTransitionTo(PostingStatus.REVERSED);
        batchStatus = PostingStatus.REVERSED;
    }

    /**
     * @brief 判断是否已入账（Check Posted Status）；
     *        Check whether batch is posted.
     *
     * @return 已入账返回 true（true when posted）。
     */
    public boolean isPosted() {
        return batchStatus == PostingStatus.POSTED;
    }

    /**
     * @brief 构建冲正事件（Build Posting Reversed Event）；
     *        Build posting-reversed domain event.
     *
     * @return 冲正事件（Posting-reversed event）。
     */
    public PostingReversed reversedEvent() {
        if (batchStatus != PostingStatus.REVERSED) {
            throw new BusinessRuleViolation("PostingReversed event can only be built for REVERSED status");
        }
        return new PostingReversed(batchId, transactionId, Instant.now());
    }

    /**
     * @brief 返回批次 ID（Return Batch ID）；
     *        Return batch identifier.
     *
     * @return 批次 ID（Batch ID）。
     */
    public PostingBatchId batchId() {
        return batchId;
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
     * @brief 返回幂等键（可空）（Return Idempotency Key, Nullable）；
     *        Return idempotency key, nullable.
     *
     * @return 幂等键或 null（Idempotency key or null）。
     */
    public String idempotencyKeyOrNull() {
        return idempotencyKey;
    }

    /**
     * @brief 返回批次状态（Return Batch Status）；
     *        Return batch status.
     *
     * @return 批次状态（Batch status）。
     */
    public PostingStatus batchStatus() {
        return batchStatus;
    }

    /**
     * @brief 返回入账时间（可空）（Return Posted Timestamp, Nullable）；
     *        Return posted timestamp, nullable.
     *
     * @return 入账时间或 null（Posted timestamp or null）。
     */
    public Instant postedAtOrNull() {
        return postedAt;
    }

    /**
     * @brief 返回创建时间（Return Created Timestamp）；
     *        Return created timestamp.
     *
     * @return 创建时间（Created timestamp）。
     */
    public Instant createdAt() {
        return createdAt;
    }

    /**
     * @brief 返回只读分录列表（Return Read-only Entry List）；
     *        Return read-only list of entries.
     *
     * @return 分录列表只读视图（Read-only entry list）。
     */
    public List<LedgerEntry> entries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * @brief 要求当前为待入账状态（Require Pending Status）；
     *        Require current status to be pending.
     *
     * @param operation 操作描述（Operation description）。
     */
    private void requirePendingStatus(final String operation) {
        if (batchStatus != PostingStatus.PENDING) {
            throw new BusinessRuleViolation("Cannot " + operation + " when batch status is " + batchStatus);
        }
    }

    /**
     * @brief 统一校验核心不变量（Ensure Core Invariants）；
     *        Ensure core invariants.
     */
    private void ensureCoreInvariants() {
        if (postedAt != null && postedAt.isBefore(createdAt)) {
            throw new BusinessRuleViolation("Posted time must not be earlier than created time");
        }
        if (batchStatus == PostingStatus.POSTED && postedAt == null) {
            throw new BusinessRuleViolation("POSTED batch must have posted time");
        }
        for (LedgerEntry entry : entries) {
            if (!transactionId.equals(entry.transactionId())) {
                throw new BusinessRuleViolation("All entries must share batch transaction ID");
            }
            final PostingBatchId entryBatchId = entry.batchIdOrNull();
            if (entryBatchId != null && !batchId.equals(entryBatchId)) {
                throw new BusinessRuleViolation("Entry batch ID must match posting batch ID");
            }
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

    /**
     * @brief 标准化幂等键（Normalize Idempotency Key）；
     *        Normalize idempotency key.
     *
     * @param rawKey 原始幂等键（Raw idempotency key）。
     * @return 标准化幂等键或 null（Normalized idempotency key or null）。
     */
    private static String normalizeNullableIdempotencyKey(final String rawKey) {
        if (rawKey == null) {
            return null;
        }
        final String normalized = rawKey.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
