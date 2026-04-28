package com.moesegfault.banking.domain.ledger;

import com.moesegfault.banking.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 入账冲正事件（Posting Reversed Event），表示已入账批次被冲正；
 *        Posting-reversed event indicating a posted batch has been reversed.
 */
public final class PostingReversed implements DomainEvent {

    /**
     * @brief 批次 ID（Batch ID）；
     *        Posting-batch identifier.
     */
    private final PostingBatchId postingBatchId;

    /**
     * @brief 交易 ID（Transaction ID）；
     *        Transaction identifier.
     */
    private final String transactionId;

    /**
     * @brief 事件时间（Occurred Timestamp）；
     *        Event occurred timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造入账冲正事件（Construct Posting Reversed Event）；
     *        Construct posting-reversed event.
     *
     * @param postingBatchId 批次 ID（Posting-batch ID）。
     * @param transactionId  交易 ID（Transaction ID）。
     * @param occurredAt     事件时间（Occurred timestamp）。
     */
    public PostingReversed(
            final PostingBatchId postingBatchId,
            final String transactionId,
            final Instant occurredAt
    ) {
        this.postingBatchId = Objects.requireNonNull(postingBatchId, "Posting batch ID must not be null");
        this.transactionId = normalizeRequiredId(transactionId, "Transaction ID");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
    }

    /**
     * @brief 返回批次 ID（Return Batch ID）；
     *        Return posting-batch identifier.
     *
     * @return 批次 ID（Batch ID）。
     */
    public PostingBatchId postingBatchId() {
        return postingBatchId;
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
