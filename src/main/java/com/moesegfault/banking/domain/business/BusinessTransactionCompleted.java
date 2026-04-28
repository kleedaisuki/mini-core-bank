package com.moesegfault.banking.domain.business;

import com.moesegfault.banking.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 业务交易已完成事件（Business Transaction Completed Event）；
 *        Business-transaction-completed domain event.
 */
public final class BusinessTransactionCompleted implements DomainEvent {

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
     * @brief 业务参考号（Business Reference）；
     *        Business reference.
     */
    private final BusinessReference referenceNo;

    /**
     * @brief 事件发生时间（Occurred Timestamp）；
     *        Event occurred timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造交易完成事件（Construct Transaction Completed Event）；
     *        Construct transaction completed event.
     *
     * @param transactionId    交易 ID（Transaction ID）。
     * @param businessTypeCode 业务类型码（Business type code）。
     * @param referenceNo      业务参考号（Business reference）。
     * @param occurredAt       事件时间（Occurred timestamp）。
     */
    public BusinessTransactionCompleted(
            final BusinessTransactionId transactionId,
            final BusinessTypeCode businessTypeCode,
            final BusinessReference referenceNo,
            final Instant occurredAt
    ) {
        this.transactionId = Objects.requireNonNull(transactionId, "Transaction ID must not be null");
        this.businessTypeCode = Objects.requireNonNull(businessTypeCode, "Business type code must not be null");
        this.referenceNo = Objects.requireNonNull(referenceNo, "Business reference must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
    }

    /**
     * @brief 返回交易 ID（Return Transaction ID）；
     *        Return transaction identifier.
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
     * @brief 返回业务参考号（Return Business Reference）；
     *        Return business reference.
     *
     * @return 业务参考号（Business reference）。
     */
    public BusinessReference referenceNo() {
        return referenceNo;
    }

    /**
     * @brief 返回事件发生时间（Return Occurred Timestamp）；
     *        Return occurred timestamp.
     *
     * @return 事件时间（Occurred timestamp）。
     */
    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
