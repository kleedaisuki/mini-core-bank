package com.moesegfault.banking.infrastructure.id;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * @brief UuidIdGenerator 单元测试（Unit Test），验证 UUID 生成行为；
 * Unit tests for UUID generation behavior.
 */
class UuidIdGeneratorTest {

    /**
     * @brief 验证生成值非空、可解析且具备随机性；
     * Verify generated values are non-null, parseable, and non-repeating.
     */
    @Test
    void shouldGenerateParseableAndDifferentIds() {
        final IdGenerator generator = new UuidIdGenerator();

        final String firstId = generator.nextId();
        final String secondId = generator.nextId();

        assertNotNull(firstId);
        assertNotNull(secondId);
        assertNotEquals(firstId, secondId);
        assertDoesNotThrow(() -> UUID.fromString(firstId));
        assertDoesNotThrow(() -> UUID.fromString(secondId));
    }
}
