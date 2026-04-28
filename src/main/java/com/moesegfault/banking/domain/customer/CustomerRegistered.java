package com.moesegfault.banking.domain.customer;

import com.moesegfault.banking.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 客户注册领域事件（Customer Registered Domain Event），表示客户完成注册；
 *        Customer registered domain event indicating customer registration completion.
 */
public final class CustomerRegistered implements DomainEvent {

    /**
     * @brief 客户 ID（Customer Identifier）；
     *        Registered customer identifier.
     */
    private final CustomerId customerId;

    /**
     * @brief 身份证件（Identity Document）；
     *        Registered identity document.
     */
    private final IdentityDocument identityDocument;

    /**
     * @brief 事件发生时间（Occurred Timestamp）；
     *        Event occurrence timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造客户注册事件（Construct Customer Registered Event）；
     *        Construct customer registered event.
     *
     * @param customerId       客户 ID（Customer ID）。
     * @param identityDocument 身份证件（Identity document）。
     * @param occurredAt       发生时间（Occurred timestamp）。
     */
    public CustomerRegistered(
            final CustomerId customerId,
            final IdentityDocument identityDocument,
            final Instant occurredAt
    ) {
        this.customerId = Objects.requireNonNull(customerId, "Customer ID must not be null");
        this.identityDocument = Objects.requireNonNull(identityDocument, "Identity document must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred time must not be null");
    }

    /**
     * @brief 返回客户 ID（Return Customer ID）；
     *        Return customer ID.
     *
     * @return 客户 ID（Customer ID）。
     */
    public CustomerId customerId() {
        return customerId;
    }

    /**
     * @brief 返回身份证件（Return Identity Document）；
     *        Return identity document.
     *
     * @return 身份证件（Identity document）。
     */
    public IdentityDocument identityDocument() {
        return identityDocument;
    }

    /**
     * @brief 返回事件时间（Return Occurred Timestamp）；
     *        Return event occurrence timestamp.
     *
     * @return 事件时间（Occurred timestamp）。
     */
    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
