package com.moesegfault.banking.infrastructure.persistence.jdbc;

import com.moesegfault.banking.domain.card.CardExpiry;
import com.moesegfault.banking.domain.card.CardId;
import com.moesegfault.banking.domain.card.CardNumber;
import com.moesegfault.banking.domain.card.CardRepository;
import com.moesegfault.banking.domain.card.CardRole;
import com.moesegfault.banking.domain.card.CardStatus;
import com.moesegfault.banking.domain.card.CreditCard;
import com.moesegfault.banking.domain.card.CreditCardAccountId;
import com.moesegfault.banking.domain.card.CustomerId;
import com.moesegfault.banking.domain.card.DebitCard;
import com.moesegfault.banking.domain.card.DebitCardBinding;
import com.moesegfault.banking.domain.card.FxAccountId;
import com.moesegfault.banking.domain.card.SavingsAccountId;
import com.moesegfault.banking.domain.card.SupplementaryDebitCard;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * @brief 卡仓储 JDBC 实现（JDBC Implementation of Card Repository），对齐 `debit_card/supplementary_debit_card/credit_card`；
 *        JDBC implementation of card repository aligned with card tables.
 */
public final class JdbcCardRepository implements CardRepository {

    /**
     * @brief 扣账卡 upsert SQL（Debit Card Upsert SQL）；
     *        Debit-card upsert SQL.
     */
    private static final String UPSERT_DEBIT_SQL = """
            INSERT INTO debit_card (
                card_id,
                card_no,
                holder_customer_id,
                savings_account_id,
                fx_account_id,
                card_status,
                issued_at,
                expired_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (card_id) DO UPDATE SET
                card_no = EXCLUDED.card_no,
                holder_customer_id = EXCLUDED.holder_customer_id,
                savings_account_id = EXCLUDED.savings_account_id,
                fx_account_id = EXCLUDED.fx_account_id,
                card_status = EXCLUDED.card_status,
                issued_at = EXCLUDED.issued_at,
                expired_at = EXCLUDED.expired_at
            """;

    /**
     * @brief 扣账附属卡 upsert SQL（Supplementary Debit Card Upsert SQL）；
     *        Supplementary-debit-card upsert SQL.
     */
    private static final String UPSERT_SUPPLEMENTARY_DEBIT_SQL = """
            INSERT INTO supplementary_debit_card (
                supplementary_card_id,
                card_no,
                holder_customer_id,
                primary_debit_card_id,
                card_status,
                issued_at,
                expired_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (supplementary_card_id) DO UPDATE SET
                card_no = EXCLUDED.card_no,
                holder_customer_id = EXCLUDED.holder_customer_id,
                primary_debit_card_id = EXCLUDED.primary_debit_card_id,
                card_status = EXCLUDED.card_status,
                issued_at = EXCLUDED.issued_at,
                expired_at = EXCLUDED.expired_at
            """;

    /**
     * @brief 信用卡 upsert SQL（Credit Card Upsert SQL）；
     *        Credit-card upsert SQL.
     */
    private static final String UPSERT_CREDIT_SQL = """
            INSERT INTO credit_card (
                credit_card_id,
                card_no,
                holder_customer_id,
                credit_card_account_id,
                card_role,
                primary_credit_card_id,
                card_status,
                issued_at,
                expired_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (credit_card_id) DO UPDATE SET
                card_no = EXCLUDED.card_no,
                holder_customer_id = EXCLUDED.holder_customer_id,
                credit_card_account_id = EXCLUDED.credit_card_account_id,
                card_role = EXCLUDED.card_role,
                primary_credit_card_id = EXCLUDED.primary_credit_card_id,
                card_status = EXCLUDED.card_status,
                issued_at = EXCLUDED.issued_at,
                expired_at = EXCLUDED.expired_at
            """;

    /**
     * @brief JDBC 模板（JDBC Template）；
     *        JDBC template.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * @brief 扣账卡映射器（Debit Card Row Mapper）；
     *        Debit card row mapper.
     */
    private final RowMapper<DebitCard> debitCardMapper = (resultSet, rowNum) -> DebitCard.restore(
            CardId.of(resultSet.getString("card_id")),
            CardNumber.of(resultSet.getString("card_no")),
            CustomerId.of(resultSet.getString("holder_customer_id")),
            DebitCardBinding.of(
                    SavingsAccountId.of(resultSet.getString("savings_account_id")),
                    FxAccountId.of(resultSet.getString("fx_account_id"))),
            CardStatus.valueOf(resultSet.getString("card_status")),
            CardExpiry.of(
                    JdbcRepositorySupport.getInstant(resultSet, "issued_at"),
                    JdbcRepositorySupport.getInstant(resultSet, "expired_at")));

    /**
     * @brief 扣账附属卡映射器（Supplementary Debit Card Row Mapper）；
     *        Supplementary debit card row mapper.
     */
    private final RowMapper<SupplementaryDebitCard> supplementaryDebitCardMapper = (resultSet, rowNum) ->
            SupplementaryDebitCard.restore(
                    CardId.of(resultSet.getString("supplementary_card_id")),
                    CardNumber.of(resultSet.getString("card_no")),
                    CustomerId.of(resultSet.getString("holder_customer_id")),
                    CardId.of(resultSet.getString("primary_debit_card_id")),
                    CardStatus.valueOf(resultSet.getString("card_status")),
                    CardExpiry.of(
                            JdbcRepositorySupport.getInstant(resultSet, "issued_at"),
                            JdbcRepositorySupport.getInstant(resultSet, "expired_at")));

    /**
     * @brief 信用卡映射器（Credit Card Row Mapper）；
     *        Credit card row mapper.
     */
    private final RowMapper<CreditCard> creditCardMapper = (resultSet, rowNum) -> CreditCard.restore(
            CardId.of(resultSet.getString("credit_card_id")),
            CardNumber.of(resultSet.getString("card_no")),
            CustomerId.of(resultSet.getString("holder_customer_id")),
            CreditCardAccountId.of(resultSet.getString("credit_card_account_id")),
            CardRole.fromDatabaseValue(resultSet.getString("card_role")),
            nullableCardId(resultSet.getString("primary_credit_card_id")),
            CardStatus.valueOf(resultSet.getString("card_status")),
            CardExpiry.of(
                    JdbcRepositorySupport.getInstant(resultSet, "issued_at"),
                    JdbcRepositorySupport.getInstant(resultSet, "expired_at")));

    /**
     * @brief 使用数据源构造仓储（Construct Repository with DataSource）；
     *        Construct repository with datasource.
     *
     * @param dataSource 数据源（Data source）。
     */
    public JdbcCardRepository(final DataSource dataSource) {
        this(new JdbcTemplate(Objects.requireNonNull(dataSource, "dataSource must not be null")));
    }

    /**
     * @brief 使用 JDBC 模板构造仓储（Construct Repository with JdbcTemplate）；
     *        Construct repository with JDBC template.
     *
     * @param jdbcTemplate JDBC 模板（JDBC template）。
     */
    public JdbcCardRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveDebitCard(final DebitCard debitCard) {
        final DebitCard normalized = Objects.requireNonNull(debitCard, "debitCard must not be null");
        jdbcTemplate.update(
                UPSERT_DEBIT_SQL,
                normalized.cardId().value(),
                normalized.cardNumber().value(),
                normalized.holderCustomerId().value(),
                normalized.binding().savingsAccountId().value(),
                normalized.binding().fxAccountId().value(),
                normalized.cardStatus().name(),
                JdbcRepositorySupport.toTimestamp(normalized.issuedAt()),
                JdbcRepositorySupport.toTimestamp(normalized.expiredAtOrNull()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSupplementaryDebitCard(final SupplementaryDebitCard supplementaryDebitCard) {
        final SupplementaryDebitCard normalized = Objects.requireNonNull(
                supplementaryDebitCard,
                "supplementaryDebitCard must not be null");
        jdbcTemplate.update(
                UPSERT_SUPPLEMENTARY_DEBIT_SQL,
                normalized.supplementaryCardId().value(),
                normalized.cardNumber().value(),
                normalized.holderCustomerId().value(),
                normalized.primaryDebitCardId().value(),
                normalized.cardStatus().name(),
                JdbcRepositorySupport.toTimestamp(normalized.issuedAt()),
                JdbcRepositorySupport.toTimestamp(normalized.expiredAtOrNull()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCreditCard(final CreditCard creditCard) {
        final CreditCard normalized = Objects.requireNonNull(creditCard, "creditCard must not be null");
        jdbcTemplate.update(
                UPSERT_CREDIT_SQL,
                normalized.creditCardId().value(),
                normalized.cardNumber().value(),
                normalized.holderCustomerId().value(),
                normalized.creditCardAccountId().value(),
                normalized.cardRole().databaseValue(),
                normalized.primaryCreditCardIdOrNull() == null ? null : normalized.primaryCreditCardIdOrNull().value(),
                normalized.cardStatus().name(),
                JdbcRepositorySupport.toTimestamp(normalized.issuedAt()),
                JdbcRepositorySupport.toTimestamp(normalized.expiredAtOrNull()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<DebitCard> findDebitCardById(final CardId cardId) {
        final CardId normalized = Objects.requireNonNull(cardId, "cardId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                "SELECT * FROM debit_card WHERE card_id = ?",
                debitCardMapper,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SupplementaryDebitCard> findSupplementaryDebitCardById(final CardId supplementaryCardId) {
        final CardId normalized = Objects.requireNonNull(
                supplementaryCardId,
                "supplementaryCardId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                "SELECT * FROM supplementary_debit_card WHERE supplementary_card_id = ?",
                supplementaryDebitCardMapper,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CreditCard> findCreditCardById(final CardId creditCardId) {
        final CardId normalized = Objects.requireNonNull(creditCardId, "creditCardId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                "SELECT * FROM credit_card WHERE credit_card_id = ?",
                creditCardMapper,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<DebitCard> findDebitCardByCardNumber(final CardNumber cardNumber) {
        final CardNumber normalized = Objects.requireNonNull(cardNumber, "cardNumber must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                "SELECT * FROM debit_card WHERE card_no = ?",
                debitCardMapper,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CreditCard> findCreditCardByCardNumber(final CardNumber cardNumber) {
        final CardNumber normalized = Objects.requireNonNull(cardNumber, "cardNumber must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                "SELECT * FROM credit_card WHERE card_no = ?",
                creditCardMapper,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsAnyByCardNumber(final CardNumber cardNumber) {
        final CardNumber normalized = Objects.requireNonNull(cardNumber, "cardNumber must not be null");
        final Boolean exists = jdbcTemplate.queryForObject(
                """
                        SELECT EXISTS (
                            SELECT 1 FROM debit_card WHERE card_no = ?
                            UNION ALL
                            SELECT 1 FROM supplementary_debit_card WHERE card_no = ?
                            UNION ALL
                            SELECT 1 FROM credit_card WHERE card_no = ?
                        )
                        """,
                Boolean.class,
                normalized.value(),
                normalized.value(),
                normalized.value());
        return Boolean.TRUE.equals(exists);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countSupplementaryDebitCardsByPrimary(final CardId primaryDebitCardId) {
        final CardId normalized = Objects.requireNonNull(
                primaryDebitCardId,
                "primaryDebitCardId must not be null");
        final Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM supplementary_debit_card WHERE primary_debit_card_id = ?",
                Long.class,
                normalized.value());
        return count == null ? 0L : count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CreditCard> findCreditCardsByAccountId(final CreditCardAccountId creditCardAccountId) {
        final CreditCardAccountId normalized = Objects.requireNonNull(
                creditCardAccountId,
                "creditCardAccountId must not be null");
        return jdbcTemplate.query(
                "SELECT * FROM credit_card WHERE credit_card_account_id = ? ORDER BY issued_at DESC",
                creditCardMapper,
                normalized.value());
    }

    /**
     * @brief 解析可空卡片 ID（Parse Nullable Card ID）；
     *        Parse nullable card ID.
     *
     * @param rawValue 原始 ID（Raw ID）。
     * @return 卡片 ID 或 null（Card ID or null）。
     */
    private static CardId nullableCardId(final String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        return CardId.of(rawValue);
    }
}
