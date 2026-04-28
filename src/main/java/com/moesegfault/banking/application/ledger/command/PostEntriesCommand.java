package com.moesegfault.banking.application.ledger.command;

import com.moesegfault.banking.domain.ledger.EntryDirection;
import com.moesegfault.banking.domain.ledger.EntryType;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * @brief 入账命令（Post Entries Command），承载同一业务交易下的一组分录入账请求；
 *        Posting command carrying a group of entry-posting requests under one transaction.
 *
 * @param transactionId     交易 ID（Transaction ID），对齐 `account_entry.transaction_id`。
 * @param idempotencyKey    幂等键（可空）（Idempotency key, nullable）。
 * @param postingRequests   分录入账请求列表（Entry posting request list）。
 * @param requestedPostedAt 指定入账时间（可空）（Requested posted-at time, nullable）。
 */
public record PostEntriesCommand(
        String transactionId,
        String idempotencyKey,
        List<PostingRequest> postingRequests,
        Instant requestedPostedAt
) {

    /**
     * @brief 紧凑构造并校验命令参数（Compact Constructor with Validation）；
     *        Compact constructor validating command fields.
     */
    public PostEntriesCommand {
        transactionId = normalizeRequiredId(transactionId, "Transaction ID");
        idempotencyKey = normalizeNullableIdempotencyKey(idempotencyKey);
        Objects.requireNonNull(postingRequests, "Posting requests must not be null");
        if (postingRequests.isEmpty()) {
            throw new IllegalArgumentException("Posting requests must not be empty");
        }
        postingRequests = List.copyOf(postingRequests);
        requestedPostedAt = requestedPostedAt;
    }

    /**
     * @brief 无指定入账时间构造命令（Construct Command Without Explicit Posted-at）；
     *        Construct command without explicit posted-at timestamp.
     *
     * @param transactionId   交易 ID（Transaction ID）。
     * @param idempotencyKey  幂等键（Idempotency key, nullable）。
     * @param postingRequests 分录请求列表（Posting request list）。
     */
    public PostEntriesCommand(
            final String transactionId,
            final String idempotencyKey,
            final List<PostingRequest> postingRequests
    ) {
        this(transactionId, idempotencyKey, postingRequests, null);
    }

    /**
     * @brief 返回有效入账时间（Resolve Effective Posted-at Time）；
     *        Resolve effective posted-at timestamp for posting operation.
     *
     * @return 指定时间或当前时间（Requested timestamp or current time）。
     */
    public Instant effectivePostedAt() {
        return requestedPostedAt == null ? Instant.now() : requestedPostedAt;
    }

    /**
     * @brief 分录入账请求项（Entry Posting Request Item），一项对应一条账务分录；
     *        Entry-posting request item where one item maps to one ledger entry.
     *
     * @param accountId      账户 ID（Account ID）。
     * @param currencyCode   币种代码（Currency code）。
     * @param entryDirection 分录方向（Entry direction）。
     * @param amount         分录金额（Entry amount）。
     * @param entryType      分录类型（Entry type）。
     */
    public record PostingRequest(
            String accountId,
            CurrencyCode currencyCode,
            EntryDirection entryDirection,
            Money amount,
            EntryType entryType
    ) {

        /**
         * @brief 紧凑构造并校验请求项参数（Compact Constructor with Validation）；
         *        Compact constructor validating posting request fields.
         */
        public PostingRequest {
            accountId = normalizeRequiredId(accountId, "Account ID");
            currencyCode = Objects.requireNonNull(currencyCode, "Currency code must not be null");
            entryDirection = Objects.requireNonNull(entryDirection, "Entry direction must not be null");
            amount = Objects.requireNonNull(amount, "Amount must not be null");
            entryType = Objects.requireNonNull(entryType, "Entry type must not be null");
            if (!amount.isPositive()) {
                throw new IllegalArgumentException("Entry amount must be positive");
            }
            if (!amount.currencyCode().equals(currencyCode)) {
                throw new IllegalArgumentException("Entry amount currency must match currency code");
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
     * @brief 标准化可空幂等键（Normalize Nullable Idempotency Key）；
     *        Normalize nullable idempotency key.
     *
     * @param rawValue 原始值（Raw value）。
     * @return 标准化值或 null（Normalized value or null）。
     */
    private static String normalizeNullableIdempotencyKey(final String rawValue) {
        if (rawValue == null) {
            return null;
        }
        final String normalized = rawValue.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
