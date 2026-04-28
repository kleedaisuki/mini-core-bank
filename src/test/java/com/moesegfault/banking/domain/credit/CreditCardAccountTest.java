package com.moesegfault.banking.domain.credit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DateRange;
import com.moesegfault.banking.domain.shared.Money;
import com.moesegfault.banking.domain.shared.Percentage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * @brief Credit 领域单元测试（Credit Domain Unit Test），覆盖额度、账单、还款与策略规则；
 *        Credit domain unit tests covering limits, statement lifecycle, repayment, and policies.
 */
class CreditCardAccountTest {

    /**
     * @brief 验证开立账户时可用额度等于总额度；
     *        Verify available credit equals total limit when opening account.
     */
    @Test
    void shouldOpenAccountWithFullAvailableCredit() {
        final CreditCardAccount account = CreditCardAccount.open(
                CreditCardAccountId.of("cc-acc-001"),
                usd("10000.0000"),
                BillingCycle.of(10, 28),
                InterestRate.ofPercent(new BigDecimal("1.5")),
                usd("3000.0000"),
                CurrencyCode.of("USD"));

        assertEquals(usd("10000.0000"), account.creditLimit().totalLimit());
        assertEquals(usd("10000.0000"), account.creditLimit().availableLimit());
    }

    /**
     * @brief 验证额度约束：可用额度和预借现金额度都不能超过总额度；
     *        Verify limit constraints: available and cash-advance limits cannot exceed total limit.
     */
    @Test
    void shouldRejectInvalidCreditLimitComposition() {
        assertThrows(
                BusinessRuleViolation.class,
                () -> CreditLimit.of(usd("1000.0000"), usd("1200.0000"), usd("300.0000"))
        );
        assertThrows(
                BusinessRuleViolation.class,
                () -> CreditLimit.of(usd("1000.0000"), usd("900.0000"), usd("1200.0000"))
        );
    }

    /**
     * @brief 验证消费、撤销、还款会更新可用额度；
     *        Verify charge, release, and repayment update available credit.
     */
    @Test
    void shouldUpdateAvailableCreditOnChargeAndRepayment() {
        final CreditCardAccount account = CreditCardAccount.open(
                CreditCardAccountId.of("cc-acc-002"),
                usd("2000.0000"),
                BillingCycle.of(5, 25),
                InterestRate.ofPercent(new BigDecimal("1.2")),
                usd("500.0000"),
                CurrencyCode.of("USD"));

        account.authorizeCharge(usd("600.0000"));
        account.releaseCredit(usd("100.0000"));
        account.receiveRepayment(usd("200.0000"));

        assertEquals(usd("1700.0000"), account.creditLimit().availableLimit());
    }

    /**
     * @brief 验证账单还款后会从 OPEN 变为 PAID；
     *        Verify statement transitions from OPEN to PAID after full repayment.
     */
    @Test
    void shouldTransitionStatementStatusToPaidAfterRepayment() {
        final CreditCardStatement statement = CreditCardStatement.generate(
                StatementId.of("st-001"),
                CreditCardAccountId.of("cc-acc-003"),
                DateRange.of(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31)),
                LocalDate.of(2026, 3, 31),
                LocalDate.of(2026, 4, 20),
                usd("1000.0000"),
                usd("100.0000"),
                CurrencyCode.of("USD"));

        final Money remainder = statement.applyRepayment(usd("1200.0000"));

        assertEquals(StatementStatus.PAID, statement.statementStatus());
        assertEquals(usd("1000.0000"), statement.paidAmount());
        assertEquals(usd("0.0000"), statement.outstandingAmount());
        assertEquals(usd("200.0000"), remainder);
    }

    /**
     * @brief 验证到期后且仍未还清时会变为 OVERDUE；
     *        Verify statement becomes OVERDUE when past due date with outstanding amount.
     */
    @Test
    void shouldMarkStatementOverdueWhenPastDueDate() {
        final CreditCardStatement statement = CreditCardStatement.generate(
                StatementId.of("st-002"),
                CreditCardAccountId.of("cc-acc-004"),
                DateRange.of(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28)),
                LocalDate.of(2026, 2, 28),
                LocalDate.of(2026, 3, 10),
                usd("300.0000"),
                usd("30.0000"),
                CurrencyCode.of("USD"));

        statement.markOverdue(LocalDate.of(2026, 3, 11));

        assertEquals(StatementStatus.OVERDUE, statement.statementStatus());
    }

    /**
     * @brief 验证还款分配策略按到期顺序分摊到账单；
     *        Verify repayment-allocation policy applies payment in due-date order.
     */
    @Test
    void shouldAllocateRepaymentByDueDateOrder() {
        final CreditCardStatement first = CreditCardStatement.generate(
                StatementId.of("st-101"),
                CreditCardAccountId.of("cc-acc-005"),
                DateRange.of(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31)),
                LocalDate.of(2026, 1, 31),
                LocalDate.of(2026, 2, 10),
                usd("300.0000"),
                usd("30.0000"),
                CurrencyCode.of("USD"));
        final CreditCardStatement second = CreditCardStatement.generate(
                StatementId.of("st-102"),
                CreditCardAccountId.of("cc-acc-005"),
                DateRange.of(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28)),
                LocalDate.of(2026, 2, 28),
                LocalDate.of(2026, 3, 10),
                usd("400.0000"),
                usd("40.0000"),
                CurrencyCode.of("USD"));

        final Money remainder = RepaymentAllocationPolicy.allocateToStatements(
                usd("500.0000"),
                List.of(second, first));

        assertEquals(usd("0.0000"), remainder);
        assertEquals(StatementStatus.PAID, first.statementStatus());
        assertEquals(usd("300.0000"), first.paidAmount());
        assertEquals(StatementStatus.OPEN, second.statementStatus());
        assertEquals(usd("200.0000"), second.paidAmount());
    }

    /**
     * @brief 验证最低还款策略支持比例与保底金额并上限为应还总额；
     *        Verify minimum-payment policy supports rate and floor, capped by total due.
     */
    @Test
    void shouldCalculateMinimumPaymentByRateAndFloor() {
        final Money minimumA = MinimumPaymentPolicy.calculate(
                usd("80.0000"),
                Percentage.ofPercent(new BigDecimal("10.0")),
                usd("20.0000"));
        final Money minimumB = MinimumPaymentPolicy.calculate(
                usd("15.0000"),
                Percentage.ofPercent(new BigDecimal("10.0")),
                usd("20.0000"));

        assertEquals(usd("20.0000"), minimumA);
        assertEquals(usd("15.0000"), minimumB);
        assertTrue(minimumB.compareTo(usd("15.0000")) <= 0);
    }

    /**
     * @brief 创建 USD 金额（Create USD Money Helper）；
     *        Create USD money helper.
     *
     * @param amount 金额字符串（Amount string）。
     * @return USD 金额对象（USD money object）。
     */
    private static Money usd(final String amount) {
        return Money.of(CurrencyCode.of("USD"), new BigDecimal(amount));
    }
}
