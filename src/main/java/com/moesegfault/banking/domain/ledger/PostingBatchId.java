package com.moesegfault.banking.domain.ledger;

import com.moesegfault.banking.domain.shared.EntityId;

/**
 * @brief 入账批次标识值对象（Posting Batch Identifier Value Object），对应 `posting_batch.batch_id`；
 *        Posting-batch identifier value object mapped to `posting_batch.batch_id`.
 */
public final class PostingBatchId extends EntityId<PostingBatchId> {

    /**
     * @brief 构造入账批次标识（Construct Posting Batch Identifier）；
     *        Construct posting-batch identifier.
     *
     * @param value 批次 ID 值（Batch ID value）。
     */
    private PostingBatchId(final String value) {
        super(value);
    }

    /**
     * @brief 从原始字符串创建入账批次标识（Factory from Raw String）；
     *        Create posting-batch identifier from raw string.
     *
     * @param rawValue 原始 ID（Raw ID value）。
     * @return 入账批次标识值对象（Posting-batch identifier value object）。
     */
    public static PostingBatchId of(final String rawValue) {
        return new PostingBatchId(rawValue);
    }
}
