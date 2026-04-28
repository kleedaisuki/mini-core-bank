package com.moesegfault.banking.domain.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * @brief CurrencyCode 单元测试（Unit Test），验证币种代码规范与预置集合行为；
 * Unit tests for currency code format constraints and seeded set behavior.
 */
class CurrencyCodeTest {

    /**
     * @brief 验证合法输入会标准化为大写并识别预置币种；
     * Verify valid input is normalized to uppercase and recognized as seeded currency.
     */
    @Test
    void shouldNormalizeToUppercaseAndRecognizeSeededCurrency() {
        final CurrencyCode code = CurrencyCode.of(" usd ");

        assertEquals("USD", code.value());
        assertTrue(code.isSeededCurrency());
    }

    /**
     * @brief 验证非法格式会抛出异常；
     * Verify invalid format raises exception.
     */
    @Test
    void shouldRejectInvalidCurrencyCodeFormat() {
        assertThrows(IllegalArgumentException.class, () -> CurrencyCode.of("US"));
        assertThrows(IllegalArgumentException.class, () -> CurrencyCode.of("USDD"));
        assertThrows(IllegalArgumentException.class, () -> CurrencyCode.of("U$D"));
    }

    /**
     * @brief 验证非预置币种不会被标记为预置；
     * Verify non-seeded code is not marked as seeded.
     */
    @Test
    void shouldMarkNonSeededCurrencyAsFalse() {
        final CurrencyCode code = CurrencyCode.of("CHF");

        assertFalse(code.isSeededCurrency());
    }
}
