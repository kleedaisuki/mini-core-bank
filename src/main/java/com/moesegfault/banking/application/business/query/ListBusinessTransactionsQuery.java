package com.moesegfault.banking.application.business.query;

import com.moesegfault.banking.domain.business.BusinessTransactionStatus;
import com.moesegfault.banking.domain.business.CustomerId;

/**
 * @brief 业务流水列表查询请求（List Business Transactions Query），支持客户/状态筛选与返回条数控制；
 *        List-business-transactions query supporting filters by customer/status and result-size cap.
 */
public final class ListBusinessTransactionsQuery {

    /**
     * @brief 默认返回上限（Default Result Limit）；
     *        Default limit of returned rows.
     */
    public static final int DEFAULT_LIMIT = 100;

    /**
     * @brief 发起客户 ID（可空）（Initiator Customer ID, Nullable）；
     *        Initiator customer ID, nullable.
     */
    private final String initiatorCustomerId;

    /**
     * @brief 交易状态（可空）（Transaction Status, Nullable）；
     *        Transaction status, nullable.
     */
    private final BusinessTransactionStatus transactionStatus;

    /**
     * @brief 返回上限（Result Limit）；
     *        Result limit.
     */
    private final int limit;

    /**
     * @brief 创建默认查询（Create Default Query）；
     *        Create default query with no filters.
     */
    public ListBusinessTransactionsQuery() {
        this(null, null, DEFAULT_LIMIT);
    }

    /**
     * @brief 构造业务流水列表查询（Construct List Query）；
     *        Construct list-business-transactions query.
     *
     * @param initiatorCustomerId 发起客户 ID（Initiator customer ID, nullable）。
     * @param transactionStatus 交易状态（Transaction status, nullable）。
     * @param limit 返回上限（Result limit, > 0）。
     */
    public ListBusinessTransactionsQuery(
            final String initiatorCustomerId,
            final BusinessTransactionStatus transactionStatus,
            final int limit
    ) {
        this.initiatorCustomerId = normalizeNullableText(initiatorCustomerId);
        this.transactionStatus = transactionStatus;
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be > 0");
        }
        this.limit = limit;
    }

    /**
     * @brief 判断是否指定了客户过滤（Check Customer Filter）；
     *        Check whether initiator-customer filter is present.
     *
     * @return 指定过滤返回 true（true when filter exists）。
     */
    public boolean hasInitiatorCustomerId() {
        return initiatorCustomerId != null;
    }

    /**
     * @brief 判断是否指定了状态过滤（Check Status Filter）；
     *        Check whether transaction-status filter is present.
     *
     * @return 指定过滤返回 true（true when filter exists）。
     */
    public boolean hasTransactionStatus() {
        return transactionStatus != null;
    }

    /**
     * @brief 返回发起客户 ID（Return Initiator Customer ID）；
     *        Return initiator customer ID, nullable.
     *
     * @return 发起客户 ID 或 null（Initiator customer ID or null）。
     */
    public String initiatorCustomerIdOrNull() {
        return initiatorCustomerId;
    }

    /**
     * @brief 返回交易状态（Return Transaction Status）；
     *        Return transaction status, nullable.
     *
     * @return 交易状态或 null（Transaction status or null）。
     */
    public BusinessTransactionStatus transactionStatusOrNull() {
        return transactionStatus;
    }

    /**
     * @brief 返回结果上限（Return Result Limit）；
     *        Return result limit.
     *
     * @return 返回上限（Result limit）。
     */
    public int limit() {
        return limit;
    }

    /**
     * @brief 转换为客户 ID 值对象（Map to Customer ID Value Object）；
     *        Map to customer ID value object, nullable.
     *
     * @return 客户 ID 值对象或 null（Customer ID value object or null）。
     */
    public CustomerId toInitiatorCustomerIdOrNull() {
        if (initiatorCustomerId == null) {
            return null;
        }
        return CustomerId.of(initiatorCustomerId);
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

