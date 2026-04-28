package com.moesegfault.banking.infrastructure.persistence.jdbc;

import com.moesegfault.banking.domain.business.BusinessCategory;
import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.business.BusinessReference;
import com.moesegfault.banking.domain.business.BusinessRepository;
import com.moesegfault.banking.domain.business.BusinessTransaction;
import com.moesegfault.banking.domain.business.BusinessTransactionId;
import com.moesegfault.banking.domain.business.BusinessTransactionStatus;
import com.moesegfault.banking.domain.business.BusinessType;
import com.moesegfault.banking.domain.business.BusinessTypeCode;
import com.moesegfault.banking.domain.business.BusinessTypeStatus;
import com.moesegfault.banking.domain.business.CustomerId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * @brief 业务流水仓储 JDBC 实现（JDBC Implementation of Business Repository），对齐 `business_type/business_transaction`；
 *        JDBC implementation of business repository aligned with business tables.
 */
public final class JdbcBusinessRepository implements BusinessRepository {

    /**
     * @brief 业务类型 upsert SQL（Business Type Upsert SQL）；
     *        Business-type upsert SQL.
     */
    private static final String UPSERT_TYPE_SQL = """
            INSERT INTO business_type (
                business_type_code,
                business_category,
                business_name,
                description,
                is_financial,
                is_reversible,
                status
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (business_type_code) DO UPDATE SET
                business_category = EXCLUDED.business_category,
                business_name = EXCLUDED.business_name,
                description = EXCLUDED.description,
                is_financial = EXCLUDED.is_financial,
                is_reversible = EXCLUDED.is_reversible,
                status = EXCLUDED.status
            """;

    /**
     * @brief 业务交易 upsert SQL（Business Transaction Upsert SQL）；
     *        Business-transaction upsert SQL.
     */
    private static final String UPSERT_TX_SQL = """
            INSERT INTO business_transaction (
                transaction_id,
                business_type_code,
                initiator_customer_id,
                operator_id,
                channel,
                transaction_status,
                requested_at,
                completed_at,
                reference_no,
                remarks
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (transaction_id) DO UPDATE SET
                business_type_code = EXCLUDED.business_type_code,
                initiator_customer_id = EXCLUDED.initiator_customer_id,
                operator_id = EXCLUDED.operator_id,
                channel = EXCLUDED.channel,
                transaction_status = EXCLUDED.transaction_status,
                requested_at = EXCLUDED.requested_at,
                completed_at = EXCLUDED.completed_at,
                reference_no = EXCLUDED.reference_no,
                remarks = EXCLUDED.remarks
            """;

    /**
     * @brief JDBC 模板（JDBC Template）；
     *        JDBC template.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * @brief 业务类型映射器（Business Type Row Mapper）；
     *        Business type row mapper.
     */
    private final RowMapper<BusinessType> businessTypeMapper = (resultSet, rowNum) -> BusinessType.restore(
            BusinessTypeCode.of(resultSet.getString("business_type_code")),
            BusinessCategory.valueOf(resultSet.getString("business_category")),
            resultSet.getString("business_name"),
            resultSet.getString("description"),
            resultSet.getBoolean("is_financial"),
            resultSet.getBoolean("is_reversible"),
            BusinessTypeStatus.valueOf(resultSet.getString("status")));

    /**
     * @brief 业务交易映射器（Business Transaction Row Mapper）；
     *        Business transaction row mapper.
     */
    private final RowMapper<BusinessTransaction> businessTransactionMapper = (resultSet, rowNum) ->
            BusinessTransaction.restore(
                    BusinessTransactionId.of(resultSet.getString("transaction_id")),
                    BusinessTypeCode.of(resultSet.getString("business_type_code")),
                    nullableCustomerId(resultSet.getString("initiator_customer_id")),
                    resultSet.getString("operator_id"),
                    BusinessChannel.valueOf(resultSet.getString("channel")),
                    BusinessTransactionStatus.valueOf(resultSet.getString("transaction_status")),
                    JdbcRepositorySupport.getInstant(resultSet, "requested_at"),
                    JdbcRepositorySupport.getInstant(resultSet, "completed_at"),
                    BusinessReference.of(resultSet.getString("reference_no")),
                    resultSet.getString("remarks"));

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
                UPSERT_TYPE_SQL,
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
                UPSERT_TX_SQL,
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
                "SELECT * FROM business_type WHERE business_type_code = ?",
                businessTypeMapper,
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
                "SELECT * FROM business_transaction WHERE transaction_id = ?",
                businessTransactionMapper,
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
                "SELECT * FROM business_transaction WHERE reference_no = ?",
                businessTransactionMapper,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsTransactionByReference(final BusinessReference referenceNo) {
        final BusinessReference normalized = Objects.requireNonNull(referenceNo, "referenceNo must not be null");
        final Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM business_transaction WHERE reference_no = ?)",
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
                """
                        SELECT *
                        FROM business_transaction
                        WHERE initiator_customer_id = ?
                        ORDER BY requested_at DESC
                        """,
                businessTransactionMapper,
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
                """
                        SELECT *
                        FROM business_transaction
                        WHERE transaction_status = ?
                        ORDER BY requested_at DESC
                        """,
                businessTransactionMapper,
                normalized.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BusinessTransaction> findAllTransactions() {
        return jdbcTemplate.query(
                "SELECT * FROM business_transaction ORDER BY requested_at DESC",
                businessTransactionMapper);
    }

    /**
     * @brief 解析可空客户 ID（Parse Nullable Customer ID）；
     *        Parse nullable customer ID.
     *
     * @param rawValue 原始值（Raw value）。
     * @return 客户 ID 或 null（Customer ID or null）。
     */
    private static CustomerId nullableCustomerId(final String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        return CustomerId.of(rawValue);
    }
}
