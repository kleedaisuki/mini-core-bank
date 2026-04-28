package com.moesegfault.banking.infrastructure.persistence.jdbc;

import com.moesegfault.banking.domain.investment.Holding;
import com.moesegfault.banking.domain.investment.HoldingId;
import com.moesegfault.banking.domain.investment.InvestmentAccountId;
import com.moesegfault.banking.domain.investment.InvestmentOrder;
import com.moesegfault.banking.domain.investment.InvestmentOrderId;
import com.moesegfault.banking.domain.investment.InvestmentProduct;
import com.moesegfault.banking.domain.investment.InvestmentRepository;
import com.moesegfault.banking.domain.investment.NetAssetValue;
import com.moesegfault.banking.domain.investment.OrderSide;
import com.moesegfault.banking.domain.investment.OrderStatus;
import com.moesegfault.banking.domain.investment.ProductCode;
import com.moesegfault.banking.domain.investment.ProductId;
import com.moesegfault.banking.domain.investment.ProductStatus;
import com.moesegfault.banking.domain.investment.ProductType;
import com.moesegfault.banking.domain.investment.ProductValuation;
import com.moesegfault.banking.domain.investment.Quantity;
import com.moesegfault.banking.domain.investment.RiskLevel;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * @brief 投资仓储 JDBC 实现（JDBC Implementation of Investment Repository），对齐投资相关四张核心表；
 *        JDBC implementation of investment repository aligned with core investment tables.
 */
public final class JdbcInvestmentRepository implements InvestmentRepository {

    /**
     * @brief 投资产品 upsert SQL（Investment Product Upsert SQL）；
     *        Investment-product upsert SQL.
     */
    private static final String UPSERT_PRODUCT_SQL = """
            INSERT INTO investment_product (
                product_id,
                product_code,
                product_name,
                product_type,
                currency_code,
                risk_level,
                issuer,
                status
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (product_id) DO UPDATE SET
                product_code = EXCLUDED.product_code,
                product_name = EXCLUDED.product_name,
                product_type = EXCLUDED.product_type,
                currency_code = EXCLUDED.currency_code,
                risk_level = EXCLUDED.risk_level,
                issuer = EXCLUDED.issuer,
                status = EXCLUDED.status
            """;

    /**
     * @brief 投资订单 upsert SQL（Investment Order Upsert SQL）；
     *        Investment-order upsert SQL.
     */
    private static final String UPSERT_ORDER_SQL = """
            INSERT INTO investment_order_detail (
                transaction_id,
                investment_account_id,
                product_id,
                order_side,
                quantity,
                price,
                gross_amount,
                fee_amount,
                currency_code,
                trade_at,
                settlement_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (transaction_id) DO UPDATE SET
                investment_account_id = EXCLUDED.investment_account_id,
                product_id = EXCLUDED.product_id,
                order_side = EXCLUDED.order_side,
                quantity = EXCLUDED.quantity,
                price = EXCLUDED.price,
                gross_amount = EXCLUDED.gross_amount,
                fee_amount = EXCLUDED.fee_amount,
                currency_code = EXCLUDED.currency_code,
                trade_at = EXCLUDED.trade_at,
                settlement_at = EXCLUDED.settlement_at
            """;

    /**
     * @brief 持仓 upsert SQL（Holding Upsert SQL）；
     *        Holding upsert SQL.
     */
    private static final String UPSERT_HOLDING_SQL = """
            INSERT INTO investment_holding (
                holding_id,
                investment_account_id,
                product_id,
                quantity,
                average_cost,
                cost_currency_code,
                market_value,
                valuation_currency_code,
                unrealized_pnl,
                updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (holding_id) DO UPDATE SET
                investment_account_id = EXCLUDED.investment_account_id,
                product_id = EXCLUDED.product_id,
                quantity = EXCLUDED.quantity,
                average_cost = EXCLUDED.average_cost,
                cost_currency_code = EXCLUDED.cost_currency_code,
                market_value = EXCLUDED.market_value,
                valuation_currency_code = EXCLUDED.valuation_currency_code,
                unrealized_pnl = EXCLUDED.unrealized_pnl,
                updated_at = EXCLUDED.updated_at
            """;

    /**
     * @brief 产品估值 upsert SQL（Product Valuation Upsert SQL）；
     *        Product-valuation upsert SQL.
     */
    private static final String UPSERT_VALUATION_SQL = """
            INSERT INTO product_valuation (
                product_id,
                valuation_date,
                nav,
                currency_code
            ) VALUES (?, ?, ?, ?)
            ON CONFLICT (product_id, valuation_date) DO UPDATE SET
                nav = EXCLUDED.nav,
                currency_code = EXCLUDED.currency_code
            """;

    /**
     * @brief JDBC 模板（JDBC Template）；
     *        JDBC template.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * @brief 产品映射器（Investment Product Row Mapper）；
     *        Investment product row mapper.
     */
    private final RowMapper<InvestmentProduct> productMapper = (resultSet, rowNum) -> InvestmentProduct.restore(
            ProductId.of(resultSet.getString("product_id")),
            ProductCode.of(resultSet.getString("product_code")),
            resultSet.getString("product_name"),
            ProductType.valueOf(resultSet.getString("product_type")),
            CurrencyCode.of(resultSet.getString("currency_code")),
            RiskLevel.valueOf(resultSet.getString("risk_level")),
            resultSet.getString("issuer"),
            ProductStatus.valueOf(resultSet.getString("status")));

    /**
     * @brief 订单映射器（Investment Order Row Mapper）；
     *        Investment order row mapper.
     */
    private final RowMapper<InvestmentOrder> orderMapper = (resultSet, rowNum) -> {
        final CurrencyCode currencyCode = CurrencyCode.of(resultSet.getString("currency_code"));
        return InvestmentOrder.restore(
                InvestmentOrderId.of(resultSet.getString("transaction_id")),
                InvestmentAccountId.of(resultSet.getString("investment_account_id")),
                ProductId.of(resultSet.getString("product_id")),
                OrderSide.valueOf(resultSet.getString("order_side")),
                Quantity.of(resultSet.getBigDecimal("quantity")),
                NetAssetValue.of(resultSet.getBigDecimal("price"), currencyCode),
                Money.of(currencyCode, resultSet.getBigDecimal("gross_amount")),
                Money.of(currencyCode, resultSet.getBigDecimal("fee_amount")),
                JdbcRepositorySupport.getInstant(resultSet, "trade_at"),
                JdbcRepositorySupport.getInstant(resultSet, "settlement_at"),
                deriveOrderStatus(
                        resultSet.getString("business_transaction_status"),
                        JdbcRepositorySupport.getInstant(resultSet, "settlement_at")));
    };

    /**
     * @brief 持仓映射器（Holding Row Mapper）；
     *        Holding row mapper.
     */
    private final RowMapper<Holding> holdingMapper = (resultSet, rowNum) -> Holding.restore(
            HoldingId.of(resultSet.getString("holding_id")),
            InvestmentAccountId.of(resultSet.getString("investment_account_id")),
            ProductId.of(resultSet.getString("product_id")),
            Quantity.of(resultSet.getBigDecimal("quantity")),
            resultSet.getBigDecimal("average_cost"),
            CurrencyCode.of(resultSet.getString("cost_currency_code")),
            resultSet.getBigDecimal("market_value"),
            CurrencyCode.of(resultSet.getString("valuation_currency_code")),
            resultSet.getBigDecimal("unrealized_pnl"),
            JdbcRepositorySupport.getInstant(resultSet, "updated_at"));

    /**
     * @brief 估值映射器（Product Valuation Row Mapper）；
     *        Product valuation row mapper.
     */
    private final RowMapper<ProductValuation> valuationMapper = (resultSet, rowNum) -> {
        final CurrencyCode currencyCode = CurrencyCode.of(resultSet.getString("currency_code"));
        return ProductValuation.of(
                ProductId.of(resultSet.getString("product_id")),
                resultSet.getObject("valuation_date", LocalDate.class),
                NetAssetValue.of(resultSet.getBigDecimal("nav"), currencyCode));
    };

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
                UPSERT_PRODUCT_SQL,
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
                UPSERT_ORDER_SQL,
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
                UPSERT_HOLDING_SQL,
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
                UPSERT_VALUATION_SQL,
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
                "SELECT * FROM investment_product WHERE product_id = ?",
                productMapper,
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
                "SELECT * FROM investment_product WHERE product_code = ?",
                productMapper,
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
                """
                        SELECT
                            iod.*,
                            bt.transaction_status AS business_transaction_status
                        FROM investment_order_detail iod
                        JOIN business_transaction bt ON bt.transaction_id = iod.transaction_id
                        WHERE iod.transaction_id = ?
                        """,
                orderMapper,
                normalized.value());
    }

    /**
     * @brief 由业务交易状态与结算时间推导订单状态（Derive Order Status from Business Transaction Status and Settlement Time）；
     *        Derive order status from business transaction status and settlement timestamp.
     *
     * @param businessTransactionStatus 业务交易状态（Business transaction status）。
     * @param settlementAt 结算时间（Settlement time, nullable）。
     * @return 订单状态（Order status）。
     */
    private static OrderStatus deriveOrderStatus(
            final String businessTransactionStatus,
            final java.time.Instant settlementAt
    ) {
        if ("FAILED".equalsIgnoreCase(businessTransactionStatus)) {
            return OrderStatus.FAILED;
        }
        if ("REVERSED".equalsIgnoreCase(businessTransactionStatus)) {
            return OrderStatus.CANCELLED;
        }
        if (settlementAt != null || "SUCCESS".equalsIgnoreCase(businessTransactionStatus)) {
            return OrderStatus.SETTLED;
        }
        return OrderStatus.PLACED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Holding> findHoldingById(final HoldingId holdingId) {
        final HoldingId normalized = Objects.requireNonNull(holdingId, "holdingId must not be null");
        return JdbcRepositorySupport.queryOptional(
                jdbcTemplate,
                "SELECT * FROM investment_holding WHERE holding_id = ?",
                holdingMapper,
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
                "SELECT * FROM investment_holding WHERE investment_account_id = ? AND product_id = ?",
                holdingMapper,
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
                "SELECT * FROM investment_holding WHERE investment_account_id = ? ORDER BY updated_at DESC",
                holdingMapper,
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
                "SELECT * FROM product_valuation WHERE product_id = ? AND valuation_date = ?",
                valuationMapper,
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
                """
                        SELECT *
                        FROM product_valuation
                        WHERE product_id = ?
                        ORDER BY valuation_date DESC
                        LIMIT 1
                        """,
                valuationMapper,
                normalized.value());
    }
}
