package com.moesegfault.banking.domain.credit;

import com.moesegfault.banking.domain.shared.Money;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @brief 还款分配策略（Repayment Allocation Policy），按到期顺序分配还款到未结清账单；
 *        Repayment-allocation policy distributing repayment to unsettled statements by due-date order.
 */
public final class RepaymentAllocationPolicy {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private RepaymentAllocationPolicy() {
        // utility class
    }

    /**
     * @brief 将还款分配到账单并返回剩余金额（Allocate Repayment and Return Remainder）；
     *        Allocate repayment across statements and return unallocated remainder.
     *
     * @param repaymentAmount 还款金额（Repayment amount）。
     * @param statements      账单列表（Statement list）。
     * @return 剩余未分配金额（Unallocated remainder）。
     */
    public static Money allocateToStatements(
            final Money repaymentAmount,
            final List<CreditCardStatement> statements
    ) {
        Objects.requireNonNull(repaymentAmount, "Repayment amount must not be null");
        final List<CreditCardStatement> normalizedStatements = Objects.requireNonNull(
                statements,
                "Statements must not be null");
        Money remaining = repaymentAmount;
        for (CreditCardStatement statement : normalizedStatements.stream()
                .filter(candidate -> candidate.statementStatus().canAcceptRepayment())
                .sorted(Comparator
                        .comparing(CreditCardStatement::paymentDueDate)
                        .thenComparing(CreditCardStatement::statementDate)
                        .thenComparing(candidate -> candidate.statementId().value()))
                .toList()) {
            if (remaining.isZero()) {
                break;
            }
            remaining = statement.applyRepayment(remaining);
        }
        return remaining;
    }
}
