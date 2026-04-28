package com.moesegfault.banking.infrastructure.persistence.jdbc;

import com.moesegfault.banking.domain.business.BusinessReference;
import com.moesegfault.banking.domain.business.BusinessRepository;
import com.moesegfault.banking.domain.business.BusinessTransaction;
import com.moesegfault.banking.domain.business.BusinessTransactionId;
import com.moesegfault.banking.domain.business.BusinessTransactionStatus;
import com.moesegfault.banking.domain.business.BusinessType;
import com.moesegfault.banking.domain.business.BusinessTypeCode;
import com.moesegfault.banking.domain.business.CustomerId;
import com.moesegfault.banking.infrastructure.persistence.mapper.BusinessRowMapper;
import com.moesegfault.banking.infrastructure.persistence.sql.BusinessSql;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @brief 业务流水仓储 JDBC 实现（JDBC Implementation of Business Repository），对齐 `business_type/business_transaction`；
 *        JDBC implementation of business repository aligned with business tables.
 */
public final class JdbcBusinessRepository implements BusinessRepository {

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
    public JdbcBusinessRepository(final DataSource dataSource) {
        this(new JdbcTemplate(Objects.requireNonNull(dataSource, "dataSource must not be null")));
    }

    /**
     * @brief 使用 JDBC 模板构造仓储（Construct Repository with JdbcTemplate）；
     *        Construct repository with JDBC template.
     *
     * @param jdbcTemplate JDBC 模板（JDBC template）。
     */
    public JdbcBusinessRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveBusinessType(final BusinessType businessType) {
        final BusinessType normalized = Objects.requireNonNull(businessType, "businessType must not be null");
        jdbcTemplate.update(
                BusinessSql.UPSERT_TYPE,
                normalized.businessTypeCode().value(),
                normalized.businessCategory().name(),
                normalized.businessName(),
                normalized.descriptionOrNull(),
                normalized.isFinancial(),
                normalized.isReversible(),
                normalized.status().name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveTransaction(final BusinessTransaction businessTransaction) {
        final BusinessTransaction normalized = Objects.requireNonNull(
                businessTransaction,
                "businessTransaction must not be null");
        jdbcTemplate.update(
                BusinessSql.UPSERT_TRANSACTION,
                normalized.transactionId().value(),
                normalized.businessTypeCode().value(),
                normalized.initiatorCustomerIdOrNull() == null ? null : normalized.initiatorCustomerIdOrNull().value(),
                normalized.operatorIdOrNull(),
                normalized.channel().name(),
                normalized.transactionStatus().name(),
                JdbcRepositorySupport.toTimestamp(normalized.requestedAt()),
                JdbcRepositorySupport.toTimestamp(normalized.completedAtOrNull()),
                normalized.referenceNo().value(),
                normalized.remarksOrNull());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<BusinessType> findBusinessTypeByCode(final BusinessTypeCode businessTypeCode) {
        final BusinessTypeCode normalized = Objects.requireNonNull(
                businessTypeCode,
                "businessTypeCode must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                BusinessSql.FIND_TYPE_BY_CODE,
                BusinessRowMapper.BUSINESS_TYPE,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<BusinessTransaction> findTransactionById(final BusinessTransactionId transactionId) {
        final BusinessTransactionId normalized = Objects.requireNonNull(transactionId, "transactionId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                BusinessSql.FIND_TRANSACTION_BY_ID,
                BusinessRowMapper.BUSINESS_TRANSACTION,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<BusinessTransaction> findTransactionByReference(final BusinessReference referenceNo) {
        final BusinessReference normalized = Objects.requireNonNull(referenceNo, "referenceNo must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                BusinessSql.FIND_TRANSACTION_BY_REFERENCE,
                BusinessRowMapper.BUSINESS_TRANSACTION,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsTransactionByReference(final BusinessReference referenceNo) {
        final BusinessReference normalized = Objects.requireNonNull(referenceNo, "referenceNo must not be null");
        final Boolean exists = jdbcTemplate.queryForObject(
                BusinessSql.EXISTS_TRANSACTION_BY_REFERENCE,
                Boolean.class,
                normalized.value());
        return Boolean.TRUE.equals(exists);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BusinessTransaction> listTransactionsByCustomerId(final CustomerId initiatorCustomerId) {
        final CustomerId normalized = Objects.requireNonNull(
                initiatorCustomerId,
                "initiatorCustomerId must not be null");
        return jdbcTemplate.query(
                BusinessSql.LIST_TRANSACTIONS_BY_CUSTOMER_ID,
                BusinessRowMapper.BUSINESS_TRANSACTION,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BusinessTransaction> listTransactionsByStatus(final BusinessTransactionStatus transactionStatus) {
        final BusinessTransactionStatus normalized = Objects.requireNonNull(
                transactionStatus,
                "transactionStatus must not be null");
        return jdbcTemplate.query(
                BusinessSql.LIST_TRANSACTIONS_BY_STATUS,
                BusinessRowMapper.BUSINESS_TRANSACTION,
                normalized.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BusinessTransaction> findAllTransactions() {
        return jdbcTemplate.query(
                BusinessSql.FIND_ALL_TRANSACTIONS,
                BusinessRowMapper.BUSINESS_TRANSACTION);
    }
}
