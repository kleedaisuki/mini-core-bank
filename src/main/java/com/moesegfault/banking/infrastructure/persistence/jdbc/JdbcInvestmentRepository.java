package com.moesegfault.banking.infrastructure.persistence.jdbc;

import com.moesegfault.banking.domain.investment.Holding;
import com.moesegfault.banking.domain.investment.HoldingId;
import com.moesegfault.banking.domain.investment.InvestmentAccountId;
import com.moesegfault.banking.domain.investment.InvestmentOrder;
import com.moesegfault.banking.domain.investment.InvestmentOrderId;
import com.moesegfault.banking.domain.investment.InvestmentProduct;
import com.moesegfault.banking.domain.investment.InvestmentRepository;
import com.moesegfault.banking.domain.investment.ProductCode;
import com.moesegfault.banking.domain.investment.ProductId;
import com.moesegfault.banking.domain.investment.ProductValuation;
import com.moesegfault.banking.infrastructure.persistence.mapper.InvestmentRowMapper;
import com.moesegfault.banking.infrastructure.persistence.sql.InvestmentSql;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @brief 投资仓储 JDBC 实现（JDBC Implementation of Investment Repository），对齐投资相关四张核心表；
 *        JDBC implementation of investment repository aligned with core investment tables.
 */
public final class JdbcInvestmentRepository implements InvestmentRepository {

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
    public JdbcInvestmentRepository(final DataSource dataSource) {
        this(new JdbcTemplate(Objects.requireNonNull(dataSource, "dataSource must not be null")));
    }

    /**
     * @brief 使用 JDBC 模板构造仓储（Construct Repository with JdbcTemplate）；
     *        Construct repository with JDBC template.
     *
     * @param jdbcTemplate JDBC 模板（JDBC template）。
     */
    public JdbcInvestmentRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveInvestmentProduct(final InvestmentProduct investmentProduct) {
        final InvestmentProduct normalized = Objects.requireNonNull(
                investmentProduct,
                "investmentProduct must not be null");
        jdbcTemplate.update(
                InvestmentSql.UPSERT_PRODUCT,
                normalized.productId().value(),
                normalized.productCode().value(),
                normalized.productName(),
                normalized.productType().name(),
                normalized.currencyCode().value(),
                normalized.riskLevel().name(),
                normalized.issuer(),
                normalized.productStatus().name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveInvestmentOrder(final InvestmentOrder investmentOrder) {
        final InvestmentOrder normalized = Objects.requireNonNull(
                investmentOrder,
                "investmentOrder must not be null");
        jdbcTemplate.update(
                InvestmentSql.UPSERT_ORDER,
                normalized.investmentOrderId().value(),
                normalized.investmentAccountId().value(),
                normalized.productId().value(),
                normalized.orderSide().name(),
                normalized.quantity().value(),
                normalized.price().value(),
                normalized.grossAmount().amount(),
                normalized.feeAmount().amount(),
                normalized.currencyCode().value(),
                JdbcRepositorySupport.toTimestamp(normalized.tradeAt()),
                JdbcRepositorySupport.toTimestamp(normalized.settlementAtOrNull()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveHolding(final Holding holding) {
        final Holding normalized = Objects.requireNonNull(holding, "holding must not be null");
        jdbcTemplate.update(
                InvestmentSql.UPSERT_HOLDING,
                normalized.holdingId().value(),
                normalized.investmentAccountId().value(),
                normalized.productId().value(),
                normalized.quantity().value(),
                normalized.averageCost(),
                normalized.costCurrencyCode().value(),
                normalized.marketValue(),
                normalized.valuationCurrencyCode().value(),
                normalized.unrealizedPnl(),
                JdbcRepositorySupport.toTimestamp(normalized.updatedAt()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveProductValuation(final ProductValuation productValuation) {
        final ProductValuation normalized = Objects.requireNonNull(
                productValuation,
                "productValuation must not be null");
        jdbcTemplate.update(
                InvestmentSql.UPSERT_VALUATION,
                normalized.productId().value(),
                normalized.valuationDate(),
                normalized.netAssetValue().value(),
                normalized.netAssetValue().currencyCode().value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<InvestmentProduct> findProductById(final ProductId productId) {
        final ProductId normalized = Objects.requireNonNull(productId, "productId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                InvestmentSql.FIND_PRODUCT_BY_ID,
                InvestmentRowMapper.INVESTMENT_PRODUCT,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<InvestmentProduct> findProductByCode(final ProductCode productCode) {
        final ProductCode normalized = Objects.requireNonNull(productCode, "productCode must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                InvestmentSql.FIND_PRODUCT_BY_CODE,
                InvestmentRowMapper.INVESTMENT_PRODUCT,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<InvestmentOrder> findOrderById(final InvestmentOrderId investmentOrderId) {
        final InvestmentOrderId normalized = Objects.requireNonNull(
                investmentOrderId,
                "investmentOrderId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                InvestmentSql.FIND_ORDER_BY_ID,
                InvestmentRowMapper.INVESTMENT_ORDER,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Holding> findHoldingById(final HoldingId holdingId) {
        final HoldingId normalized = Objects.requireNonNull(holdingId, "holdingId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                InvestmentSql.FIND_HOLDING_BY_ID,
                InvestmentRowMapper.HOLDING,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Holding> findHoldingByAccountAndProduct(
            final InvestmentAccountId investmentAccountId,
            final ProductId productId
    ) {
        final InvestmentAccountId normalizedAccountId = Objects.requireNonNull(
                investmentAccountId,
                "investmentAccountId must not be null");
        final ProductId normalizedProductId = Objects.requireNonNull(productId, "productId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                InvestmentSql.FIND_HOLDING_BY_ACCOUNT_AND_PRODUCT,
                InvestmentRowMapper.HOLDING,
                normalizedAccountId.value(),
                normalizedProductId.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Holding> listHoldingsByAccountId(final InvestmentAccountId investmentAccountId) {
        final InvestmentAccountId normalized = Objects.requireNonNull(
                investmentAccountId,
                "investmentAccountId must not be null");
        return jdbcTemplate.query(
                InvestmentSql.LIST_HOLDINGS_BY_ACCOUNT_ID,
                InvestmentRowMapper.HOLDING,
                normalized.value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ProductValuation> findProductValuationByDate(
            final ProductId productId,
            final LocalDate valuationDate
    ) {
        final ProductId normalizedProductId = Objects.requireNonNull(productId, "productId must not be null");
        final LocalDate normalizedValuationDate = Objects.requireNonNull(
                valuationDate,
                "valuationDate must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                InvestmentSql.FIND_VALUATION_BY_DATE,
                InvestmentRowMapper.PRODUCT_VALUATION,
                normalizedProductId.value(),
                normalizedValuationDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ProductValuation> findLatestProductValuation(final ProductId productId) {
        final ProductId normalized = Objects.requireNonNull(productId, "productId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                InvestmentSql.FIND_LATEST_VALUATION,
                InvestmentRowMapper.PRODUCT_VALUATION,
                normalized.value());
    }
}
