package com.moesegfault.banking.domain.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * @brief DateRange 单元测试（Unit Test），验证区间边界与交集规则；
 * Unit tests for date range boundary and overlap rules.
 */
class DateRangeTest {

    /**
     * @brief 验证 end 在 start 前时会抛出业务规则异常；
     * Verify a business-rule violation is thrown when end is before start.
     */
    @Test
    void shouldRejectEndBeforeStart() {
        assertThrows(
                BusinessRuleViolation.class,
                () -> DateRange.of(LocalDate.of(2026, 4, 2), LocalDate.of(2026, 4, 1))
        );
    }

    /**
     * @brief 验证区间包含与天数计算逻辑；
     * Verify contains and inclusive-day-length logic.
     */
    @Test
    void shouldSupportContainsAndLength() {
        final DateRange range = DateRange.of(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));

        assertTrue(range.contains(LocalDate.of(2026, 4, 1)));
        assertTrue(range.contains(LocalDate.of(2026, 4, 30)));
        assertFalse(range.contains(LocalDate.of(2026, 5, 1)));
        assertEquals(30L, range.lengthInDaysInclusive());
    }

    /**
     * @brief 验证区间交集判定；
     * Verify range overlap decision.
     */
    @Test
    void shouldDetectOverlap() {
        final DateRange first = DateRange.of(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
        final DateRange second = DateRange.of(LocalDate.of(2026, 1, 31), LocalDate.of(2026, 2, 15));
        final DateRange third = DateRange.of(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28));

        assertTrue(first.overlaps(second));
        assertFalse(first.overlaps(third));
    }
}
