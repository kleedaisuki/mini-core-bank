package com.moesegfault.banking.infrastructure.persistence.mapper;

import com.moesegfault.banking.domain.business.BusinessCategory;
import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.business.BusinessReference;
import com.moesegfault.banking.domain.business.BusinessTransaction;
import com.moesegfault.banking.domain.business.BusinessTransactionId;
import com.moesegfault.banking.domain.business.BusinessTransactionStatus;
import com.moesegfault.banking.domain.business.BusinessType;
import com.moesegfault.banking.domain.business.BusinessTypeCode;
import com.moesegfault.banking.domain.business.BusinessTypeStatus;
import com.moesegfault.banking.domain.business.CustomerId;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import org.springframework.jdbc.core.RowMapper;

/**
 * @brief 业务行映射器（Business Row Mapper）；
 *        Maps business type and transaction rows.
 */
public final class BusinessRowMapper {

    /**
     * @brief 业务类型映射器（Business Type Mapper）；
     *        Mapper for `business_type` records.
     */
    public static final RowMapper<BusinessType> BUSINESS_TYPE = (resultSet, rowNum) -> BusinessType.restore(
            BusinessTypeCode.of(resultSet.getString("business_type_code")),
            BusinessCategory.valueOf(resultSet.getString("business_category")),
            resultSet.getString("business_name"),
            resultSet.getString("description"),
            resultSet.getBoolean("is_financial"),
            resultSet.getBoolean("is_reversible"),
            BusinessTypeStatus.valueOf(resultSet.getString("status")));

    /**
     * @brief 业务交易映射器（Business Transaction Mapper）；
     *        Mapper for `business_transaction` records.
     */
    public static final RowMapper<BusinessTransaction> BUSINESS_TRANSACTION = (resultSet, rowNum) ->
            BusinessTransaction.restore(
                    BusinessTransactionId.of(resultSet.getString("transaction_id")),
                    BusinessTypeCode.of(resultSet.getString("business_type_code")),
                    nullableCustomerId(resultSet.getString("initiator_customer_id")),
                    resultSet.getString("operator_id"),
                    BusinessChannel.valueOf(resultSet.getString("channel")),
                    BusinessTransactionStatus.valueOf(resultSet.getString("transaction_status")),
                    getInstant(resultSet, "requested_at"),
                    getInstant(resultSet, "completed_at"),
                    BusinessReference.of(resultSet.getString("reference_no")),
                    resultSet.getString("remarks"));

    /**
     * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
     *        Utility class should not be instantiated.
     */
    private BusinessRowMapper() {
    }

    /**
     * @brief 解析可空客户 ID（Parse Nullable Customer ID）；
     *        Parses nullable customer identifier.
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
