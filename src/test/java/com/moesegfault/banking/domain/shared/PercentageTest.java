package com.moesegfault.banking.domain.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * @brief Percentage 单元测试（Unit Test），验证比例表示与金额应用规则；
 * Unit tests for percentage representation and money application behavior.
 */
class PercentageTest {

    /**
     * @brief 验证百分数输入会正确转换为小数形式；
     * Verify percent input is converted to decimal fraction correctly.
     */
    @Test
    void shouldConvertPercentToDecimal() {
        final Percentage percentage = Percentage.ofPercent(new BigDecimal("3.5"));

        assertEquals(new BigDecimal("0.035000"), percentage.decimalValue());
    }

    /**
     * @brief 验证比例可用于金额计算并保留金额精度；
     * Verify percentage can be applied to money while preserving money scale.
     */
    @Test
    void shouldApplyPercentageToMoney() {
        final Percentage percentage = Percentage.ofPercent(new BigDecimal("12.5"));
        final Money principal = Money.of(CurrencyCode.of("USD"), new BigDecimal("80.0000"));

        final Money result = percentage.applyTo(principal);

        assertEquals(new BigDecimal("10.0000"), result.amount());
        assertEquals(CurrencyCode.of("USD"), result.currencyCode());
    }
}
