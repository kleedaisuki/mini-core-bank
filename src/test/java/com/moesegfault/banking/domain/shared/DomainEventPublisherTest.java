package com.moesegfault.banking.domain.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * @brief DomainEventPublisher 单元测试（Unit Test），验证批量发布与空实现行为；
 * Unit tests for DomainEventPublisher batch publish and no-op behavior.
 */
class DomainEventPublisherTest {

    /**
     * @brief 验证 publishAll 会逐个转发事件；
     * Verify publishAll forwards events one by one.
     */
    @Test
    void shouldPublishAllEventsOneByOne() {
        final AtomicInteger publishedCount = new AtomicInteger(0);
        final DomainEventPublisher publisher = event -> publishedCount.incrementAndGet();

        publisher.publishAll(List.of(new TestDomainEvent(), new TestDomainEvent(), new TestDomainEvent()));

        assertEquals(3, publishedCount.get());
    }

    /**
     * @brief 验证 noop 发布器可安全调用；
     * Verify no-op publisher can be safely invoked.
     */
    @Test
    void shouldSupportNoopPublisher() {
        final DomainEventPublisher noop = DomainEventPublisher.noop();
        noop.publish(new TestDomainEvent());
        noop.publishAll(List.of(new TestDomainEvent()));
    }

    /**
     * @brief 测试用领域事件（Test Domain Event）；
     * Test domain event implementation.
     */
    private static final class TestDomainEvent implements DomainEvent {

        /**
         * @brief 返回事件时间（Return Event Timestamp）；
         * Return event timestamp.
         *
         * @return 当前时间戳（Current timestamp）。
         */
        @Override
        public Instant occurredAt() {
            return Instant.now();
        }
    }
}
