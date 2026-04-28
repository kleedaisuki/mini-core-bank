package com.moesegfault.banking.infrastructure.persistence.jdbc;

import com.moesegfault.banking.domain.ledger.Balance;
import com.moesegfault.banking.domain.ledger.EntryDirection;
import com.moesegfault.banking.domain.ledger.EntryType;
import com.moesegfault.banking.domain.ledger.LedgerEntry;
import com.moesegfault.banking.domain.ledger.LedgerEntryId;
import com.moesegfault.banking.domain.ledger.LedgerRepository;
import com.moesegfault.banking.domain.ledger.PostingBatch;
import com.moesegfault.banking.domain.ledger.PostingBatchId;
import com.moesegfault.banking.domain.ledger.PostingStatus;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * @brief 账务仓储 JDBC 实现（JDBC Implementation of Ledger Repository），对齐 `posting_batch/account_balance/account_entry`；
 *        JDBC implementation of ledger repository aligned with ledger tables.
 */
public final class JdbcLedgerRepository implements LedgerRepository {

    /**
     * @brief 余额 upsert SQL（Balance Upsert SQL）；
     *        Balance upsert SQL.
     */
    private static final String UPSERT_BALANCE_SQL = """
            INSERT INTO account_balance (
                account_id,
                currency_code,
                ledger_balance,
                available_balance,
                updated_at
            ) VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (account_id, currency_code) DO UPDATE SET
                ledger_balance = EXCLUDED.ledger_balance,
                available_balance = EXCLUDED.available_balance,
                updated_at = EXCLUDED.updated_at
            """;

    /**
     * @brief 分录 upsert SQL（Ledger Entry Upsert SQL）；
     *        Ledger-entry upsert SQL.
     */
    private static final String UPSERT_ENTRY_SQL = """
            INSERT INTO account_entry (
                entry_id,
                transaction_id,
                batch_id,
                account_id,
                currency_code,
                entry_direction,
                amount,
                ledger_balance_after,
                available_balance_after,
                entry_type,
                posted_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (entry_id) DO UPDATE SET
                transaction_id = EXCLUDED.transaction_id,
                batch_id = EXCLUDED.batch_id,
                account_id = EXCLUDED.account_id,
                currency_code = EXCLUDED.currency_code,
                entry_direction = EXCLUDED.entry_direction,
                amount = EXCLUDED.amount,
                ledger_balance_after = EXCLUDED.ledger_balance_after,
                available_balance_after = EXCLUDED.available_balance_after,
                entry_type = EXCLUDED.entry_type,
                posted_at = EXCLUDED.posted_at
            """;

    /**
     * @brief 批次 upsert SQL（Posting Batch Upsert SQL）；
     *        Posting-batch upsert SQL.
     */
    private static final String UPSERT_BATCH_SQL = """
            INSERT INTO posting_batch (
                batch_id,
                transaction_id,
                idempotency_key,
                batch_status,
                posted_at,
                created_at
            ) VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (batch_id) DO UPDATE SET
                transaction_id = EXCLUDED.transaction_id,
                idempotency_key = EXCLUDED.idempotency_key,
                batch_status = EXCLUDED.batch_status,
                posted_at = EXCLUDED.posted_at,
                created_at = EXCLUDED.created_at
            """;

    /**
     * @brief JDBC 模板（JDBC Template）；
     *        JDBC template.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * @brief 余额映射器（Balance Row Mapper）；
     *        Balance row mapper.
     */
    private final RowMapper<Balance> balanceMapper = (resultSet, rowNum) -> {
        final CurrencyCode currencyCode = CurrencyCode.of(resultSet.getString("currency_code"));
        return Balance.restore(
                resultSet.getString("account_id"),
                currencyCode,
                Money.of(currencyCode, resultSet.getBigDecimal("ledger_balance")),
                Money.of(currencyCode, resultSet.getBigDecimal("available_balance")),
                JdbcRepositorySupport.getInstant(resultSet, "updated_at"));
    };

    /**
     * @brief 分录映射器（Ledger Entry Row Mapper）；
     *        Ledger entry row mapper.
     */
    private final RowMapper<LedgerEntry> ledgerEntryMapper = (resultSet, rowNum) -> {
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
                JdbcRepositorySupport.getInstant(resultSet, "posted_at"));
    };

    /**
     * @brief 使用数据源构造仓储（Construct Repository with DataSource）；
     *        Construct repository with datasource.
     *
     * @param dataSource 数据源（Data source）。
     */
    public JdbcLedgerRepository(final DataSource dataSource) {
        this(new JdbcTemplate(Objects.requireNonNull(dataSource, "dataSource must not be null")));
    }

    /**
     * @brief 使用 JDBC 模板构造仓储（Construct Repository with JdbcTemplate）；
     *        Construct repository with JDBC template.
     *
     * @param jdbcTemplate JDBC 模板（JDBC template）。
     */
    public JdbcLedgerRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveBalance(final Balance balance) {
        final Balance normalized = Objects.requireNonNull(balance, "balance must not be null");
        jdbcTemplate.update(
                UPSERT_BALANCE_SQL,
                normalized.accountId(),
                normalized.currencyCode().value(),
                normalized.ledgerBalance().amount(),
                normalized.availableBalance().amount(),
                JdbcRepositorySupport.toTimestamp(normalized.updatedAt()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveEntry(final LedgerEntry ledgerEntry) {
        final LedgerEntry normalized = Objects.requireNonNull(ledgerEntry, "ledgerEntry must not be null");
        jdbcTemplate.update(
                UPSERT_ENTRY_SQL,
                normalized.entryId().value(),
                normalized.transactionId(),
                normalized.batchIdOrNull() == null ? null : normalized.batchIdOrNull().value(),
                normalized.accountId(),
                normalized.currencyCode().value(),
                normalized.entryDirection().name(),
                normalized.amount().amount(),
                normalized.ledgerBalanceAfterOrNull() == null ? null : normalized.ledgerBalanceAfterOrNull().amount(),
                normalized.availableBalanceAfterOrNull() == null ? null : normalized.availableBalanceAfterOrNull().amount(),
                normalized.entryType().name(),
                JdbcRepositorySupport.toTimestamp(normalized.postedAt()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveEntries(final List<LedgerEntry> ledgerEntries) {
        final List<LedgerEntry> normalized = Objects.requireNonNull(ledgerEntries, "ledgerEntries must not be null");
        if (normalized.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(
                UPSERT_ENTRY_SQL,
                normalized,
                normalized.size(),
                (preparedStatement, entry) -> {
                    preparedStatement.setString(1, entry.entryId().value());
                    preparedStatement.setString(2, entry.transactionId());
                    preparedStatement.setString(3, entry.batchIdOrNull() == null ? null : entry.batchIdOrNull().value());
                    preparedStatement.setString(4, entry.accountId());
                    preparedStatement.setString(5, entry.currencyCode().value());
                    preparedStatement.setString(6, entry.entryDirection().name());
                    preparedStatement.setBigDecimal(7, entry.amount().amount());
                    preparedStatement.setBigDecimal(
                            8,
                            entry.ledgerBalanceAfterOrNull() == null ? null : entry.ledgerBalanceAfterOrNull().amount());
                    preparedStatement.setBigDecimal(
                            9,
                            entry.availableBalanceAfterOrNull() == null ? null : entry.availableBalanceAfterOrNull().amount());
                    preparedStatement.setString(10, entry.entryType().name());
                    preparedStatement.setTimestamp(11, JdbcRepositorySupport.toTimestamp(entry.postedAt()));
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void savePostingBatch(final PostingBatch postingBatch) {
        final PostingBatch normalized = Objects.requireNonNull(postingBatch, "postingBatch must not be null");
        jdbcTemplate.update(
                UPSERT_BATCH_SQL,
                normalized.batchId().value(),
                normalized.transactionId(),
                normalized.idempotencyKeyOrNull(),
                normalized.batchStatus().name(),
                JdbcRepositorySupport.toTimestamp(normalized.postedAtOrNull()),
                JdbcRepositorySupport.toTimestamp(normalized.createdAt()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<PostingBatch> findPostingBatchById(final PostingBatchId postingBatchId) {
        final PostingBatchId normalized = Objects.requireNonNull(postingBatchId, "postingBatchId must not be null");
        return findPostingBatchByWhereClause("batch_id = ?", normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<PostingBatch> findPostingBatchByTransactionId(final String transactionId) {
        final String normalized = normalizeRequiredString(transactionId, "transactionId");
        return findPostingBatchByWhereClause("transaction_id = ?", normalized);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<PostingBatch> findPostingBatchByIdempotencyKey(final String idempotencyKey) {
        final String normalized = normalizeRequiredString(idempotencyKey, "idempotencyKey");
        return findPostingBatchByWhereClause("idempotency_key = ?", normalized);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Balance> findBalance(final String accountId, final CurrencyCode currencyCode) {
        final String normalizedAccountId = normalizeRequiredString(accountId, "accountId");
        final CurrencyCode normalizedCurrencyCode = Objects.requireNonNull(currencyCode, "currencyCode must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                "SELECT * FROM account_balance WHERE account_id = ? AND currency_code = ?",
                balanceMapper,
                normalizedAccountId,
                normalizedCurrencyCode.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Balance> listBalancesByAccountId(final String accountId) {
        final String normalized = normalizeRequiredString(accountId, "accountId");
        return jdbcTemplate.query(
                "SELECT * FROM account_balance WHERE account_id = ? ORDER BY currency_code",
                balanceMapper,
                normalized);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LedgerEntry> listEntriesByTransactionId(final String transactionId) {
        final String normalized = normalizeRequiredString(transactionId, "transactionId");
        return jdbcTemplate.query(
                "SELECT * FROM account_entry WHERE transaction_id = ? ORDER BY posted_at ASC, entry_id ASC",
                ledgerEntryMapper,
                normalized);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LedgerEntry> listRecentEntriesByAccountId(final String accountId, final int limit) {
        final String normalizedAccountId = normalizeRequiredString(accountId, "accountId");
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }
        return jdbcTemplate.query(
                "SELECT * FROM account_entry WHERE account_id = ? ORDER BY posted_at DESC, entry_id DESC LIMIT ?",
                ledgerEntryMapper,
                normalizedAccountId,
                limit);
    }

    /**
     * @brief 通过条件查询批次（Find Posting Batch by Where Clause）；
     *        Find posting batch by where clause.
     *
     * @param whereClause 条件片段（Where clause fragment）。
     * @param argument 条件参数（Where argument）。
     * @return 批次可选值（Optional posting batch）。
     */
    private Optional<PostingBatch> findPostingBatchByWhereClause(
            final String whereClause,
            final Object argument
    ) {
        final Optional<PostingBatchRecord> batchRecord = JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                "SELECT * FROM posting_batch WHERE " + whereClause,
                (resultSet, rowNum) -> new PostingBatchRecord(
                        PostingBatchId.of(resultSet.getString("batch_id")),
                        resultSet.getString("transaction_id"),
                        resultSet.getString("idempotency_key"),
                        PostingStatus.valueOf(resultSet.getString("batch_status")),
                        JdbcRepositorySupport.getInstant(resultSet, "posted_at"),
                        JdbcRepositorySupport.getInstant(resultSet, "created_at")),
                argument);
        if (batchRecord.isEmpty()) {
            return Optional.empty();
        }
        final PostingBatchRecord record = batchRecord.get();
        final List<LedgerEntry> entries = jdbcTemplate.query(
                "SELECT * FROM account_entry WHERE batch_id = ? ORDER BY posted_at ASC, entry_id ASC",
                ledgerEntryMapper,
                record.batchId().value());
        return Optional.of(PostingBatch.restore(
                record.batchId(),
                record.transactionId(),
                record.idempotencyKey(),
                record.batchStatus(),
                record.postedAt(),
                record.createdAt(),
                entries));
    }

    /**
     * @brief 标准化必填字符串（Normalize Required String）；
     *        Normalize required string.
     *
     * @param rawValue 原始值（Raw value）。
     * @param fieldName 字段名（Field name）。
     * @return 标准化结果（Normalized string）。
     */
    private static String normalizeRequiredString(final String rawValue, final String fieldName) {
        if (rawValue == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        final String normalized = rawValue.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    /**
     * @brief 解析可空批次 ID（Parse Nullable Posting Batch ID）；
     *        Parse nullable posting-batch identifier.
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
     * @brief 批次记录载体（Posting Batch Record Carrier）；
     *        Posting batch record carrier.
     *
     * @param batchId 批次 ID（Batch ID）。
     * @param transactionId 交易 ID（Transaction ID）。
     * @param idempotencyKey 幂等键（Idempotency key）。
     * @param batchStatus 批次状态（Batch status）。
     * @param postedAt 入账时间（Posted time）。
     * @param createdAt 创建时间（Created time）。
     */
    private record PostingBatchRecord(
            PostingBatchId batchId,
            String transactionId,
            String idempotencyKey,
            PostingStatus batchStatus,
            java.time.Instant postedAt,
            java.time.Instant createdAt) {
    }
}
