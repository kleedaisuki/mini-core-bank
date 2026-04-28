package com.moesegfault.banking.infrastructure.persistence.jdbc;

import com.moesegfault.banking.domain.account.Account;
import com.moesegfault.banking.domain.account.AccountId;
import com.moesegfault.banking.domain.account.AccountNumber;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.account.CustomerId;
import com.moesegfault.banking.domain.account.FxAccount;
import com.moesegfault.banking.domain.account.FxAccountId;
import com.moesegfault.banking.domain.account.InvestmentAccount;
import com.moesegfault.banking.domain.account.InvestmentAccountId;
import com.moesegfault.banking.domain.account.SavingsAccount;
import com.moesegfault.banking.domain.account.SavingsAccountId;
import com.moesegfault.banking.infrastructure.persistence.mapper.AccountRowMapper;
import com.moesegfault.banking.infrastructure.persistence.sql.AccountSql;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @brief 账户仓储 JDBC 实现（JDBC Implementation of Account Repository），对齐 `account` 及子类型表；
 *        JDBC implementation of account repository aligned with `account` and subtype tables.
 */
public final class JdbcAccountRepository implements AccountRepository {

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
    public JdbcAccountRepository(final DataSource dataSource) {
        this(new JdbcTemplate(Objects.requireNonNull(dataSource, "dataSource must not be null")));
    }

    /**
     * @brief 使用 JDBC 模板构造仓储（Construct Repository with JdbcTemplate）；
     *        Construct repository with JDBC template.
     *
     * @param jdbcTemplate JDBC 模板（JDBC template）。
     */
    public JdbcAccountRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveAccount(final Account account) {
        final Account normalized = Objects.requireNonNull(account, "account must not be null");
        jdbcTemplate.update(
                AccountSql.UPSERT_ACCOUNT,
                normalized.accountId().value(),
                normalized.customerId().value(),
                normalized.accountNo().value(),
                normalized.accountType().name(),
                normalized.accountStatus().name(),
                JdbcRepositorySupport.toTimestamp(normalized.openedAt()),
                JdbcRepositorySupport.toTimestamp(normalized.closedAtOrNull()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSavingsAccount(final SavingsAccount savingsAccount) {
        final SavingsAccount normalized = Objects.requireNonNull(savingsAccount, "savingsAccount must not be null");
        saveAccount(normalized.account());
        jdbcTemplate.update(AccountSql.UPSERT_SAVINGS, normalized.savingsAccountId().value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveFxAccount(final FxAccount fxAccount) {
        final FxAccount normalized = Objects.requireNonNull(fxAccount, "fxAccount must not be null");
        saveAccount(normalized.account());
        jdbcTemplate.update(
                AccountSql.UPSERT_FX,
                normalized.fxAccountId().value(),
                normalized.linkedSavingsAccountId().value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveInvestmentAccount(final InvestmentAccount investmentAccount) {
        final InvestmentAccount normalized = Objects.requireNonNull(
                investmentAccount,
                "investmentAccount must not be null");
        saveAccount(normalized.account());
        jdbcTemplate.update(AccountSql.UPSERT_INVESTMENT, normalized.investmentAccountId().value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Account> findAccountById(final AccountId accountId) {
        final AccountId normalized = Objects.requireNonNull(accountId, "accountId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                AccountSql.SELECT_COLUMNS + " WHERE account_id = ?",
                AccountRowMapper.ACCOUNT,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Account> findAccountByNumber(final AccountNumber accountNo) {
        final AccountNumber normalized = Objects.requireNonNull(accountNo, "accountNo must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                AccountSql.SELECT_COLUMNS + " WHERE account_no = ?",
                AccountRowMapper.ACCOUNT,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SavingsAccount> findSavingsAccountById(final SavingsAccountId savingsAccountId) {
        final SavingsAccountId normalized = Objects.requireNonNull(
                savingsAccountId,
                "savingsAccountId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                AccountSql.FIND_SAVINGS_BY_ID,
                AccountRowMapper.ACCOUNT,
                normalized.value()).map(SavingsAccount::restore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FxAccount> findFxAccountById(final FxAccountId fxAccountId) {
        final FxAccountId normalized = Objects.requireNonNull(fxAccountId, "fxAccountId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                AccountSql.FIND_FX_ACCOUNT_BY_ID,
                AccountRowMapper.FX_ACCOUNT,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<InvestmentAccount> findInvestmentAccountById(final InvestmentAccountId investmentAccountId) {
        final InvestmentAccountId normalized = Objects.requireNonNull(
                investmentAccountId,
                "investmentAccountId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                AccountSql.FIND_INVESTMENT_BY_ID,
                AccountRowMapper.ACCOUNT,
                normalized.value()).map(InvestmentAccount::restore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Account> findAccountsByCustomerId(final CustomerId customerId) {
        final CustomerId normalized = Objects.requireNonNull(customerId, "customerId must not be null");
        return jdbcTemplate.query(
                AccountSql.SELECT_COLUMNS + " WHERE customer_id = ? ORDER BY opened_at DESC",
                AccountRowMapper.ACCOUNT,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByAccountNumber(final AccountNumber accountNo) {
        final AccountNumber normalized = Objects.requireNonNull(accountNo, "accountNo must not be null");
        final Boolean exists = jdbcTemplate.queryForObject(
                AccountSql.EXISTS_BY_ACCOUNT_NUMBER,
                Boolean.class,
                normalized.value());
        return Boolean.TRUE.equals(exists);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countInvestmentAccountsByCustomerId(final CustomerId customerId) {
        final CustomerId normalized = Objects.requireNonNull(customerId, "customerId must not be null");
        final Long count = jdbcTemplate.queryForObject(
                AccountSql.COUNT_INVESTMENT_BY_CUSTOMER,
                Long.class,
                normalized.value());
        return count == null ? 0L : count;
    }
}
