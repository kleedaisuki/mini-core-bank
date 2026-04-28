package com.moesegfault.banking.domain.business;

import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 业务交易实体（Business Transaction Entity），映射 `business_transaction` 并管理状态流转不变量；
 *        Business transaction entity mapped to `business_transaction` and enforcing lifecycle invariants.
 */
public final class BusinessTransaction {

    /**
     * @brief 业务交易 ID（Business Transaction ID）；
     *        Business transaction identifier.
     */
    private final BusinessTransactionId transactionId;

    /**
     * @brief 业务类型码（Business Type Code）；
     *        Business type code.
     */
    private final BusinessTypeCode businessTypeCode;

    /**
     * @brief 发起客户 ID（可空）（Initiator Customer ID, Nullable）；
     *        Initiator customer identifier, nullable.
     */
    private final CustomerId initiatorCustomerId;

    /**
     * @brief 操作员 ID（可空）（Operator ID, Nullable）；
     *        Operator identifier, nullable.
     */
    private final String operatorId;

    /**
     * @brief 交易渠道（Transaction Channel）；
     *        Transaction channel.
     */
    private final BusinessChannel channel;

    /**
     * @brief 交易状态（Transaction Status）；
     *        Transaction status.
     */
    private BusinessTransactionStatus transactionStatus;

    /**
     * @brief 发起时间（Requested Timestamp）；
     *        Requested timestamp.
     */
    private final Instant requestedAt;

    /**
     * @brief 完成时间（可空）（Completed Timestamp, Nullable）；
     *        Completed timestamp, nullable.
     */
    private Instant completedAt;

    /**
     * @brief 业务参考号（Business Reference）；
     *        Business reference number.
     */
    private final BusinessReference referenceNo;

    /**
     * @brief 备注（可空）（Remarks, Nullable）；
     *        Remarks, nullable.
     */
    private String remarks;

    /**
     * @brief 构造业务交易实体（Construct Business Transaction Entity）；
     *        Construct business transaction entity.
     *
     * @param transactionId       业务交易 ID（Transaction ID）。
     * @param businessTypeCode    业务类型码（Business type code）。
     * @param initiatorCustomerId 发起客户 ID（Initiator customer ID, nullable）。
     * @param operatorId          操作员 ID（Operator ID, nullable）。
     * @param channel             业务渠道（Business channel）。
     * @param transactionStatus   交易状态（Transaction status）。
     * @param requestedAt         发起时间（Requested timestamp）。
     * @param completedAt         完成时间（Completed timestamp, nullable）。
     * @param referenceNo         业务参考号（Business reference number）。
     * @param remarks             备注（Remarks, nullable）。
     */
    private BusinessTransaction(
            final BusinessTransactionId transactionId,
            final BusinessTypeCode businessTypeCode,
            final CustomerId initiatorCustomerId,
            final String operatorId,
            final BusinessChannel channel,
            final BusinessTransactionStatus transactionStatus,
            final Instant requestedAt,
            final Instant completedAt,
            final BusinessReference referenceNo,
            final String remarks
    ) {
        this.transactionId = Objects.requireNonNull(transactionId, "Transaction ID must not be null");
        this.businessTypeCode = Objects.requireNonNull(businessTypeCode, "Business type code must not be null");
        this.initiatorCustomerId = initiatorCustomerId;
        this.operatorId = normalizeOperatorId(operatorId);
        this.channel = Objects.requireNonNull(channel, "Business channel must not be null");
        this.transactionStatus = Objects.requireNonNull(transactionStatus, "Transaction status must not be null");
        this.requestedAt = Objects.requireNonNull(requestedAt, "Requested-at must not be null");
        this.completedAt = completedAt;
        this.referenceNo = Objects.requireNonNull(referenceNo, "Business reference must not be null");
        this.remarks = normalizeNullableText(remarks);
        validateTimestampOrder(this.requestedAt, this.completedAt);
        validateStatusCompletionConsistency(this.transactionStatus, this.completedAt);
    }

    /**
     * @brief 发起业务交易（Start Business Transaction）；
     *        Start a business transaction in `PENDING` status.
     *
     * @param transactionId       业务交易 ID（Transaction ID）。
     * @param businessTypeCode    业务类型码（Business type code）。
     * @param initiatorCustomerId 发起客户 ID（Initiator customer ID, nullable）。
     * @param operatorId          操作员 ID（Operator ID, nullable）。
     * @param channel             业务渠道（Business channel）。
     * @param referenceNo         业务参考号（Business reference）。
     * @param remarks             备注（Remarks, nullable）。
     * @return 新建业务交易实体（Newly started business transaction）。
     */
    public static BusinessTransaction start(
            final BusinessTransactionId transactionId,
            final BusinessTypeCode businessTypeCode,
            final CustomerId initiatorCustomerId,
            final String operatorId,
            final BusinessChannel channel,
            final BusinessReference referenceNo,
            final String remarks
    ) {
        return new BusinessTransaction(
                transactionId,
                businessTypeCode,
                initiatorCustomerId,
                operatorId,
                channel,
                BusinessTransactionStatus.PENDING,
                Instant.now(),
                null,
                referenceNo,
                remarks);
    }

    /**
     * @brief 从持久化状态重建业务交易（Restore Business Transaction from Persistence）；
     *        Restore business transaction from persistence state.
     *
     * @param transactionId       业务交易 ID（Transaction ID）。
     * @param businessTypeCode    业务类型码（Business type code）。
     * @param initiatorCustomerId 发起客户 ID（Initiator customer ID, nullable）。
     * @param operatorId          操作员 ID（Operator ID, nullable）。
     * @param channel             业务渠道（Business channel）。
     * @param transactionStatus   交易状态（Transaction status）。
     * @param requestedAt         发起时间（Requested timestamp）。
     * @param completedAt         完成时间（Completed timestamp, nullable）。
     * @param referenceNo         业务参考号（Business reference）。
     * @param remarks             备注（Remarks, nullable）。
     * @return 重建后的业务交易实体（Reconstructed business transaction）。
     */
    public static BusinessTransaction restore(
            final BusinessTransactionId transactionId,
            final BusinessTypeCode businessTypeCode,
            final CustomerId initiatorCustomerId,
            final String operatorId,
            final BusinessChannel channel,
            final BusinessTransactionStatus transactionStatus,
            final Instant requestedAt,
            final Instant completedAt,
            final BusinessReference referenceNo,
            final String remarks
    ) {
        return new BusinessTransaction(
                transactionId,
                businessTypeCode,
                initiatorCustomerId,
                operatorId,
                channel,
                transactionStatus,
                requestedAt,
                completedAt,
                referenceNo,
                remarks);
    }

    /**
     * @brief 标记交易成功（Mark Transaction as Success）；
     *        Mark pending transaction as successful.
     *
     * @param finishedAt 完成时间（Finished timestamp）。
     * @param remarks    完成备注（Completion remarks, nullable）。
     */
    public void completeSuccess(final Instant finishedAt, final String remarks) {
        BusinessTransactionPolicy.ensureCanComplete(this);
        this.transactionStatus = BusinessTransactionStatus.SUCCESS;
        this.completedAt = normalizeCompletionTime(finishedAt);
        this.remarks = normalizeNullableText(remarks);
    }

    /**
     * @brief 标记交易失败（Mark Transaction as Failed）；
     *        Mark pending transaction as failed.
     *
     * @param finishedAt     完成时间（Finished timestamp）。
     * @param failureRemarks 失败原因备注（Failure remarks）。
     */
    public void fail(final Instant finishedAt, final String failureRemarks) {
        BusinessTransactionPolicy.ensureCanFail(this);
        final String normalizedFailure = normalizeRequiredText(failureRemarks, "Failure remarks must not be blank");
        this.transactionStatus = BusinessTransactionStatus.FAILED;
        this.completedAt = normalizeCompletionTime(finishedAt);
        this.remarks = normalizedFailure;
    }

    /**
     * @brief 冲正交易（Reverse Transaction）；
     *        Reverse a successful transaction if type is reversible.
     *
     * @param businessType    交易对应业务类型（Business type of this transaction）。
     * @param reversedAt      冲正时间（Reversed timestamp）。
     * @param reverseRemarks  冲正备注（Reverse remarks, nullable）。
     */
    public void reverse(
            final BusinessType businessType,
            final Instant reversedAt,
            final String reverseRemarks
    ) {
        BusinessTransactionPolicy.ensureCanReverse(this, businessType);
        this.transactionStatus = BusinessTransactionStatus.REVERSED;
        this.completedAt = normalizeCompletionTime(reversedAt);
        this.remarks = normalizeNullableText(reverseRemarks);
    }

    /**
     * @brief 构建“交易已开始”事件（Build Transaction Started Event）；
     *        Build business-transaction-started event.
     *
     * @return 交易开始事件（Transaction started event）。
     */
    public BusinessTransactionStarted startedEvent() {
        return new BusinessTransactionStarted(
                transactionId,
                businessTypeCode,
                referenceNo,
                channel,
                requestedAt);
    }

    /**
     * @brief 构建“交易已完成”事件（Build Transaction Completed Event）；
     *        Build business-transaction-completed event.
     *
     * @return 交易完成事件（Transaction completed event）。
     */
    public BusinessTransactionCompleted completedEvent() {
        if (transactionStatus != BusinessTransactionStatus.SUCCESS) {
            throw new BusinessRuleViolation("Only SUCCESS transaction can emit completed event");
        }
        return new BusinessTransactionCompleted(
                transactionId,
                businessTypeCode,
                referenceNo,
                completedAtOrThrow());
    }

    /**
     * @brief 构建“交易失败”事件（Build Transaction Failed Event）；
     *        Build business-transaction-failed event.
     *
     * @return 交易失败事件（Transaction failed event）。
     */
    public BusinessTransactionFailed failedEvent() {
        if (transactionStatus != BusinessTransactionStatus.FAILED) {
            throw new BusinessRuleViolation("Only FAILED transaction can emit failed event");
        }
        return new BusinessTransactionFailed(
                transactionId,
                businessTypeCode,
                referenceNo,
                normalizeRequiredText(remarks, "Failure remarks must exist for failed event"),
                completedAtOrThrow());
    }

    /**
     * @brief 返回交易 ID（Return Transaction ID）；
     *        Return business transaction identifier.
     *
     * @return 交易 ID（Transaction ID）。
     */
    public BusinessTransactionId transactionId() {
        return transactionId;
    }

    /**
     * @brief 返回业务类型码（Return Business Type Code）；
     *        Return business type code.
     *
     * @return 业务类型码（Business type code）。
     */
    public BusinessTypeCode businessTypeCode() {
        return businessTypeCode;
    }

    /**
     * @brief 返回发起客户 ID（可空）（Return Optional Initiator Customer ID）；
     *        Return initiator customer ID, nullable.
     *
     * @return 发起客户 ID 或 null（Initiator customer ID or null）。
     */
    public CustomerId initiatorCustomerIdOrNull() {
        return initiatorCustomerId;
    }

    /**
     * @brief 返回操作员 ID（可空）（Return Optional Operator ID）；
     *        Return operator ID, nullable.
     *
     * @return 操作员 ID 或 null（Operator ID or null）。
     */
    public String operatorIdOrNull() {
        return operatorId;
    }

    /**
     * @brief 返回渠道（Return Business Channel）；
     *        Return business channel.
     *
     * @return 业务渠道（Business channel）。
     */
    public BusinessChannel channel() {
        return channel;
    }

    /**
     * @brief 返回交易状态（Return Transaction Status）；
     *        Return transaction status.
     *
     * @return 交易状态（Transaction status）。
     */
    public BusinessTransactionStatus transactionStatus() {
        return transactionStatus;
    }

    /**
     * @brief 返回发起时间（Return Requested Timestamp）；
     *        Return requested timestamp.
     *
     * @return 发起时间（Requested timestamp）。
     */
    public Instant requestedAt() {
        return requestedAt;
    }

    /**
     * @brief 返回完成时间（可空）（Return Optional Completed Timestamp）；
     *        Return completed timestamp, nullable.
     *
     * @return 完成时间或 null（Completed timestamp or null）。
     */
    public Instant completedAtOrNull() {
        return completedAt;
    }

    /**
     * @brief 返回业务参考号（Return Business Reference）；
     *        Return business reference.
     *
     * @return 业务参考号（Business reference）。
     */
    public BusinessReference referenceNo() {
        return referenceNo;
    }

    /**
     * @brief 返回备注（可空）（Return Optional Remarks）；
     *        Return remarks, nullable.
     *
     * @return 备注或 null（Remarks or null）。
     */
    public String remarksOrNull() {
        return remarks;
    }

    /**
     * @brief 判断是否处于待处理状态（Check Pending Status）；
     *        Check whether transaction is pending.
     *
     * @return `PENDING` 返回 true（true when `PENDING`）。
     */
    public boolean isPending() {
        return transactionStatus == BusinessTransactionStatus.PENDING;
    }

    /**
     * @brief 标准化完成时间（Normalize Completion Timestamp）；
     *        Normalize and validate completion timestamp.
     *
     * @param rawCompletedAt 原始完成时间（Raw completion timestamp）。
     * @return 标准化完成时间（Normalized completion timestamp）。
     */
    private Instant normalizeCompletionTime(final Instant rawCompletedAt) {
        final Instant normalized = Objects.requireNonNull(rawCompletedAt, "Completion time must not be null");
        if (normalized.isBefore(requestedAt)) {
            throw new BusinessRuleViolation("Completed-at must not be before requested-at");
        }
        return normalized;
    }

    /**
     * @brief 返回已存在的完成时间（Return Existing Completed Timestamp）；
     *        Return existing completed timestamp.
     *
     * @return 完成时间（Completed timestamp）。
     */
    private Instant completedAtOrThrow() {
        if (completedAt == null) {
            throw new BusinessRuleViolation("Completed-at must exist for terminal transaction");
        }
        return completedAt;
    }

    /**
     * @brief 校验时间顺序约束（Validate Timestamp Ordering）；
     *        Validate ordering constraint between requested and completed timestamps.
     *
     * @param requestedAt 发起时间（Requested timestamp）。
     * @param completedAt 完成时间（Completed timestamp, nullable）。
     */
    private static void validateTimestampOrder(final Instant requestedAt, final Instant completedAt) {
        if (completedAt != null && completedAt.isBefore(requestedAt)) {
            throw new BusinessRuleViolation("Completed-at must not be before requested-at");
        }
    }

    /**
     * @brief 校验状态与完成时间一致性（Validate Status/Completion Consistency）；
     *        Validate consistency between status and completion timestamp.
     *
     * @param status      交易状态（Transaction status）。
     * @param completedAt 完成时间（Completed timestamp, nullable）。
     */
    private static void validateStatusCompletionConsistency(
            final BusinessTransactionStatus status,
            final Instant completedAt
    ) {
        final boolean terminal = status.isTerminal();
        if (terminal && completedAt == null) {
            throw new BusinessRuleViolation("Terminal transaction must have completed-at");
        }
        if (!terminal && completedAt != null) {
            throw new BusinessRuleViolation("Pending transaction must not have completed-at");
        }
    }

    /**
     * @brief 标准化操作员 ID（Normalize Operator ID）；
     *        Normalize operator ID.
     *
     * @param rawOperatorId 原始操作员 ID（Raw operator ID）。
     * @return 标准化操作员 ID 或 null（Normalized operator ID or null）。
     */
    private static String normalizeOperatorId(final String rawOperatorId) {
        final String normalized = normalizeNullableText(rawOperatorId);
        if (normalized != null && normalized.length() > 64) {
            throw new IllegalArgumentException("Operator ID length must be <= 64");
        }
        return normalized;
    }

    /**
     * @brief 标准化必填文本（Normalize Required Text）；
     *        Normalize required text and reject blank value.
     *
     * @param rawValue       原始文本（Raw text value）。
     * @param errorIfMissing 缺失时错误信息（Error message when missing）。
     * @return 标准化后的非空文本（Normalized non-blank text）。
     */
    private static String normalizeRequiredText(final String rawValue, final String errorIfMissing) {
        final String normalized = normalizeNullableText(rawValue);
        if (normalized == null) {
            throw new BusinessRuleViolation(errorIfMissing);
        }
        return normalized;
    }

    /**
     * @brief 标准化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text by trimming and collapsing blank to null.
     *
     * @param rawValue 原始文本（Raw text value）。
     * @return 标准化文本或 null（Normalized text or null）。
     */
    private static String normalizeNullableText(final String rawValue) {
        if (rawValue == null) {
            return null;
        }
        final String normalized = rawValue.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
