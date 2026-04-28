package com.moesegfault.banking.infrastructure.persistence.mapper;

import com.moesegfault.banking.domain.credit.BillingCycle;
import com.moesegfault.banking.domain.credit.CreditCardAccount;
import com.moesegfault.banking.domain.credit.CreditCardAccountId;
import com.moesegfault.banking.domain.credit.CreditCardStatement;
import com.moesegfault.banking.domain.credit.CreditLimit;
import com.moesegfault.banking.domain.credit.InterestRate;
import com.moesegfault.banking.domain.credit.StatementId;
import com.moesegfault.banking.domain.credit.StatementStatus;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DateRange;
import com.moesegfault.banking.domain.shared.Money;
import java.time.LocalDate;
import org.springframework.jdbc.core.RowMapper;

/**
 * @brief 信用行映射器（Credit Row Mapper）；
 *        Maps credit domain related rows.
 */
public final class CreditRowMapper {

    /**
     * @brief 信用账户映射器（Credit Account Mapper）；
     *        Mapper for `credit_card_account` records.
     */
    public static final RowMapper<CreditCardAccount> CREDIT_CARD_ACCOUNT = (resultSet, rowNum) -> {
        final CurrencyCode currencyCode = CurrencyCode.of(resultSet.getString("account_currency_code"));
        final Money totalLimit = Money.of(currencyCode, resultSet.getBigDecimal("credit_limit"));
        final Money availableLimit = Money.of(currencyCode, resultSet.getBigDecimal("available_credit"));
        final Money cashAdvanceLimit = Money.of(currencyCode, resultSet.getBigDecimal("cash_advance_limit"));
        return CreditCardAccount.restore(
                CreditCardAccountId.of(resultSet.getString("account_id")),
                CreditLimit.of(totalLimit, availableLimit, cashAdvanceLimit),
                BillingCycle.of(resultSet.getInt("billing_cycle_day"), resultSet.getInt("payment_due_day")),
                InterestRate.ofDecimal(resultSet.getBigDecimal("interest_rate")),
                currencyCode);
    };

    /**
     * @brief 信用账单映射器（Credit Statement Mapper）；
     *        Mapper for `credit_card_statement` records.
     */
    public static final RowMapper<CreditCardStatement> CREDIT_CARD_STATEMENT = (resultSet, rowNum) -> {
        final CurrencyCode currencyCode = CurrencyCode.of(resultSet.getString("currency_code"));
        return CreditCardStatement.restore(
                StatementId.of(resultSet.getString("statement_id")),
                CreditCardAccountId.of(resultSet.getString("credit_card_account_id")),
                DateRange.of(
                        resultSet.getObject("statement_period_start", LocalDate.class),
                        resultSet.getObject("statement_period_end", LocalDate.class)),
                resultSet.getObject("statement_date", LocalDate.class),
                resultSet.getObject("payment_due_date", LocalDate.class),
                Money.of(currencyCode, resultSet.getBigDecimal("total_amount_due")),
                Money.of(currencyCode, resultSet.getBigDecimal("minimum_amount_due")),
                Money.of(currencyCode, resultSet.getBigDecimal("paid_amount")),
                StatementStatus.valueOf(resultSet.getString("statement_status")),
                currencyCode);
    };

    /**
     * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
     *        Utility class should not be instantiated.
     */
    private CreditRowMapper() {
    }
}
