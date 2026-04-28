package com.moesegfault.banking.infrastructure.persistence.mapper;

import com.moesegfault.banking.domain.account.Account;
import com.moesegfault.banking.domain.account.AccountId;
import com.moesegfault.banking.domain.account.AccountNumber;
import com.moesegfault.banking.domain.account.AccountStatus;
import com.moesegfault.banking.domain.account.AccountType;
import com.moesegfault.banking.domain.account.CustomerId;
import com.moesegfault.banking.domain.account.FxAccount;
import com.moesegfault.banking.domain.account.SavingsAccountId;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import org.springframework.jdbc.core.RowMapper;

/**
 * @brief 账户行映射器（Account Row Mapper）；
 *        Maps account related rows to domain objects.
 */
public final class AccountRowMapper {

    /**
     * @brief 账户映射器（Account Mapper）；
     *        Mapper for `account` table records.
     */
    public static final RowMapper<Account> ACCOUNT = (resultSet, rowNum) -> Account.restore(
            AccountId.of(resultSet.getString("account_id")),
            CustomerId.of(resultSet.getString("customer_id")),
            AccountNumber.of(resultSet.getString("account_no")),
            AccountType.valueOf(resultSet.getString("account_type")),
            AccountStatus.valueOf(resultSet.getString("account_status")),
            getInstant(resultSet, "opened_at"),
            getInstant(resultSet, "closed_at"));

    /**
     * @brief 外汇账户映射器（FX Account Mapper）；
     *        Mapper for FX account joined query records.
     */
    public static final RowMapper<FxAccount> FX_ACCOUNT = (resultSet, rowNum) -> FxAccount.restore(
            ACCOUNT.mapRow(resultSet, rowNum),
            SavingsAccountId.of(resultSet.getString("linked_savings_account_id")));

    /**
     * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
     *        Utility class should not be instantiated.
     */
    private AccountRowMapper() {
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
