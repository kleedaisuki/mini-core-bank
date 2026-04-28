package com.moesegfault.banking.application.business.query;

import com.moesegfault.banking.domain.business.BusinessReference;
import com.moesegfault.banking.domain.business.BusinessTransactionId;
import java.util.Objects;

/**
 * @brief 查询单笔业务流水请求（Find Business Transaction Query），支持按交易 ID 或参考号定位；
 *        Find-business-transaction query supporting lookup by transaction ID or reference number.
 */
public final class FindBusinessTransactionQuery {

    /**
     * @brief 交易 ID（可空）（Transaction ID, Nullable）；
     *        Transaction ID, nullable.
     */
    private final String transactionId;

    /**
     * @brief 业务参考号（可空）（Business Reference, Nullable）；
     *        Business reference number, nullable.
     */
    private final String referenceNo;

    /**
     * @brief 构造查询请求（Construct Query）；
     *        Construct query.
     *
     * @param transactionId 交易 ID（Transaction ID, nullable）。
     * @param referenceNo 业务参考号（Business reference number, nullable）。
     */
    public FindBusinessTransactionQuery(final String transactionId, final String referenceNo) {
        this.transactionId = normalizeNullableText(transactionId);
        this.referenceNo = normalizeNullableText(referenceNo);
        validateSelector(this.transactionId, this.referenceNo);
    }

    /**
     * @brief 创建按交易 ID 查询请求（Create Query by Transaction ID）；
     *        Create query by transaction ID.
     *
     * @param transactionId 交易 ID（Transaction ID）。
     * @return 查询请求（Query instance）。
     */
    public static FindBusinessTransactionQuery byTransactionId(final String transactionId) {
        return new FindBusinessTransactionQuery(transactionId, null);
    }

    /**
     * @brief 创建按参考号查询请求（Create Query by Reference Number）；
     *        Create query by reference number.
     *
     * @param referenceNo 业务参考号（Business reference number）。
     * @return 查询请求（Query instance）。
     */
    public static FindBusinessTransactionQuery byReferenceNo(final String referenceNo) {
        return new FindBusinessTransactionQuery(null, referenceNo);
    }

    /**
     * @brief 判断是否按交易 ID 查询（Check Query by Transaction ID）；
     *        Check whether this query selects by transaction ID.
     *
     * @return 按交易 ID 返回 true（true when querying by transaction ID）。
     */
    public boolean hasTransactionId() {
        return transactionId != null;
    }

    /**
     * @brief 返回交易 ID（Return Transaction ID）；
     *        Return transaction ID, nullable.
     *
     * @return 交易 ID 或 null（Transaction ID or null）。
     */
    public String transactionIdOrNull() {
        return transactionId;
    }

    /**
     * @brief 返回参考号（Return Reference Number）；
     *        Return reference number, nullable.
     *
     * @return 参考号或 null（Reference number or null）。
     */
    public String referenceNoOrNull() {
        return referenceNo;
    }

    /**
     * @brief 转换为交易 ID 值对象（Map to Transaction ID Value Object）；
     *        Map to transaction ID value object.
     *
     * @return 交易 ID 值对象（Transaction ID value object）。
     */
    public BusinessTransactionId toTransactionId() {
        return BusinessTransactionId.of(Objects.requireNonNull(transactionId, "transactionId must not be null"));
    }

    /**
     * @brief 转换为参考号值对象（Map to Reference Value Object）；
     *        Map to reference value object.
     *
     * @return 参考号值对象（Reference value object）。
     */
    public BusinessReference toReferenceNo() {
        return BusinessReference.of(Objects.requireNonNull(referenceNo, "referenceNo must not be null"));
    }

    /**
     * @brief 校验查询选择器（Validate Query Selector）；
     *        Validate query selector cardinality.
     *
     * @param transactionId 交易 ID（Transaction ID, nullable）。
     * @param referenceNo 参考号（Reference number, nullable）。
     */
    private static void validateSelector(final String transactionId, final String referenceNo) {
        final boolean hasTransactionId = transactionId != null;
        final boolean hasReferenceNo = referenceNo != null;
        if (hasTransactionId == hasReferenceNo) {
            throw new IllegalArgumentException(
                    "Exactly one of transactionId or referenceNo must be provided");
        }
    }

    /**
     * @brief 标准化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text by trimming and collapsing blank to null.
     *
     * @param rawValue 原始值（Raw value）。
     * @return 标准化值或 null（Normalized value or null）。
     */
    private static String normalizeNullableText(final String rawValue) {
        if (rawValue == null) {
            return null;
        }
        final String normalized = rawValue.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}

