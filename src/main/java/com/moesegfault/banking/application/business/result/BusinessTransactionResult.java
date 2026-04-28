package com.moesegfault.banking.application.business.result;

import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.business.BusinessTransaction;
import com.moesegfault.banking.domain.business.BusinessTransactionStatus;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 业务流水结果视图（Business Transaction Result View），统一 application 对外输出 schema；
 *        Business-transaction result view that standardizes outbound application schema.
 */
public final class BusinessTransactionResult {

    /**
     * @brief 业务交易 ID（Business Transaction ID）；
     *        Business transaction identifier.
     */
    private final String transactionId;

    /**
     * @brief 业务类型码（Business Type Code）；
     *        Business type code.
     */
    private final String businessTypeCode;

    /**
     * @brief 发起客户 ID（可空）（Initiator Customer ID, Nullable）；
     *        Initiator customer ID, nullable.
     */
    private final String initiatorCustomerId;

    /**
     * @brief 操作员 ID（可空）（Operator ID, Nullable）；
     *        Operator ID, nullable.
     */
    private final String operatorId;

    /**
     * @brief 渠道（Channel）；
     *        Business channel.
     */
    private final BusinessChannel channel;

    /**
     * @brief 交易状态（Transaction Status）；
     *        Business transaction status.
     */
    private final BusinessTransactionStatus transactionStatus;

    /**
     * @brief 发起时间（Requested Timestamp）；
     *        Requested timestamp.
     */
    private final Instant requestedAt;

    /**
     * @brief 完成时间（可空）（Completed Timestamp, Nullable）；
     *        Completed timestamp, nullable.
     */
    private final Instant completedAt;

    /**
     * @brief 业务参考号（Business Reference Number）；
     *        Business reference number.
     */
    private final String referenceNo;

    /**
     * @brief 备注（可空）（Remarks, Nullable）；
     *        Remarks, nullable.
     */
    private final String remarks;

    /**
     * @brief 构造业务流水结果（Construct Business Transaction Result）；
     *        Construct business transaction result.
     *
     * @param transactionId       交易 ID（Transaction ID）。
     * @param businessTypeCode    业务类型码（Business type code）。
     * @param initiatorCustomerId 发起客户 ID（Initiator customer ID, nullable）。
     * @param operatorId          操作员 ID（Operator ID, nullable）。
     * @param channel             渠道（Channel）。
     * @param transactionStatus   状态（Transaction status）。
     * @param requestedAt         发起时间（Requested timestamp）。
     * @param completedAt         完成时间（Completed timestamp, nullable）。
     * @param referenceNo         业务参考号（Business reference number）。
     * @param remarks             备注（Remarks, nullable）。
     */
    public BusinessTransactionResult(
            final String transactionId,
            final String businessTypeCode,
            final String initiatorCustomerId,
            final String operatorId,
            final BusinessChannel channel,
            final BusinessTransactionStatus transactionStatus,
            final Instant requestedAt,
            final Instant completedAt,
            final String referenceNo,
            final String remarks
    ) {
        this.transactionId = Objects.requireNonNull(transactionId, "transactionId must not be null");
        this.businessTypeCode = Objects.requireNonNull(businessTypeCode, "businessTypeCode must not be null");
        this.initiatorCustomerId = initiatorCustomerId;
        this.operatorId = operatorId;
        this.channel = Objects.requireNonNull(channel, "channel must not be null");
        this.transactionStatus = Objects.requireNonNull(transactionStatus, "transactionStatus must not be null");
        this.requestedAt = Objects.requireNonNull(requestedAt, "requestedAt must not be null");
        this.completedAt = completedAt;
        this.referenceNo = Objects.requireNonNull(referenceNo, "referenceNo must not be null");
        this.remarks = remarks;
    }

    /**
     * @brief 从领域实体映射结果（Map from Domain Entity）；
     *        Map from business-transaction domain entity.
     *
     * @param transaction 业务交易实体（Business transaction entity）。
     * @return 业务流水结果（Business transaction result）。
     */
    public static BusinessTransactionResult from(final BusinessTransaction transaction) {
        final BusinessTransaction source = Objects.requireNonNull(transaction, "transaction must not be null");
        return new BusinessTransactionResult(
                source.transactionId().value(),
                source.businessTypeCode().value(),
                source.initiatorCustomerIdOrNull() == null ? null : source.initiatorCustomerIdOrNull().value(),
                source.operatorIdOrNull(),
                source.channel(),
                source.transactionStatus(),
                source.requestedAt(),
                source.completedAtOrNull(),
                source.referenceNo().value(),
                source.remarksOrNull());
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
     * @brief 返回业务类型码（Return Business Type Code）；
     *        Return business type code.
     *
     * @return 业务类型码（Business type code）。
     */
    public String businessTypeCode() {
        return businessTypeCode;
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
     * @brief 返回操作员 ID（Return Operator ID）；
     *        Return operator ID, nullable.
     *
     * @return 操作员 ID 或 null（Operator ID or null）。
     */
    public String operatorIdOrNull() {
        return operatorId;
    }

    /**
     * @brief 返回渠道（Return Channel）；
     *        Return business channel.
     *
     * @return 渠道（Channel）。
     */
    public BusinessChannel channel() {
        return channel;
    }

    /**
     * @brief 返回交易状态（Return Transaction Status）；
     *        Return business transaction status.
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
     * @brief 返回完成时间（Return Completed Timestamp）；
     *        Return completed timestamp, nullable.
     *
     * @return 完成时间或 null（Completed timestamp or null）。
     */
    public Instant completedAtOrNull() {
        return completedAt;
    }

    /**
     * @brief 返回业务参考号（Return Business Reference Number）；
     *        Return business reference number.
     *
     * @return 参考号（Reference number）。
     */
    public String referenceNo() {
        return referenceNo;
    }

    /**
     * @brief 返回备注（Return Remarks）；
     *        Return remarks, nullable.
     *
     * @return 备注或 null（Remarks or null）。
     */
    public String remarksOrNull() {
        return remarks;
    }
}
