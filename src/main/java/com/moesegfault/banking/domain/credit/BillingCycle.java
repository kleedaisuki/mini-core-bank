package com.moesegfault.banking.domain.credit;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.DateRange;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

/**
 * @brief 账单周期值对象（Billing Cycle Value Object），对齐 `billing_cycle_day` 与 `payment_due_day`；
 *        Billing-cycle value object aligned with `billing_cycle_day` and `payment_due_day`.
 */
public final class BillingCycle {

    /**
     * @brief 账单日（Billing Cycle Day），取值范围 1..31；
     *        Billing-cycle day in range 1..31.
     */
    private final int billingCycleDay;

    /**
     * @brief 到期还款日（Payment Due Day），取值范围 1..31；
     *        Payment-due day in range 1..31.
     */
    private final int paymentDueDay;

    /**
     * @brief 构造账单周期值对象（Construct Billing Cycle Value Object）；
     *        Construct a billing-cycle value object.
     *
     * @param billingCycleDay 账单日（Billing cycle day）。
     * @param paymentDueDay   到期还款日（Payment due day）。
     */
    private BillingCycle(
            final int billingCycleDay,
            final int paymentDueDay
    ) {
        this.billingCycleDay = requireValidDay(billingCycleDay, "Billing cycle day");
        this.paymentDueDay = requireValidDay(paymentDueDay, "Payment due day");
    }

    /**
     * @brief 创建账单周期值对象（Factory Method）；
     *        Create a billing-cycle value object.
     *
     * @param billingCycleDay 账单日（Billing cycle day）。
     * @param paymentDueDay   到期还款日（Payment due day）。
     * @return 账单周期值对象（Billing-cycle value object）。
     */
    public static BillingCycle of(
            final int billingCycleDay,
            final int paymentDueDay
    ) {
        return new BillingCycle(billingCycleDay, paymentDueDay);
    }

    /**
     * @brief 解析指定月份的出账日期（Resolve Statement Date）；
     *        Resolve statement date in a specific month.
     *
     * @param yearMonth 年月（Year-month）。
     * @return 出账日期（Statement date）。
     */
    public LocalDate resolveStatementDate(final YearMonth yearMonth) {
        final YearMonth normalized = Objects.requireNonNull(yearMonth, "Year-month must not be null");
        return normalized.atDay(Math.min(billingCycleDay, normalized.lengthOfMonth()));
    }

    /**
     * @brief 解析给定出账日期的到期还款日（Resolve Payment Due Date）；
     *        Resolve payment-due date from a statement date.
     *
     * @param statementDate 出账日期（Statement date）。
     * @return 到期还款日期（Payment-due date）。
     */
    public LocalDate resolvePaymentDueDate(final LocalDate statementDate) {
        final LocalDate normalized = Objects.requireNonNull(statementDate, "Statement date must not be null");
        final YearMonth dueMonth = YearMonth.from(normalized).plusMonths(1);
        return dueMonth.atDay(Math.min(paymentDueDay, dueMonth.lengthOfMonth()));
    }

    /**
     * @brief 推导账期范围（Derive Statement Period）；
     *        Derive statement period by using previous statement date + 1 day as period start.
     *
     * @param statementDate 出账日期（Statement date）。
     * @return 账期范围（Statement period range）。
     */
    public DateRange deriveStatementPeriod(final LocalDate statementDate) {
        final LocalDate normalized = Objects.requireNonNull(statementDate, "Statement date must not be null");
        final LocalDate previousStatementDate = resolveStatementDate(YearMonth.from(normalized).minusMonths(1));
        return DateRange.of(previousStatementDate.plusDays(1), normalized);
    }

    /**
     * @brief 返回账单日（Return Billing Cycle Day）；
     *        Return billing-cycle day.
     *
     * @return 账单日（Billing cycle day）。
     */
    public int billingCycleDay() {
        return billingCycleDay;
    }

    /**
     * @brief 返回还款日（Return Payment Due Day）；
     *        Return payment-due day.
     *
     * @return 到期还款日（Payment due day）。
     */
    public int paymentDueDay() {
        return paymentDueDay;
    }

    /**
     * @brief 值对象相等判定（Value Object Equality）；
     *        Value-object equality check.
     *
     * @param other 对比对象（Object to compare）。
     * @return 同值返回 true（true when equal）。
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BillingCycle that)) {
            return false;
        }
        return billingCycleDay == that.billingCycleDay && paymentDueDay == that.paymentDueDay;
    }

    /**
     * @brief 计算哈希值（Compute Hash Code）；
     *        Compute hash code.
     *
     * @return 哈希值（Hash code）。
     */
    @Override
    public int hashCode() {
        return Objects.hash(billingCycleDay, paymentDueDay);
    }

    /**
     * @brief 返回字符串表示（String Representation）；
     *        Return string representation.
     *
     * @return 字符串表示（String representation）。
     */
    @Override
    public String toString() {
        return "BillingCycle{statementDay=" + billingCycleDay + ", dueDay=" + paymentDueDay + "}";
    }

    /**
     * @brief 校验日值范围（Validate Day Range）；
     *        Validate day is in range 1..31.
     *
     * @param day       日期值（Day value）。
     * @param fieldName 字段名（Field name）。
     * @return 归一化后的日值（Validated day）。
     */
    private static int requireValidDay(
            final int day,
            final String fieldName
    ) {
        if (day < 1 || day > 31) {
            throw new BusinessRuleViolation(fieldName + " must be between 1 and 31");
        }
        return day;
    }
}
