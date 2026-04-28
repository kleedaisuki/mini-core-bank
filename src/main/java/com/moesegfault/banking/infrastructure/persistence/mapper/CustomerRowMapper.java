package com.moesegfault.banking.infrastructure.persistence.mapper;

import com.moesegfault.banking.domain.customer.Address;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.customer.CustomerStatus;
import com.moesegfault.banking.domain.customer.IdentityDocument;
import com.moesegfault.banking.domain.customer.IdentityDocumentType;
import com.moesegfault.banking.domain.customer.PhoneNumber;
import com.moesegfault.banking.domain.customer.TaxProfile;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import org.springframework.jdbc.core.RowMapper;

/**
 * @brief 客户行映射器（Customer Row Mapper）；
 *        Maps `customer` query rows to `Customer` domain entity.
 */
public final class CustomerRowMapper {

    /**
     * @brief 客户映射器实例（Customer Mapper Instance）；
     *        Mapper instance for customer records.
     */
    public static final RowMapper<Customer> CUSTOMER = (resultSet, rowNum) -> Customer.restore(
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
            getInstant(resultSet, "created_at"),
            getInstant(resultSet, "updated_at"));

    /**
     * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
     *        Utility class should not be instantiated.
     */
    private CustomerRowMapper() {
    }

    /**
     * @brief 从结果集读取时间戳（Read Instant from ResultSet）；
     *        Reads nullable timestamp column as `Instant`.
     *
     * @param resultSet 结果集（Result set）。
     * @param column 列名（Column name）。
     * @return 时间点或 null（Instant or null）。
     * @throws SQLException SQL 读取异常（SQL read exception）。
     */
    private static Instant getInstant(final ResultSet resultSet, final String column) throws SQLException {
        final Timestamp timestamp = resultSet.getTimestamp(column);
        return timestamp == null ? null : timestamp.toInstant();
    }
}
