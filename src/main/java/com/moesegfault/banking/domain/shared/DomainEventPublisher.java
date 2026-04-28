package com.moesegfault.banking.domain.shared;

import java.util.Collection;
import java.util.Objects;

/**
 * @brief 领域事件发布接口（Domain Event Publisher Interface），定义事件对外传播契约；
 *        Domain event publisher interface defining event propagation contract.
 */
@FunctionalInterface
public interface DomainEventPublisher {

    /**
     * @brief 发布单个领域事件（Publish Single Domain Event）；
     *        Publish a single domain event.
     *
     * @param event 领域事件（Domain event）。
     */
    void publish(DomainEvent event);

    /**
     * @brief 批量发布领域事件（Publish Multiple Domain Events）；
     *        Publish multiple domain events.
     *
     * @param events 领域事件集合（Domain event collection）。
     */
    default void publishAll(final Collection<? extends DomainEvent> events) {
        Objects.requireNonNull(events, "Events must not be null");
        for (DomainEvent event : events) {
            publish(event);
        }
    }

    /**
     * @brief 空实现发布器（No-op Publisher）；
     *        No-op publisher implementation.
     *
     * @return 不执行任何发布动作的发布器（Publisher that performs no action）。
     */
    static DomainEventPublisher noop() {
        return event -> {
            // intentional no-op
        };
    }
}
