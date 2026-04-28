package com.moesegfault.banking.infrastructure.persistence.jdbc;

import com.moesegfault.banking.domain.ledger.Balance;
import com.moesegfault.banking.domain.ledger.LedgerEntry;
import com.moesegfault.banking.domain.ledger.LedgerRepository;
import com.moesegfault.banking.domain.ledger.PostingBatch;
import com.moesegfault.banking.domain.ledger.PostingBatchId;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.infrastructure.persistence.mapper.LedgerRowMapper;
import com.moesegfault.banking.infrastructure.persistence.mapper.LedgerRowMapper.PostingBatchSnapshot;
import com.moesegfault.banking.infrastructure.persistence.sql.LedgerSql;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @brief 账务仓储 JDBC 实现（JDBC Implementation of Ledger Repository），对齐 `posting_batch/account_balance/account_entry`；
 *        JDBC implementation of ledger repository aligned with ledger tables.
 */
public final class JdbcLedgerRepository implements LedgerRepository {

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
                LedgerSql.UPSERT_BALANCE,
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
                LedgerSql.UPSERT_ENTRY,
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
                LedgerSql.UPSERT_ENTRY,
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
                LedgerSql.UPSERT_BATCH,
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
                LedgerSql.FIND_BALANCE,
                LedgerRowMapper.BALANCE,
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
                LedgerSql.LIST_BALANCES_BY_ACCOUNT_ID,
                LedgerRowMapper.BALANCE,
                normalized);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LedgerEntry> listEntriesByTransactionId(final String transactionId) {
        final String normalized = normalizeRequiredString(transactionId, "transactionId");
        return jdbcTemplate.query(
                LedgerSql.LIST_ENTRIES_BY_TRANSACTION_ID,
                LedgerRowMapper.LEDGER_ENTRY,
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
                LedgerSql.LIST_RECENT_ENTRIES_BY_ACCOUNT_ID,
                LedgerRowMapper.LEDGER_ENTRY,
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
        final Optional<PostingBatchSnapshot> batchSnapshot = JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                LedgerSql.FIND_POSTING_BATCH_PREFIX + whereClause,
                LedgerRowMapper.POSTING_BATCH_SNAPSHOT,
                argument);
        if (batchSnapshot.isEmpty()) {
            return Optional.empty();
        }
        final PostingBatchSnapshot snapshot = batchSnapshot.get();
        final List<LedgerEntry> entries = jdbcTemplate.query(
                LedgerSql.LIST_ENTRIES_BY_BATCH_ID,
                LedgerRowMapper.LEDGER_ENTRY,
                snapshot.batchId().value());
        return Optional.of(PostingBatch.restore(
                snapshot.batchId(),
                snapshot.transactionId(),
                snapshot.idempotencyKey(),
                snapshot.batchStatus(),
                snapshot.postedAt(),
                snapshot.createdAt(),
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
}
