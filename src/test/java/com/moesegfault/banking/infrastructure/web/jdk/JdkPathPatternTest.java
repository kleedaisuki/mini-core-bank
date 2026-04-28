package com.moesegfault.banking.infrastructure.web.jdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * @brief JdkPathPattern 单元测试（JdkPathPattern Unit Tests），验证路由模式匹配与参数提取；
 *        Unit tests for JdkPathPattern route matching and path-parameter extraction.
 */
class JdkPathPatternTest {

    /**
     * @brief 验证静态路径可匹配；
     *        Verify static path pattern can be matched.
     */
    @Test
    void shouldMatchStaticPath() {
        final JdkPathPattern pattern = new JdkPathPattern("/health");

        assertTrue(pattern.match("/health").isPresent());
    }

    /**
     * @brief 验证路径参数可提取；
     *        Verify path parameters are extracted when matched.
     */
    @Test
    void shouldExtractPathParameter() {
        final JdkPathPattern pattern = new JdkPathPattern("/customers/{customerId}");

        final Map<String, String> matched = pattern.match("/customers/c-001").orElseThrow();

        assertEquals("c-001", matched.get("customerId"));
    }

    /**
     * @brief 验证段数不一致时不匹配；
     *        Verify mismatch when segment count is different.
     */
    @Test
    void shouldNotMatchWhenSegmentCountIsDifferent() {
        final JdkPathPattern pattern = new JdkPathPattern("/customers/{customerId}");

        assertTrue(pattern.match("/customers").isEmpty());
    }

    /**
     * @brief 验证尾斜杠被规范化处理；
     *        Verify trailing slash normalization behavior.
     */
    @Test
    void shouldTreatTrailingSlashAsSamePath() {
        final JdkPathPattern pattern = new JdkPathPattern("/health/");

        assertTrue(pattern.match("/health").isPresent());
        assertTrue(pattern.match("/health/").isPresent());
    }

    /**
     * @brief 验证非法参数名会抛异常；
     *        Verify invalid parameter name causes exception.
     */
    @Test
    void shouldRejectInvalidParameterName() {
        assertThrows(IllegalArgumentException.class, () -> new JdkPathPattern("/customers/{customer-id}"));
    }
}
