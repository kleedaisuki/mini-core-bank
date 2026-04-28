package com.moesegfault.banking.infrastructure.persistence.jdbc;

import com.moesegfault.banking.domain.credit.BillingCycle;
import com.moesegfault.banking.domain.credit.CreditCardAccount;
import com.moesegfault.banking.domain.credit.CreditCardAccountId;
import com.moesegfault.banking.domain.credit.CreditCardStatement;
import com.moesegfault.banking.domain.credit.CreditLimit;
import com.moesegfault.banking.domain.credit.CreditRepository;
import com.moesegfault.banking.domain.credit.InterestRate;
import com.moesegfault.banking.domain.credit.StatementId;
import com.moesegfault.banking.domain.credit.StatementStatus;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DateRange;
import com.moesegfault.banking.domain.shared.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * @brief 信用仓储 JDBC 实现（JDBC Implementation of Credit Repository），对齐 `credit_card_account/credit_card_statement`；
 *        JDBC implementation of credit repository aligned with credit tables.
 */
public final class JdbcCreditRepository implements CreditRepository {

    /**
     * @brief 信用账户 upsert SQL（Credit Account Upsert SQL）；
     *        Credit-account upsert SQL.
     */
    private static final String UPSERT_ACCOUNT_SQL = """
            INSERT INTO credit_card_account (
                account_id,
                credit_limit,
                available_credit,
                billing_cycle_day,
                payment_due_day,
                interest_rate,
                cash_advance_limit,
                account_currency_code
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (account_id) DO UPDATE SET
                credit_limit = EXCLUDED.credit_limit,
                available_credit = EXCLUDED.available_credit,
                billing_cycle_day = EXCLUDED.billing_cycle_day,
                payment_due_day = EXCLUDED.payment_due_day,
                interest_rate = EXCLUDED.interest_rate,
                cash_advance_limit = EXCLUDED.cash_advance_limit,
                account_currency_code = EXCLUDED.account_currency_code
            """;

    /**
     * @brief 账单 upsert SQL（Statement Upsert SQL）；
     *        Statement upsert SQL.
     */
    private static final String UPSERT_STATEMENT_SQL = """
            INSERT INTO credit_card_statement (
                statement_id,
                credit_card_account_id,
                statement_period_start,
                statement_period_end,
                statement_date,
                payment_due_date,
                total_amount_due,
                minimum_amount_due,
                paid_amount,
                statement_status,
                currency_code
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (statement_id) DO UPDATE SET
                credit_card_account_id = EXCLUDED.credit_card_account_id,
                statement_period_start = EXCLUDED.statement_period_start,
                statement_period_end = EXCLUDED.statement_period_end,
                statement_date = EXCLUDED.statement_date,
                payment_due_date = EXCLUDED.payment_due_date,
                total_amount_due = EXCLUDED.total_amount_due,
                minimum_amount_due = EXCLUDED.minimum_amount_due,
                paid_amount = EXCLUDED.paid_amount,
                statement_status = EXCLUDED.statement_status,
                currency_code = EXCLUDED.currency_code
            """;

    /**
     * @brief JDBC 模板（JDBC Template）；
     *        JDBC template.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * @brief 信用账户映射器（Credit Account Row Mapper）；
     *        Credit-account row mapper.
     */
    private final RowMapper<CreditCardAccount> accountMapper = (resultSet, rowNum) -> {
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
     * @brief 信用卡账单映射器（Credit Card Statement Row Mapper）；
     *        Credit-card-statement row mapper.
     */
    private final RowMapper<CreditCardStatement> statementMapper = (resultSet, rowNum) -> {
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
     * @brief 使用数据源构造仓储（Construct Repository with DataSource）；
     *        Construct repository with datasource.
     *
     * @param dataSource 数据源（Data source）。
     */
    public JdbcCreditRepository(final DataSource dataSource) {
        this(new JdbcTemplate(Objects.requireNonNull(dataSource, "dataSource must not be null")));
    }

    /**
     * @brief 使用 JDBC 模板构造仓储（Construct Repository with JdbcTemplate）；
     *        Construct repository with JDBC template.
     *
     * @param jdbcTemplate JDBC 模板（JDBC template）。
     */
    public JdbcCreditRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCreditCardAccount(final CreditCardAccount creditCardAccount) {
        final CreditCardAccount normalized = Objects.requireNonNull(
                creditCardAccount,
                "creditCardAccount must not be null");
        final CreditLimit creditLimit = normalized.creditLimit();
        jdbcTemplate.update(
                UPSERT_ACCOUNT_SQL,
                normalized.creditCardAccountId().value(),
                creditLimit.totalLimit().amount(),
                creditLimit.availableLimit().amount(),
                normalized.billingCycle().billingCycleDay(),
                normalized.billingCycle().paymentDueDay(),
                normalized.interestRate().rate().decimalValue(),
                creditLimit.cashAdvanceLimit().amount(),
                normalized.accountCurrencyCode().value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCreditCardStatement(final CreditCardStatement creditCardStatement) {
        final CreditCardStatement normalized = Objects.requireNonNull(
                creditCardStatement,
                "creditCardStatement must not be null");
        final DateRange period = normalized.statementPeriod();
        jdbcTemplate.update(
                UPSERT_STATEMENT_SQL,
                normalized.statementId().value(),
                normalized.creditCardAccountId().value(),
                period.start(),
                period.end(),
                normalized.statementDate(),
                normalized.paymentDueDate(),
                normalized.totalAmountDue().amount(),
                normalized.minimumAmountDue().amount(),
                normalized.paidAmount().amount(),
                normalized.statementStatus().name(),
                normalized.currencyCode().value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CreditCardAccount> findCreditCardAccountById(final CreditCardAccountId creditCardAccountId) {
        final CreditCardAccountId normalized = Objects.requireNonNull(
                creditCardAccountId,
                "creditCardAccountId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                "SELECT * FROM credit_card_account WHERE account_id = ?",
                accountMapper,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CreditCardStatement> findStatementById(final StatementId statementId) {
        final StatementId normalized = Objects.requireNonNull(statementId, "statementId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                "SELECT * FROM credit_card_statement WHERE statement_id = ?",
                statementMapper,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CreditCardStatement> findStatementByPeriod(
            final CreditCardAccountId creditCardAccountId,
            final DateRange statementPeriod
    ) {
        final CreditCardAccountId normalizedAccountId = Objects.requireNonNull(
                creditCardAccountId,
                "creditCardAccountId must not be null");
        final DateRange normalizedPeriod = Objects.requireNonNull(statementPeriod, "statementPeriod must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                """
                        SELECT *
                        FROM credit_card_statement
                        WHERE credit_card_account_id = ?
                          AND statement_period_start = ?
                          AND statement_period_end = ?
                        """,
                statementMapper,
                normalizedAccountId.value(),
                normalizedPeriod.start(),
                normalizedPeriod.end());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CreditCardStatement> listRepayableStatementsByAccountId(final CreditCardAccountId creditCardAccountId) {
        final CreditCardAccountId normalized = Objects.requireNonNull(
                creditCardAccountId,
                "creditCardAccountId must not be null");
        return jdbcTemplate.query(
                """
                        SELECT *
                        FROM credit_card_statement
                        WHERE credit_card_account_id = ?
                          AND statement_status IN ('OPEN', 'OVERDUE')
                        ORDER BY statement_date ASC
                        """,
                statementMapper,
                normalized.value());
    }
}
