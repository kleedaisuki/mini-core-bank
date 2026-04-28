package com.moesegfault.banking.infrastructure.persistence.jdbc;

import com.moesegfault.banking.domain.card.CardId;
import com.moesegfault.banking.domain.card.CardNumber;
import com.moesegfault.banking.domain.card.CardRepository;
import com.moesegfault.banking.domain.card.CreditCard;
import com.moesegfault.banking.domain.card.CreditCardAccountId;
import com.moesegfault.banking.domain.card.DebitCard;
import com.moesegfault.banking.domain.card.SupplementaryDebitCard;
import com.moesegfault.banking.infrastructure.persistence.mapper.CardRowMapper;
import com.moesegfault.banking.infrastructure.persistence.sql.CardSql;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @brief 卡仓储 JDBC 实现（JDBC Implementation of Card Repository），对齐 `debit_card/supplementary_debit_card/credit_card`；
 *        JDBC implementation of card repository aligned with card tables.
 */
public final class JdbcCardRepository implements CardRepository {

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
                CardSql.UPSERT_DEBIT,
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
                CardSql.UPSERT_SUPPLEMENTARY_DEBIT,
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
                CardSql.UPSERT_CREDIT,
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
                CardSql.FIND_DEBIT_BY_ID,
                CardRowMapper.DEBIT_CARD,
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
                CardSql.FIND_SUPPLEMENTARY_DEBIT_BY_ID,
                CardRowMapper.SUPPLEMENTARY_DEBIT_CARD,
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
                CardSql.FIND_CREDIT_BY_ID,
                CardRowMapper.CREDIT_CARD,
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
                CardSql.FIND_DEBIT_BY_CARD_NUMBER,
                CardRowMapper.DEBIT_CARD,
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
                CardSql.FIND_CREDIT_BY_CARD_NUMBER,
                CardRowMapper.CREDIT_CARD,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsAnyByCardNumber(final CardNumber cardNumber) {
        final CardNumber normalized = Objects.requireNonNull(cardNumber, "cardNumber must not be null");
        final Boolean exists = jdbcTemplate.queryForObject(
                CardSql.EXISTS_ANY_BY_CARD_NUMBER,
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
                CardSql.COUNT_SUPPLEMENTARY_BY_PRIMARY,
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
                CardSql.FIND_CREDIT_BY_ACCOUNT_ID,
                CardRowMapper.CREDIT_CARD,
                normalized.value());
    }
}
