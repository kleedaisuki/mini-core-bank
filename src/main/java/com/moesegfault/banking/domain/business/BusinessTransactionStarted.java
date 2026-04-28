package com.moesegfault.banking.domain.business;

import com.moesegfault.banking.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 业务交易已开始事件（Business Transaction Started Event）；
 *        Business-transaction-started domain event.
 */
public final class BusinessTransactionStarted implements DomainEvent {

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
     * @brief 交易渠道（Transaction Channel）；
     *        Transaction channel.
     */
    private final BusinessChannel channel;

    /**
     * @brief 事件发生时间（Occurred Timestamp）；
     *        Event occurred timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造交易开始事件（Construct Transaction Started Event）；
     *        Construct transaction started event.
     *
     * @param transactionId    交易 ID（Transaction ID）。
     * @param businessTypeCode 业务类型码（Business type code）。
     * @param referenceNo      业务参考号（Business reference）。
     * @param channel          交易渠道（Transaction channel）。
     * @param occurredAt       事件时间（Occurred timestamp）。
     */
    public BusinessTransactionStarted(
            final BusinessTransactionId transactionId,
            final BusinessTypeCode businessTypeCode,
            final BusinessReference referenceNo,
            final BusinessChannel channel,
            final Instant occurredAt
    ) {
        this.transactionId = Objects.requireNonNull(transactionId, "Transaction ID must not be null");
        this.businessTypeCode = Objects.requireNonNull(businessTypeCode, "Business type code must not be null");
        this.referenceNo = Objects.requireNonNull(referenceNo, "Business reference must not be null");
        this.channel = Objects.requireNonNull(channel, "Business channel must not be null");
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
     * @brief 返回交易渠道（Return Business Channel）；
     *        Return business channel.
     *
     * @return 交易渠道（Business channel）。
     */
    public BusinessChannel channel() {
        return channel;
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
