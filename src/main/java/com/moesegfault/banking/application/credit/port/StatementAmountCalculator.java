package com.moesegfault.banking.application.credit.port;

import com.moesegfault.banking.domain.credit.CreditCardAccount;
import com.moesegfault.banking.domain.shared.DateRange;
import com.moesegfault.banking.domain.shared.Money;
import java.time.LocalDate;

/**
 * @brief 账单金额计算端口（Statement Amount Calculator Port），用于应用层解耦账单聚合来源；
 *        Statement-amount calculator port for decoupling statement aggregation sources in application layer.
 */
@FunctionalInterface
public interface StatementAmountCalculator {

    /**
     * @brief 计算账单应还总额（Calculate Total Amount Due）;
     *        Calculate statement total amount due.
     *
     * @param creditCardAccount 信用卡账户实体（Credit-card-account entity）。
     * @param statementPeriod 账期范围（Statement period）。
     * @param statementDate 出账日期（Statement date）。
     * @return 应还总额（Total amount due）。
     */
    Money calculateTotalAmountDue(
            CreditCardAccount creditCardAccount,
            DateRange statementPeriod,
            LocalDate statementDate);
}
