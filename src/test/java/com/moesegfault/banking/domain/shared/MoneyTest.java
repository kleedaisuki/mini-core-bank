package com.moesegfault.banking.domain.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * @brief Money 单元测试（Unit Test），验证金额精度与运算规则；
 * Unit tests for money precision constraints and arithmetic rules.
 */
class MoneyTest {

    /**
     * @brief 验证金额创建会按 4 位小数标准化；
     * Verify money creation is normalized to scale 4.
     */
    @Test
    void shouldNormalizeAmountToScaleFour() {
        final Money money = Money.of(CurrencyCode.of("USD"), new BigDecimal("123.4500"));

        assertEquals(4, money.amount().scale());
        assertEquals(new BigDecimal("123.4500"), money.amount());
    }

    /**
     * @brief 验证超出 scale 会抛出异常；
     * Verify scale overflow raises exception.
     */
    @Test
    void shouldRejectAmountWithMoreThanFourFractionDigits() {
        assertThrows(
                ArithmeticException.class,
                () -> Money.of(CurrencyCode.of("USD"), new BigDecimal("1.00001"))
        );
    }

    /**
     * @brief 验证超出 NUMERIC(19,4) 精度会抛出异常；
     * Verify precision overflow against NUMERIC(19,4) raises exception.
     */
    @Test
    void shouldRejectAmountPrecisionOverflow() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Money.of(CurrencyCode.of("USD"), new BigDecimal("1234567890123456.0000"))
        );
    }

    /**
     * @brief 验证同币种可做加减运算；
     * Verify add/subtract operations for same currency.
     */
    @Test
    void shouldAddAndSubtractForSameCurrency() {
        final Money base = Money.of(CurrencyCode.of("USD"), new BigDecimal("100.0000"));
        final Money delta = Money.of(CurrencyCode.of("USD"), new BigDecimal("20.1250"));

        assertEquals(new BigDecimal("120.1250"), base.add(delta).amount());
        assertEquals(new BigDecimal("79.8750"), base.subtract(delta).amount());
    }

    /**
     * @brief 验证跨币种运算会抛出业务规则异常；
     * Verify cross-currency arithmetic raises business-rule violation.
     */
    @Test
    void shouldRejectCrossCurrencyOperation() {
        final Money usd = Money.of(CurrencyCode.of("USD"), new BigDecimal("1.0000"));
        final Money eur = Money.of(CurrencyCode.of("EUR"), new BigDecimal("1.0000"));

        final BusinessRuleViolation exception = assertThrows(BusinessRuleViolation.class, () -> usd.add(eur));
        assertTrue(exception.getMessage().contains("currency mismatch"));
    }
}
