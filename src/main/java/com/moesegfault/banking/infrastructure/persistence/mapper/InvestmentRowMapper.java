package com.moesegfault.banking.infrastructure.persistence.mapper;

import com.moesegfault.banking.domain.investment.Holding;
import com.moesegfault.banking.domain.investment.HoldingId;
import com.moesegfault.banking.domain.investment.InvestmentAccountId;
import com.moesegfault.banking.domain.investment.InvestmentOrder;
import com.moesegfault.banking.domain.investment.InvestmentOrderId;
import com.moesegfault.banking.domain.investment.InvestmentProduct;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.jdbc.core.RowMapper;

/**
 * @brief 投资行映射器（Investment Row Mapper）；
 *        Maps investment related rows to domain objects.
 */
public final class InvestmentRowMapper {

    /**
     * @brief 投资产品映射器（Investment Product Mapper）；
     *        Mapper for `investment_product` records.
     */
    public static final RowMapper<InvestmentProduct> INVESTMENT_PRODUCT = (resultSet, rowNum) ->
            InvestmentProduct.restore(
                    ProductId.of(resultSet.getString("product_id")),
                    ProductCode.of(resultSet.getString("product_code")),
                    resultSet.getString("product_name"),
                    ProductType.valueOf(resultSet.getString("product_type")),
                    CurrencyCode.of(resultSet.getString("currency_code")),
                    RiskLevel.valueOf(resultSet.getString("risk_level")),
                    resultSet.getString("issuer"),
                    ProductStatus.valueOf(resultSet.getString("status")));

    /**
     * @brief 投资订单映射器（Investment Order Mapper）；
     *        Mapper for joined investment order records.
     */
    public static final RowMapper<InvestmentOrder> INVESTMENT_ORDER = (resultSet, rowNum) -> {
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
                getInstant(resultSet, "trade_at"),
                getInstant(resultSet, "settlement_at"),
                deriveOrderStatus(
                        resultSet.getString("business_transaction_status"),
                        getInstant(resultSet, "settlement_at")));
    };

    /**
     * @brief 持仓映射器（Holding Mapper）；
     *        Mapper for `investment_holding` records.
     */
    public static final RowMapper<Holding> HOLDING = (resultSet, rowNum) -> Holding.restore(
            HoldingId.of(resultSet.getString("holding_id")),
            InvestmentAccountId.of(resultSet.getString("investment_account_id")),
            ProductId.of(resultSet.getString("product_id")),
            Quantity.of(resultSet.getBigDecimal("quantity")),
            resultSet.getBigDecimal("average_cost"),
            CurrencyCode.of(resultSet.getString("cost_currency_code")),
            resultSet.getBigDecimal("market_value"),
            CurrencyCode.of(resultSet.getString("valuation_currency_code")),
            resultSet.getBigDecimal("unrealized_pnl"),
            getInstant(resultSet, "updated_at"));

    /**
     * @brief 产品估值映射器（Product Valuation Mapper）；
     *        Mapper for `product_valuation` records.
     */
    public static final RowMapper<ProductValuation> PRODUCT_VALUATION = (resultSet, rowNum) -> {
        final CurrencyCode currencyCode = CurrencyCode.of(resultSet.getString("currency_code"));
        return ProductValuation.of(
                ProductId.of(resultSet.getString("product_id")),
                resultSet.getObject("valuation_date", LocalDate.class),
                NetAssetValue.of(resultSet.getBigDecimal("nav"), currencyCode));
    };

    /**
     * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
     *        Utility class should not be instantiated.
     */
    private InvestmentRowMapper() {
    }

    /**
     * @brief 推导订单状态（Derive Order Status）；
     *        Derives order status from business transaction and settlement status.
     *
     * @param businessTransactionStatus 业务交易状态（Business transaction status）。
     * @param settlementAt 结算时间（Settlement timestamp, nullable）。
     * @return 订单状态（Order status）。
     */
    private static OrderStatus deriveOrderStatus(
            final String businessTransactionStatus,
            final Instant settlementAt
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
