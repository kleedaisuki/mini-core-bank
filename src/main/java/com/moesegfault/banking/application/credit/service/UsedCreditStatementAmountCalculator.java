package com.moesegfault.banking.application.credit.service;

import com.moesegfault.banking.application.credit.port.StatementAmountCalculator;
import com.moesegfault.banking.domain.credit.CreditCardAccount;
import com.moesegfault.banking.domain.shared.DateRange;
import com.moesegfault.banking.domain.shared.Money;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @brief 已用额度账单金额计算实现（Used-credit Statement Amount Calculator），当前阶段用已用额度作为账单应还总额快照；
 *        Used-credit statement-amount calculator that uses consumed credit as current-phase total-due snapshot.
 *
 * @note 该实现是演进阶段的保守默认值；未来可替换为基于交易明细（Transaction Detail）和总账（Ledger）聚合的实现；
 *       This implementation is a conservative default for current lifecycle and can be replaced by transaction/ledger aggregation.
 */
public final class UsedCreditStatementAmountCalculator implements StatementAmountCalculator {

    /**
     * @brief 计算账单应还总额（Calculate Total Amount Due）；
     *        Calculate statement total amount due.
     *
     * @param creditCardAccount 信用卡账户实体（Credit-card-account entity）。
     * @param statementPeriod 账期范围（Statement period）。
     * @param statementDate 出账日期（Statement date）。
     * @return 应还总额（Total amount due）。
     */
    @Override
    public Money calculateTotalAmountDue(
            final CreditCardAccount creditCardAccount,
            final DateRange statementPeriod,
            final LocalDate statementDate
    ) {
        final CreditCardAccount normalizedAccount = Objects.requireNonNull(
                creditCardAccount,
                "creditCardAccount must not be null");
        Objects.requireNonNull(statementPeriod, "statementPeriod must not be null");
        Objects.requireNonNull(statementDate, "statementDate must not be null");
        return normalizedAccount.creditLimit().usedAmount();
    }
}
