package com.moesegfault.banking.infrastructure.persistence.jdbc;

import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.customer.IdentityDocument;
import com.moesegfault.banking.domain.customer.PhoneNumber;
import com.moesegfault.banking.domain.customer.TaxProfile;
import com.moesegfault.banking.infrastructure.persistence.mapper.CustomerRowMapper;
import com.moesegfault.banking.infrastructure.persistence.sql.CustomerSql;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @brief 客户仓储 JDBC 实现（JDBC Implementation of Customer Repository），对齐 `customer` 表结构；
 *        JDBC implementation of customer repository aligned with `customer` table schema.
 */
public final class JdbcCustomerRepository implements CustomerRepository {

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
                CustomerSql.UPSERT,
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
                CustomerSql.SELECT_COLUMNS + " WHERE customer_id = ?",
                CustomerRowMapper.CUSTOMER,
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
                CustomerSql.SELECT_COLUMNS + " WHERE id_type = ? AND id_number = ? AND issuing_region = ?",
                CustomerRowMapper.CUSTOMER,
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
                CustomerSql.EXISTS_BY_IDENTITY_DOCUMENT,
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
                CustomerSql.SELECT_COLUMNS + " WHERE mobile_phone = ? ORDER BY created_at DESC",
                CustomerRowMapper.CUSTOMER,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query(
                CustomerSql.SELECT_COLUMNS + " ORDER BY created_at DESC",
                CustomerRowMapper.CUSTOMER);
    }
}
