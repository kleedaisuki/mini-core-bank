package com.moesegfault.banking.infrastructure.persistence.mapper;

import com.moesegfault.banking.domain.ledger.Balance;
import com.moesegfault.banking.domain.ledger.EntryDirection;
import com.moesegfault.banking.domain.ledger.EntryType;
import com.moesegfault.banking.domain.ledger.LedgerEntry;
import com.moesegfault.banking.domain.ledger.LedgerEntryId;
import com.moesegfault.banking.domain.ledger.PostingBatchId;
import com.moesegfault.banking.domain.ledger.PostingStatus;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import org.springframework.jdbc.core.RowMapper;

/**
 * @brief 账务行映射器（Ledger Row Mapper）；
 *        Maps ledger related rows to domain objects.
 */
public final class LedgerRowMapper {

    /**
     * @brief 余额映射器（Balance Mapper）；
     *        Mapper for `account_balance` records.
     */
    public static final RowMapper<Balance> BALANCE = (resultSet, rowNum) -> {
        final CurrencyCode currencyCode = CurrencyCode.of(resultSet.getString("currency_code"));
        return Balance.restore(
                resultSet.getString("account_id"),
                currencyCode,
                Money.of(currencyCode, resultSet.getBigDecimal("ledger_balance")),
                Money.of(currencyCode, resultSet.getBigDecimal("available_balance")),
                getInstant(resultSet, "updated_at"));
    };

    /**
     * @brief 分录映射器（Ledger Entry Mapper）；
     *        Mapper for `account_entry` records.
     */
    public static final RowMapper<LedgerEntry> LEDGER_ENTRY = (resultSet, rowNum) -> {
        final CurrencyCode currencyCode = CurrencyCode.of(resultSet.getString("currency_code"));
        final BigDecimal ledgerBalanceAfter = resultSet.getBigDecimal("ledger_balance_after");
        final BigDecimal availableBalanceAfter = resultSet.getBigDecimal("available_balance_after");
        return LedgerEntry.restore(
                LedgerEntryId.of(resultSet.getString("entry_id")),
                resultSet.getString("transaction_id"),
                nullablePostingBatchId(resultSet.getString("batch_id")),
                resultSet.getString("account_id"),
                currencyCode,
                EntryDirection.valueOf(resultSet.getString("entry_direction")),
                Money.of(currencyCode, resultSet.getBigDecimal("amount")),
                ledgerBalanceAfter == null ? null : Money.of(currencyCode, ledgerBalanceAfter),
                availableBalanceAfter == null ? null : Money.of(currencyCode, availableBalanceAfter),
                EntryType.valueOf(resultSet.getString("entry_type")),
                getInstant(resultSet, "posted_at"));
    };

    /**
     * @brief 批次快照映射器（Posting Batch Snapshot Mapper）；
     *        Mapper for `posting_batch` query rows.
     */
    public static final RowMapper<PostingBatchSnapshot> POSTING_BATCH_SNAPSHOT = (resultSet,
            rowNum) -> new PostingBatchSnapshot(
                    PostingBatchId.of(resultSet.getString("batch_id")),
                    resultSet.getString("transaction_id"),
                    resultSet.getString("idempotency_key"),
                    PostingStatus.valueOf(resultSet.getString("batch_status")),
                    getInstant(resultSet, "posted_at"),
                    getInstant(resultSet, "created_at"));

    /**
     * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
     *        Utility class should not be instantiated.
     */
    private LedgerRowMapper() {
    }

    /**
     * @brief 可空批次快照（Posting Batch Snapshot）；
     *        Lightweight row snapshot of posting batch.
     *
     * @param batchId        批次 ID（Batch ID）。
     * @param transactionId  交易 ID（Transaction ID）。
     * @param idempotencyKey 幂等键（Idempotency key）。
     * @param batchStatus    批次状态（Batch status）。
     * @param postedAt       入账时间（Posted time）。
     * @param createdAt      创建时间（Created time）。
     */
    public record PostingBatchSnapshot(
            PostingBatchId batchId,
            String transactionId,
            String idempotencyKey,
            PostingStatus batchStatus,
            Instant postedAt,
            Instant createdAt) {
    }

    /**
     * @brief 解析可空批次 ID（Parse Nullable Posting Batch ID）；
     *        Parses nullable posting batch identifier.
     *
     * @param rawValue 原始值（Raw value）。
     * @return 批次 ID 或 null（Batch ID or null）。
     */
    private static PostingBatchId nullablePostingBatchId(final String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        return PostingBatchId.of(rawValue);
    }

    /**
     * @brief 从结果集读取时间戳（Read Instant from ResultSet）；
     *        Reads nullable timestamp column as `Instant`.
     *
     * @param resultSet 结果集（Result set）。
     * @param column    列名（Column name）。
     * @return 时间点或 null（Instant or null）。
     * @throws SQLException SQL 读取异常（SQL read exception）。
     */
    private static Instant getInstant(final ResultSet resultSet, final String column) throws SQLException {
        final Timestamp timestamp = resultSet.getTimestamp(column);
        return timestamp == null ? null : timestamp.toInstant();
    }
}
