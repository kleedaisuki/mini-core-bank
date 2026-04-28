package com.moesegfault.banking.infrastructure.persistence.jdbc;

import com.moesegfault.banking.domain.customer.Address;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.customer.CustomerStatus;
import com.moesegfault.banking.domain.customer.IdentityDocument;
import com.moesegfault.banking.domain.customer.IdentityDocumentType;
import com.moesegfault.banking.domain.customer.PhoneNumber;
import com.moesegfault.banking.domain.customer.TaxProfile;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * @brief 客户仓储 JDBC 实现（JDBC Implementation of Customer Repository），对齐 `customer` 表结构；
 *        JDBC implementation of customer repository aligned with `customer` table schema.
 */
public final class JdbcCustomerRepository implements CustomerRepository {

    /**
     * @brief 客户 upsert SQL（Customer Upsert SQL）；
     *        Customer upsert SQL.
     */
    private static final String UPSERT_SQL = """
            INSERT INTO customer (
                customer_id,
                id_type,
                id_number,
                issuing_region,
                mobile_phone,
                residential_address,
                mailing_address,
                is_us_tax_resident,
                crs_info,
                customer_status,
                created_at,
                updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (customer_id) DO UPDATE SET
                id_type = EXCLUDED.id_type,
                id_number = EXCLUDED.id_number,
                issuing_region = EXCLUDED.issuing_region,
                mobile_phone = EXCLUDED.mobile_phone,
                residential_address = EXCLUDED.residential_address,
                mailing_address = EXCLUDED.mailing_address,
                is_us_tax_resident = EXCLUDED.is_us_tax_resident,
                crs_info = EXCLUDED.crs_info,
                customer_status = EXCLUDED.customer_status,
                updated_at = EXCLUDED.updated_at
            """;

    /**
     * @brief 默认查询列（Default Select Columns）；
     *        Default select columns.
     */
    private static final String SELECT_COLUMNS = """
            SELECT
                customer_id,
                id_type,
                id_number,
                issuing_region,
                mobile_phone,
                residential_address,
                mailing_address,
                is_us_tax_resident,
                crs_info,
                customer_status,
                created_at,
                updated_at
            FROM customer
            """;

    /**
     * @brief JDBC 模板（JDBC Template）；
     *        JDBC template.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * @brief 客户行映射器（Customer Row Mapper）；
     *        Customer row mapper.
     */
    private final RowMapper<Customer> customerRowMapper = (resultSet, rowNum) -> Customer.restore(
            CustomerId.of(resultSet.getString("customer_id")),
            IdentityDocument.of(
                    IdentityDocumentType.fromDatabaseValue(resultSet.getString("id_type")),
                    resultSet.getString("id_number"),
                    resultSet.getString("issuing_region")),
            PhoneNumber.of(resultSet.getString("mobile_phone")),
            Address.of(resultSet.getString("residential_address")),
            Address.of(resultSet.getString("mailing_address")),
            TaxProfile.of(resultSet.getBoolean("is_us_tax_resident"), resultSet.getString("crs_info")),
            CustomerStatus.valueOf(resultSet.getString("customer_status")),
            JdbcRepositorySupport.getInstant(resultSet, "created_at"),
            JdbcRepositorySupport.getInstant(resultSet, "updated_at"));

    /**
     * @brief 使用数据源构造仓储（Construct Repository with DataSource）；
     *        Construct repository with datasource.
     *
     * @param dataSource 数据源（Data source）。
     */
    public JdbcCustomerRepository(final DataSource dataSource) {
        this(new JdbcTemplate(Objects.requireNonNull(dataSource, "dataSource must not be null")));
    }

    /**
     * @brief 使用 JDBC 模板构造仓储（Construct Repository with JdbcTemplate）；
     *        Construct repository with JDBC template.
     *
     * @param jdbcTemplate JDBC 模板（JDBC template）。
     */
    public JdbcCustomerRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(final Customer customer) {
        final Customer normalized = Objects.requireNonNull(customer, "customer must not be null");
        final IdentityDocument identityDocument = normalized.identityDocument();
        final TaxProfile taxProfile = normalized.taxProfile();
        jdbcTemplate.update(
                UPSERT_SQL,
                normalized.customerId().value(),
                identityDocument.idType().databaseValue(),
                identityDocument.idNumber(),
                identityDocument.issuingRegion(),
                normalized.mobilePhone().value(),
                normalized.residentialAddress().value(),
                normalized.mailingAddress().value(),
                taxProfile.isUsTaxResident(),
                taxProfile.crsInfoOrNull(),
                normalized.customerStatus().name(),
                JdbcRepositorySupport.toTimestamp(normalized.createdAt()),
                JdbcRepositorySupport.toTimestamp(normalized.updatedAt()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Customer> findById(final CustomerId customerId) {
        final CustomerId normalized = Objects.requireNonNull(customerId, "customerId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                SELECT_COLUMNS + " WHERE customer_id = ?",
                customerRowMapper,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Customer> findByIdentityDocument(final IdentityDocument identityDocument) {
        final IdentityDocument normalized = Objects.requireNonNull(
                identityDocument,
                "identityDocument must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                SELECT_COLUMNS + " WHERE id_type = ? AND id_number = ? AND issuing_region = ?",
                customerRowMapper,
                normalized.idType().databaseValue(),
                normalized.idNumber(),
                normalized.issuingRegion());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByIdentityDocument(final IdentityDocument identityDocument) {
        final IdentityDocument normalized = Objects.requireNonNull(
                identityDocument,
                "identityDocument must not be null");
        final Boolean exists = jdbcTemplate.queryForObject(
                """
                        SELECT EXISTS (
                            SELECT 1
                            FROM customer
                            WHERE id_type = ? AND id_number = ? AND issuing_region = ?
                        )
                        """,
                Boolean.class,
                normalized.idType().databaseValue(),
                normalized.idNumber(),
                normalized.issuingRegion());
        return Boolean.TRUE.equals(exists);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> findByMobilePhone(final PhoneNumber mobilePhone) {
        final PhoneNumber normalized = Objects.requireNonNull(mobilePhone, "mobilePhone must not be null");
        return jdbcTemplate.query(
                SELECT_COLUMNS + " WHERE mobile_phone = ? ORDER BY created_at DESC",
                customerRowMapper,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query(SELECT_COLUMNS + " ORDER BY created_at DESC", customerRowMapper);
    }
}
