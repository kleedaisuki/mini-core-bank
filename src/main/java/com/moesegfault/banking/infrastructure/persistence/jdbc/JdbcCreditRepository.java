package com.moesegfault.banking.infrastructure.persistence.jdbc;

import com.moesegfault.banking.domain.credit.CreditCardAccount;
import com.moesegfault.banking.domain.credit.CreditCardAccountId;
import com.moesegfault.banking.domain.credit.CreditCardStatement;
import com.moesegfault.banking.domain.credit.CreditLimit;
import com.moesegfault.banking.domain.credit.CreditRepository;
import com.moesegfault.banking.domain.credit.StatementId;
import com.moesegfault.banking.domain.shared.DateRange;
import com.moesegfault.banking.infrastructure.persistence.mapper.CreditRowMapper;
import com.moesegfault.banking.infrastructure.persistence.sql.CreditSql;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @brief 信用仓储 JDBC 实现（JDBC Implementation of Credit Repository），对齐 `credit_card_account/credit_card_statement`；
 *        JDBC implementation of credit repository aligned with credit tables.
 */
public final class JdbcCreditRepository implements CreditRepository {

    /**
     * @brief JDBC 模板（JDBC Template）；
     *        JDBC template.
     */
    private final JdbcTemplate jdbcTemplate;

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
                CreditSql.UPSERT_ACCOUNT,
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
                CreditSql.UPSERT_STATEMENT,
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
                CreditSql.FIND_ACCOUNT_BY_ID,
                CreditRowMapper.CREDIT_CARD_ACCOUNT,
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
                CreditSql.FIND_STATEMENT_BY_ID,
                CreditRowMapper.CREDIT_CARD_STATEMENT,
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
                CreditSql.FIND_STATEMENT_BY_PERIOD,
                CreditRowMapper.CREDIT_CARD_STATEMENT,
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
                CreditSql.LIST_REPAYABLE_BY_ACCOUNT_ID,
                CreditRowMapper.CREDIT_CARD_STATEMENT,
                normalized.value());
    }
}
