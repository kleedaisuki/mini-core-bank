package com.moesegfault.banking.infrastructure.persistence.jdbc;

import com.moesegfault.banking.domain.account.Account;
import com.moesegfault.banking.domain.account.AccountId;
import com.moesegfault.banking.domain.account.AccountNumber;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.account.AccountStatus;
import com.moesegfault.banking.domain.account.AccountType;
import com.moesegfault.banking.domain.account.CustomerId;
import com.moesegfault.banking.domain.account.FxAccount;
import com.moesegfault.banking.domain.account.FxAccountId;
import com.moesegfault.banking.domain.account.InvestmentAccount;
import com.moesegfault.banking.domain.account.InvestmentAccountId;
import com.moesegfault.banking.domain.account.SavingsAccount;
import com.moesegfault.banking.domain.account.SavingsAccountId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * @brief 账户仓储 JDBC 实现（JDBC Implementation of Account Repository），对齐 `account` 及子类型表；
 *        JDBC implementation of account repository aligned with `account` and subtype tables.
 */
public final class JdbcAccountRepository implements AccountRepository {

    /**
     * @brief 账户 upsert SQL（Account Upsert SQL）；
     *        Account upsert SQL.
     */
    private static final String UPSERT_ACCOUNT_SQL = """
            INSERT INTO account (
                account_id,
                customer_id,
                account_no,
                account_type,
                account_status,
                opened_at,
                closed_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (account_id) DO UPDATE SET
                customer_id = EXCLUDED.customer_id,
                account_no = EXCLUDED.account_no,
                account_type = EXCLUDED.account_type,
                account_status = EXCLUDED.account_status,
                opened_at = EXCLUDED.opened_at,
                closed_at = EXCLUDED.closed_at
            """;

    /**
     * @brief 储蓄账户 upsert SQL（Savings Account Upsert SQL）；
     *        Savings-account upsert SQL.
     */
    private static final String UPSERT_SAVINGS_SQL = """
            INSERT INTO savings_account (account_id)
            VALUES (?)
            ON CONFLICT (account_id) DO NOTHING
            """;

    /**
     * @brief 外汇账户 upsert SQL（FX Account Upsert SQL）；
     *        FX-account upsert SQL.
     */
    private static final String UPSERT_FX_SQL = """
            INSERT INTO fx_account (account_id, linked_savings_account_id)
            VALUES (?, ?)
            ON CONFLICT (account_id) DO UPDATE SET
                linked_savings_account_id = EXCLUDED.linked_savings_account_id
            """;

    /**
     * @brief 投资账户 upsert SQL（Investment Account Upsert SQL）；
     *        Investment-account upsert SQL.
     */
    private static final String UPSERT_INVESTMENT_SQL = """
            INSERT INTO investment_account (account_id)
            VALUES (?)
            ON CONFLICT (account_id) DO NOTHING
            """;

    /**
     * @brief 账户查询列（Account Select Columns）；
     *        Account select columns.
     */
    private static final String ACCOUNT_COLUMNS = """
            SELECT
                account_id,
                customer_id,
                account_no,
                account_type,
                account_status,
                opened_at,
                closed_at
            FROM account
            """;

    /**
     * @brief JDBC 模板（JDBC Template）；
     *        JDBC template.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * @brief 账户行映射器（Account Row Mapper）；
     *        Account row mapper.
     */
    private final RowMapper<Account> accountRowMapper = (resultSet, rowNum) -> Account.restore(
            AccountId.of(resultSet.getString("account_id")),
            CustomerId.of(resultSet.getString("customer_id")),
            AccountNumber.of(resultSet.getString("account_no")),
            AccountType.valueOf(resultSet.getString("account_type")),
            AccountStatus.valueOf(resultSet.getString("account_status")),
            JdbcRepositorySupport.getInstant(resultSet, "opened_at"),
            JdbcRepositorySupport.getInstant(resultSet, "closed_at"));

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
                UPSERT_ACCOUNT_SQL,
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
        jdbcTemplate.update(UPSERT_SAVINGS_SQL, normalized.savingsAccountId().value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveFxAccount(final FxAccount fxAccount) {
        final FxAccount normalized = Objects.requireNonNull(fxAccount, "fxAccount must not be null");
        saveAccount(normalized.account());
        jdbcTemplate.update(
                UPSERT_FX_SQL,
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
        jdbcTemplate.update(UPSERT_INVESTMENT_SQL, normalized.investmentAccountId().value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Account> findAccountById(final AccountId accountId) {
        final AccountId normalized = Objects.requireNonNull(accountId, "accountId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                ACCOUNT_COLUMNS + " WHERE account_id = ?",
                accountRowMapper,
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
                ACCOUNT_COLUMNS + " WHERE account_no = ?",
                accountRowMapper,
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
                ACCOUNT_COLUMNS + " WHERE account_id = ? AND account_type = 'SAVINGS' AND EXISTS ("
                        + "SELECT 1 FROM savings_account s WHERE s.account_id = account.account_id)",
                accountRowMapper,
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
                """
                        SELECT
                            a.account_id,
                            a.customer_id,
                            a.account_no,
                            a.account_type,
                            a.account_status,
                            a.opened_at,
                            a.closed_at,
                            f.linked_savings_account_id
                        FROM account a
                        JOIN fx_account f ON f.account_id = a.account_id
                        WHERE a.account_id = ?
                          AND a.account_type = 'FX'
                        """,
                (resultSet, rowNum) -> FxAccount.restore(
                        accountRowMapper.mapRow(resultSet, rowNum),
                        SavingsAccountId.of(resultSet.getString("linked_savings_account_id"))),
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
                ACCOUNT_COLUMNS + " WHERE account_id = ? AND account_type = 'INVESTMENT' AND EXISTS ("
                        + "SELECT 1 FROM investment_account i WHERE i.account_id = account.account_id)",
                accountRowMapper,
                normalized.value()).map(InvestmentAccount::restore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Account> findAccountsByCustomerId(final CustomerId customerId) {
        final CustomerId normalized = Objects.requireNonNull(customerId, "customerId must not be null");
        return jdbcTemplate.query(
                ACCOUNT_COLUMNS + " WHERE customer_id = ? ORDER BY opened_at DESC",
                accountRowMapper,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByAccountNumber(final AccountNumber accountNo) {
        final AccountNumber normalized = Objects.requireNonNull(accountNo, "accountNo must not be null");
        final Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM account WHERE account_no = ?)",
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
                """
                        SELECT COUNT(1)
                        FROM investment_account i
                        JOIN account a ON a.account_id = i.account_id
                        WHERE a.customer_id = ?
                        """,
                Long.class,
                normalized.value());
        return count == null ? 0L : count;
    }
}
