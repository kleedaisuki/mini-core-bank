package com.moesegfault.banking.infrastructure.persistence.jdbc;

import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.business.BusinessRepository;
import com.moesegfault.banking.domain.card.CardRepository;
import com.moesegfault.banking.domain.credit.CreditRepository;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.investment.InvestmentRepository;
import com.moesegfault.banking.domain.ledger.LedgerRepository;
import com.moesegfault.banking.infrastructure.persistence.Repository;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import com.moesegfault.banking.infrastructure.persistence.transaction.JdbcTransactionManager;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @brief JDBC 仓储聚合门面（JDBC Repository Aggregate Facade），按读/写能力暴露反射契约并管理写事务边界；
 *        JDBC repository aggregate facade exposing reflective contracts by
 *        read/write scopes and managing write-transaction boundaries.
 */
public final class JdbcRepository implements Repository {

    /**
     * @brief 能力描述符（Capability Descriptor）；
     *        Capability descriptor.
     */
    private final RepositoryDescriptor descriptor;

    /**
     * @brief 读契约映射（Read Contract Map）；
     *        Read contract map.
     */
    private final Map<Class<?>, Object> readContracts;

    /**
     * @brief 写契约映射（Write Contract Map）；
     *        Write contract map.
     */
    private final Map<Class<?>, Object> writeContracts;

    /**
     * @brief 写事务管理器（Write Transaction Manager）；
     *        Write transaction manager.
     */
    private final DbTransactionManager writeTransactionManager;

    /**
     * @brief 使用数据源构造 JDBC 聚合仓储（Construct JDBC Aggregate Repository with
     *        DataSource）；
     *        Construct JDBC aggregate repository with datasource.
     *
     * @param dataSource 数据源（Data source）。
     */
    public JdbcRepository(final DataSource dataSource) {
        final DataSource normalizedDataSource = Objects.requireNonNull(dataSource, "dataSource must not be null");
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(normalizedDataSource);

        final CustomerRepository customerRepository = new JdbcCustomerRepository(jdbcTemplate);
        final AccountRepository accountRepository = new JdbcAccountRepository(jdbcTemplate);
        final CardRepository cardRepository = new JdbcCardRepository(jdbcTemplate);
        final CreditRepository creditRepository = new JdbcCreditRepository(jdbcTemplate);
        final InvestmentRepository investmentRepository = new JdbcInvestmentRepository(jdbcTemplate);
        final BusinessRepository businessRepository = new JdbcBusinessRepository(jdbcTemplate);
        final LedgerRepository ledgerRepository = new JdbcLedgerRepository(jdbcTemplate);

        final JdbcTransactionManager jdbcTransactionManager = new JdbcTransactionManager(normalizedDataSource);

        this.readContracts = Map.of(
                CustomerRepository.class, customerRepository,
                AccountRepository.class, accountRepository,
                CardRepository.class, cardRepository,
                CreditRepository.class, creditRepository,
                InvestmentRepository.class, investmentRepository,
                BusinessRepository.class, businessRepository,
                LedgerRepository.class, ledgerRepository);

        this.writeContracts = Map.of(
                CustomerRepository.class, customerRepository,
                AccountRepository.class, accountRepository,
                CardRepository.class, cardRepository,
                CreditRepository.class, creditRepository,
                InvestmentRepository.class, investmentRepository,
                BusinessRepository.class, businessRepository,
                LedgerRepository.class, ledgerRepository);

        this.writeTransactionManager = jdbcTransactionManager;

        this.descriptor = new RepositoryDescriptor(
                "jdbc-postgresql",
                new SemanticVersion(1, 0, 0),
                Map.of(
                        CapabilityScope.READ, readContracts.keySet(),
                        CapabilityScope.WRITE, writeContracts.keySet()),
                Map.of(
                        "dialect", "postgresql",
                        "driver", "jdbc",
                        "rw-mode", "single-source",
                        "tx-boundary", "repository-managed"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RepositoryDescriptor descriptor() {
        return descriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Optional<T> reader(final Class<T> contractType) {
        return resolveFromMap(readContracts, contractType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Optional<T> writer(final Class<T> contractType) {
        return resolveFromMap(writeContracts, contractType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T writeInTransaction(final Supplier<T> action) {
        return writeTransactionManager.execute(action);
    }

    /**
     * @brief 按契约类型从映射解析实现（Resolve Implementation from Contract Map）；
     *        Resolve implementation from contract map by contract type.
     *
     * @param <T>          契约类型（Contract type）。
     * @param contractMap  契约映射（Contract map）。
     * @param contractType 契约类型（Contract class type）。
     * @return 契约实现可选值（Optional contract implementation）。
     */
    private static <T> Optional<T> resolveFromMap(
            final Map<Class<?>, Object> contractMap,
            final Class<T> contractType) {
        Objects.requireNonNull(contractMap, "contractMap must not be null");
        Objects.requireNonNull(contractType, "contractType must not be null");
        final Object candidate = contractMap.get(contractType);
        if (candidate == null) {
            return Optional.empty();
        }
        return Optional.of(contractType.cast(candidate));
    }
}
