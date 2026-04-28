package com.moesegfault.banking.domain.shared;

import java.time.Instant;

/**
 * @brief 领域事件接口（Domain Event Interface），承载“事实已发生”的语义；
 *        Domain event interface carrying the semantic that a fact has happened.
 */
public interface DomainEvent {

    /**
     * @brief 事件发生时间（Event Occurred Time）；
     *        Event occurred timestamp.
     *
     * @return 事件发生时间戳（Event occurred timestamp）。
     */
    Instant occurredAt();

    /**
     * @brief 事件名称（Event Name）；
     *        Event name for routing and observability.
     *
     * @return 事件名称（Event name）。
     */
    default String eventName() {
        return getClass().getSimpleName();
    }
}
