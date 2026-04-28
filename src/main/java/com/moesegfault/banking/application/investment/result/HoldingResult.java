package com.moesegfault.banking.application.investment.result;

import com.moesegfault.banking.domain.investment.Holding;
import com.moesegfault.banking.domain.investment.InvestmentProduct;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 持仓结果视图（Holding Result View）；
 *        Read model for investment holding output.
 */
public final class HoldingResult {

    /**
     * @brief 持仓 ID（Holding ID）；
     *        Holding identifier string.
     */
    private final String holdingId;

    /**
     * @brief 投资账户 ID（Investment Account ID）；
     *        Investment account ID string.
     */
    private final String investmentAccountId;

    /**
     * @brief 产品 ID（Product ID）；
     *        Product ID string.
     */
    private final String productId;

    /**
     * @brief 产品代码（可空）（Product Code, Nullable）；
     *        Product code, nullable when details not loaded.
     */
    private final String productCodeOrNull;

    /**
     * @brief 产品名称（可空）（Product Name, Nullable）；
     *        Product name, nullable when details not loaded.
     */
    private final String productNameOrNull;

    /**
     * @brief 产品类型（可空）（Product Type, Nullable）；
     *        Product type, nullable when details not loaded.
     */
    private final String productTypeOrNull;

    /**
     * @brief 份额（Quantity）；
     *        Holding quantity.
     */
    private final BigDecimal quantity;

    /**
     * @brief 平均成本（Average Cost）；
     *        Average unit cost.
     */
    private final BigDecimal averageCost;

    /**
     * @brief 成本币种（Cost Currency）；
     *        Cost currency code.
     */
    private final String costCurrencyCode;

    /**
     * @brief 市值（Market Value）；
     *        Mark-to-market value.
     */
    private final BigDecimal marketValue;

    /**
     * @brief 估值币种（Valuation Currency）；
     *        Valuation currency code.
     */
    private final String valuationCurrencyCode;

    /**
     * @brief 未实现盈亏（Unrealized PnL）；
     *        Unrealized profit and loss.
     */
    private final BigDecimal unrealizedPnl;

    /**
     * @brief 更新时间（Updated Timestamp）；
     *        Last updated timestamp.
     */
    private final Instant updatedAt;

    /**
     * @brief 构造持仓结果（Construct Holding Result）；
     *        Construct holding result view.
     *
     * @param holdingId            持仓 ID（Holding ID）。
     * @param investmentAccountId  投资账户 ID（Investment account ID）。
     * @param productId            产品 ID（Product ID）。
     * @param productCodeOrNull    产品代码（Product code, nullable）。
     * @param productNameOrNull    产品名称（Product name, nullable）。
     * @param productTypeOrNull    产品类型（Product type, nullable）。
     * @param quantity             份额（Quantity）。
     * @param averageCost          平均成本（Average cost）。
     * @param costCurrencyCode     成本币种（Cost currency）。
     * @param marketValue          市值（Market value）。
     * @param valuationCurrencyCode 估值币种（Valuation currency）。
     * @param unrealizedPnl        未实现盈亏（Unrealized PnL）。
     * @param updatedAt            更新时间（Updated timestamp）。
     */
    public HoldingResult(
            final String holdingId,
            final String investmentAccountId,
            final String productId,
            final String productCodeOrNull,
            final String productNameOrNull,
            final String productTypeOrNull,
            final BigDecimal quantity,
            final BigDecimal averageCost,
            final String costCurrencyCode,
            final BigDecimal marketValue,
            final String valuationCurrencyCode,
            final BigDecimal unrealizedPnl,
            final Instant updatedAt
    ) {
        this.holdingId = Objects.requireNonNull(holdingId, "holdingId must not be null");
        this.investmentAccountId = Objects.requireNonNull(investmentAccountId, "investmentAccountId must not be null");
        this.productId = Objects.requireNonNull(productId, "productId must not be null");
        this.productCodeOrNull = productCodeOrNull;
        this.productNameOrNull = productNameOrNull;
        this.productTypeOrNull = productTypeOrNull;
        this.quantity = Objects.requireNonNull(quantity, "quantity must not be null");
        this.averageCost = Objects.requireNonNull(averageCost, "averageCost must not be null");
        this.costCurrencyCode = Objects.requireNonNull(costCurrencyCode, "costCurrencyCode must not be null");
        this.marketValue = Objects.requireNonNull(marketValue, "marketValue must not be null");
        this.valuationCurrencyCode = Objects.requireNonNull(
                valuationCurrencyCode,
                "valuationCurrencyCode must not be null");
        this.unrealizedPnl = Objects.requireNonNull(unrealizedPnl, "unrealizedPnl must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    /**
     * @brief 从持仓实体映射结果（Map from Holding Entity）；
     *        Map result from holding entity without product details.
     *
     * @param holding 持仓实体（Holding entity）。
     * @return 持仓结果（Holding result）。
     */
    public static HoldingResult fromHolding(final Holding holding) {
        return fromHoldingAndProduct(holding, null);
    }

    /**
     * @brief 从持仓与产品映射结果（Map from Holding and Product）；
     *        Map result from holding and optional product detail.
     *
     * @param holding 持仓实体（Holding entity）。
     * @param product 产品实体（Product entity, nullable）。
     * @return 持仓结果（Holding result）。
     */
    public static HoldingResult fromHoldingAndProduct(
            final Holding holding,
            final InvestmentProduct product
    ) {
        final Holding normalizedHolding = Objects.requireNonNull(holding, "holding must not be null");
        final String productCode = product == null ? null : product.productCode().value();
        final String productName = product == null ? null : product.productName();
        final String productType = product == null ? null : product.productType().name();
        return new HoldingResult(
                normalizedHolding.holdingId().value(),
                normalizedHolding.investmentAccountId().value(),
                normalizedHolding.productId().value(),
                productCode,
                productName,
                productType,
                normalizedHolding.quantity().value(),
                normalizedHolding.averageCost(),
                normalizedHolding.costCurrencyCode().value(),
                normalizedHolding.marketValue(),
                normalizedHolding.valuationCurrencyCode().value(),
                normalizedHolding.unrealizedPnl(),
                normalizedHolding.updatedAt());
    }

    /**
     * @brief 返回持仓 ID（Return Holding ID）；
     *        Return holding ID.
     *
     * @return 持仓 ID（Holding ID）。
     */
    public String holdingId() {
        return holdingId;
    }

    /**
     * @brief 返回投资账户 ID（Return Investment Account ID）；
     *        Return investment account ID.
     *
     * @return 投资账户 ID（Investment account ID）。
     */
    public String investmentAccountId() {
        return investmentAccountId;
    }

    /**
     * @brief 返回产品 ID（Return Product ID）；
     *        Return product ID.
     *
     * @return 产品 ID（Product ID）。
     */
    public String productId() {
        return productId;
    }

    /**
     * @brief 返回产品代码（可空）（Return Product Code, Nullable）；
     *        Return product code, nullable.
     *
     * @return 产品代码或 null（Product code or null）。
     */
    public String productCodeOrNull() {
        return productCodeOrNull;
    }

    /**
     * @brief 返回产品名称（可空）（Return Product Name, Nullable）；
     *        Return product name, nullable.
     *
     * @return 产品名称或 null（Product name or null）。
     */
    public String productNameOrNull() {
        return productNameOrNull;
    }

    /**
     * @brief 返回产品类型（可空）（Return Product Type, Nullable）；
     *        Return product type, nullable.
     *
     * @return 产品类型或 null（Product type or null）。
     */
    public String productTypeOrNull() {
        return productTypeOrNull;
    }

    /**
     * @brief 返回份额（Return Quantity）；
     *        Return quantity.
     *
     * @return 份额（Quantity）。
     */
    public BigDecimal quantity() {
        return quantity;
    }

    /**
     * @brief 返回平均成本（Return Average Cost）；
     *        Return average cost.
     *
     * @return 平均成本（Average cost）。
     */
    public BigDecimal averageCost() {
        return averageCost;
    }

    /**
     * @brief 返回成本币种（Return Cost Currency）；
     *        Return cost currency code.
     *
     * @return 成本币种（Cost currency）。
     */
    public String costCurrencyCode() {
        return costCurrencyCode;
    }

    /**
     * @brief 返回市值（Return Market Value）；
     *        Return market value.
     *
     * @return 市值（Market value）。
     */
    public BigDecimal marketValue() {
        return marketValue;
    }

    /**
     * @brief 返回估值币种（Return Valuation Currency）；
     *        Return valuation currency code.
     *
     * @return 估值币种（Valuation currency）。
     */
    public String valuationCurrencyCode() {
        return valuationCurrencyCode;
    }

    /**
     * @brief 返回未实现盈亏（Return Unrealized PnL）；
     *        Return unrealized PnL.
     *
     * @return 未实现盈亏（Unrealized PnL）。
     */
    public BigDecimal unrealizedPnl() {
        return unrealizedPnl;
    }

    /**
     * @brief 返回更新时间（Return Updated Timestamp）；
     *        Return updated timestamp.
     *
     * @return 更新时间（Updated timestamp）。
     */
    public Instant updatedAt() {
        return updatedAt;
    }
}
