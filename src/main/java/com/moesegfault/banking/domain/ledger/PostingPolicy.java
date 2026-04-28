package com.moesegfault.banking.domain.ledger;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 入账策略（Posting Policy），统一幂等与状态约束校验；
 *        Posting policy centralizing idempotency and posting-status constraints.
 */
public final class PostingPolicy {

    /**
     * @brief 工具类私有构造（Utility Private Constructor）；
     *        Private constructor for utility class.
     */
    private PostingPolicy() {
        // Utility class.
    }

    /**
     * @brief 校验批次必须处于待入账状态（Ensure Batch Is Pending）；
     *        Ensure posting batch is in pending status.
     *
     * @param postingBatch 入账批次（Posting batch）。
     */
    public static void ensurePending(final PostingBatch postingBatch) {
        final PostingBatch normalizedBatch = Objects.requireNonNull(postingBatch, "Posting batch must not be null");
        if (normalizedBatch.batchStatus() != PostingStatus.PENDING) {
            throw new BusinessRuleViolation("Posting batch must be PENDING");
        }
    }

    /**
     * @brief 校验交易幂等性（Ensure Transaction Idempotency）；
     *        Ensure transaction idempotency by existing batch lookup.
     *
     * @param existingBatchOpt     已存在批次（Existing batch optional）。
     * @param expectedTransactionId 期望交易 ID（Expected transaction ID）。
     */
    public static void ensureTransactionIdempotency(
            final Optional<PostingBatch> existingBatchOpt,
            final String expectedTransactionId
    ) {
        final String normalizedTransactionId = normalizeRequiredId(expectedTransactionId, "Expected transaction ID");
        Objects.requireNonNull(existingBatchOpt, "Existing batch optional must not be null");
        if (existingBatchOpt.isEmpty()) {
            return;
        }
        final PostingBatch existingBatch = existingBatchOpt.get();
        if (!normalizedTransactionId.equals(existingBatch.transactionId())) {
            throw new BusinessRuleViolation("Existing posting batch transaction ID mismatches request");
        }
        if (existingBatch.batchStatus() == PostingStatus.FAILED) {
            throw new BusinessRuleViolation("Existing posting batch is FAILED and not idempotent-success");
        }
    }

    /**
     * @brief 校验分录不可变（Ensure Entry Immutability）；
     *        Ensure immutable entry fields remain unchanged.
     *
     * @param persistedEntry 已持久化分录（Persisted entry）。
     * @param candidateEntry 候选分录（Candidate entry）。
     */
    public static void ensureEntryImmutable(
            final LedgerEntry persistedEntry,
            final LedgerEntry candidateEntry
    ) {
        final LedgerEntry persisted = Objects.requireNonNull(persistedEntry, "Persisted entry must not be null");
        final LedgerEntry candidate = Objects.requireNonNull(candidateEntry, "Candidate entry must not be null");
        if (!persisted.entryId().equals(candidate.entryId())
                || !persisted.transactionId().equals(candidate.transactionId())
                || !persisted.accountId().equals(candidate.accountId())
                || !persisted.currencyCode().equals(candidate.currencyCode())
                || persisted.entryDirection() != candidate.entryDirection()
                || !persisted.amount().equals(candidate.amount())
                || persisted.entryType() != candidate.entryType()) {
            throw new BusinessRuleViolation("Ledger entry immutable fields mismatch");
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
