package com.moesegfault.banking.domain.investment;

import com.moesegfault.banking.domain.account.InvestmentAccountId;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 投资持仓实体（Holding Entity），映射 `investment_holding` 并维护份额/估值不变量（Invariant）；
 *        Holding entity mapped to `investment_holding` and enforcing quantity/valuation invariants.
 */
public final class Holding {

    /**
     * @brief 小数位（Scale）对齐 `NUMERIC(19,6)`；
     *        Decimal scale aligned with `NUMERIC(19,6)`.
     */
    public static final int SCALE = 6;

    /**
     * @brief 最大精度（Max Precision）对齐 `NUMERIC(19,6)`；
     *        Maximum precision aligned with `NUMERIC(19,6)`.
     */
    public static final int MAX_PRECISION = 19;

    /**
     * @brief 持仓 ID（Holding ID）；
     *        Holding identifier.
     */
    private final HoldingId holdingId;

    /**
     * @brief 投资账户 ID（Investment Account ID）；
     *        Investment-account identifier.
     */
    private final InvestmentAccountId investmentAccountId;

    /**
     * @brief 产品 ID（Product ID）；
     *        Product identifier.
     */
    private final ProductId productId;

    /**
     * @brief 当前份额（Current Quantity）；
     *        Current holding quantity.
     */
    private Quantity quantity;

    /**
     * @brief 平均成本（Average Cost）；
     *        Average cost per unit.
     */
    private BigDecimal averageCost;

    /**
     * @brief 成本币种（Cost Currency Code）；
     *        Cost currency code.
     */
    private final CurrencyCode costCurrencyCode;

    /**
     * @brief 当前市值（Market Value）；
     *        Current market value amount.
     */
    private BigDecimal marketValue;

    /**
     * @brief 估值币种（Valuation Currency Code）；
     *        Valuation currency code.
     */
    private CurrencyCode valuationCurrencyCode;

    /**
     * @brief 未实现盈亏（Unrealized PnL）；
     *        Unrealized profit and loss amount.
     */
    private BigDecimal unrealizedPnl;

    /**
     * @brief 更新时间（Last Updated Timestamp）；
     *        Last updated timestamp.
     */
    private Instant updatedAt;

    /**
     * @brief 构造持仓实体（Construct Holding Entity）；
     *        Construct holding entity.
     *
     * @param holdingId            持仓 ID（Holding ID）。
     * @param investmentAccountId  投资账户 ID（Investment account ID）。
     * @param productId            产品 ID（Product ID）。
     * @param quantity             当前份额（Quantity）。
     * @param averageCost          平均成本（Average cost）。
     * @param costCurrencyCode     成本币种（Cost currency）。
     * @param marketValue          当前市值（Market value）。
     * @param valuationCurrencyCode 估值币种（Valuation currency）。
     * @param unrealizedPnl        未实现盈亏（Unrealized PnL）。
     * @param updatedAt            更新时间（Updated timestamp）。
     */
    private Holding(
            final HoldingId holdingId,
            final InvestmentAccountId investmentAccountId,
            final ProductId productId,
            final Quantity quantity,
            final BigDecimal averageCost,
            final CurrencyCode costCurrencyCode,
            final BigDecimal marketValue,
            final CurrencyCode valuationCurrencyCode,
            final BigDecimal unrealizedPnl,
            final Instant updatedAt
    ) {
        this.holdingId = Objects.requireNonNull(holdingId, "Holding ID must not be null");
        this.investmentAccountId = Objects.requireNonNull(
                investmentAccountId,
                "Investment account ID must not be null");
        this.productId = Objects.requireNonNull(productId, "Product ID must not be null");
        this.quantity = Objects.requireNonNull(quantity, "Quantity must not be null");
        this.averageCost = normalizeAmount(averageCost, "Average cost");
        this.costCurrencyCode = Objects.requireNonNull(costCurrencyCode, "Cost currency code must not be null");
        this.marketValue = normalizeAmount(marketValue, "Market value");
        this.valuationCurrencyCode = Objects.requireNonNull(
                valuationCurrencyCode,
                "Valuation currency code must not be null");
        this.unrealizedPnl = normalizeAmount(unrealizedPnl, "Unrealized PnL");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated-at must not be null");
        validateInvariants();
    }

    /**
     * @brief 创建空持仓（Create Empty Holding）；
     *        Create an empty holding.
     *
     * @param holdingId           持仓 ID（Holding ID）。
     * @param investmentAccountId 投资账户 ID（Investment account ID）。
     * @param productId           产品 ID（Product ID）。
     * @param currencyCode        币种（Currency code）。
     * @return 空持仓实体（Empty holding entity）。
     */
    public static Holding open(
            final HoldingId holdingId,
            final InvestmentAccountId investmentAccountId,
            final ProductId productId,
            final CurrencyCode currencyCode
    ) {
        final Instant now = Instant.now();
        return new Holding(
                holdingId,
                investmentAccountId,
                productId,
                Quantity.zero(),
                BigDecimal.ZERO,
                currencyCode,
                BigDecimal.ZERO,
                currencyCode,
                BigDecimal.ZERO,
                now);
    }

    /**
     * @brief 从持久化状态重建持仓（Restore Holding from Persistence）；
     *        Restore holding from persistence state.
     *
     * @param holdingId             持仓 ID（Holding ID）。
     * @param investmentAccountId   投资账户 ID（Investment account ID）。
     * @param productId             产品 ID（Product ID）。
     * @param quantity              当前份额（Quantity）。
     * @param averageCost           平均成本（Average cost）。
     * @param costCurrencyCode      成本币种（Cost currency）。
     * @param marketValue           当前市值（Market value）。
     * @param valuationCurrencyCode 估值币种（Valuation currency）。
     * @param unrealizedPnl         未实现盈亏（Unrealized PnL）。
     * @param updatedAt             更新时间（Updated timestamp）。
     * @return 重建后的持仓实体（Restored holding entity）。
     */
    public static Holding restore(
            final HoldingId holdingId,
            final InvestmentAccountId investmentAccountId,
            final ProductId productId,
            final Quantity quantity,
            final BigDecimal averageCost,
            final CurrencyCode costCurrencyCode,
            final BigDecimal marketValue,
            final CurrencyCode valuationCurrencyCode,
            final BigDecimal unrealizedPnl,
            final Instant updatedAt
    ) {
        return new Holding(
                holdingId,
                investmentAccountId,
                productId,
                quantity,
                averageCost,
                costCurrencyCode,
                marketValue,
                valuationCurrencyCode,
                unrealizedPnl,
                updatedAt);
    }

    /**
     * @brief 买入加仓（Buy and Increase Holding）；
     *        Buy quantity and recalculate weighted average cost.
     *
     * @param buyQuantity    买入份额（Buy quantity）。
     * @param executionPrice 成交价格（Execution price）。
     */
    public void buy(
            final Quantity buyQuantity,
            final NetAssetValue executionPrice
    ) {
        final Quantity normalizedQuantity = Objects.requireNonNull(buyQuantity, "Buy quantity must not be null");
        final NetAssetValue normalizedPrice = Objects.requireNonNull(
                executionPrice,
                "Execution price must not be null");
        if (!normalizedQuantity.isPositive()) {
            throw new BusinessRuleViolation("Buy quantity must be positive");
        }
        if (!costCurrencyCode.equals(normalizedPrice.currencyCode())) {
            throw new BusinessRuleViolation("Execution price currency must match holding cost currency");
        }

        final BigDecimal currentTotalCost = quantity.value().multiply(averageCost);
        final BigDecimal incrementalCost = normalizedQuantity.value().multiply(normalizedPrice.value());
        final Quantity mergedQuantity = quantity.add(normalizedQuantity);

        final BigDecimal mergedAverageCost = currentTotalCost.add(incrementalCost)
                .divide(mergedQuantity.value(), SCALE, RoundingMode.HALF_UP);

        quantity = mergedQuantity;
        averageCost = normalizeAmount(mergedAverageCost, "Average cost");
        touch();
    }

    /**
     * @brief 卖出减仓（Sell and Decrease Holding）；
     *        Sell quantity and keep non-negative holding.
     *
     * @param sellQuantity 卖出份额（Sell quantity）。
     */
    public void sell(final Quantity sellQuantity) {
        final Quantity normalizedQuantity = Objects.requireNonNull(sellQuantity, "Sell quantity must not be null");
        HoldingPolicy.ensureSufficientQuantity(this, normalizedQuantity);
        quantity = quantity.subtract(normalizedQuantity);
        if (quantity.isZero()) {
            averageCost = normalizeAmount(BigDecimal.ZERO, "Average cost");
            marketValue = normalizeAmount(BigDecimal.ZERO, "Market value");
            unrealizedPnl = normalizeAmount(BigDecimal.ZERO, "Unrealized PnL");
        }
        touch();
    }

    /**
     * @brief 按估值更新持仓市值与未实现盈亏（Mark to Market by Valuation）；
     *        Mark holding to market by valuation.
     *
     * @param valuation 产品估值（Product valuation）。
     */
    public void markToMarket(final ProductValuation valuation) {
        final ProductValuation normalized = Objects.requireNonNull(valuation, "Valuation must not be null");
        if (!productId.equals(normalized.productId())) {
            throw new BusinessRuleViolation("Valuation product does not match holding product");
        }
        final NetAssetValue nav = normalized.netAssetValue();
        if (!costCurrencyCode.equals(nav.currencyCode())) {
            throw new BusinessRuleViolation("Valuation currency must match holding cost currency");
        }

        valuationCurrencyCode = nav.currencyCode();
        marketValue = normalizeAmount(nav.marketValueOf(quantity), "Market value");

        final BigDecimal costBasis = quantity.value().multiply(averageCost).setScale(SCALE, RoundingMode.HALF_UP);
        unrealizedPnl = normalizeAmount(marketValue.subtract(costBasis), "Unrealized PnL");
        touch();
    }

    /**
     * @brief 应用已结算订单到持仓（Apply Settled Order to Holding）；
     *        Apply a settled order to holding quantity.
     *
     * @param order 已结算订单（Settled order）。
     */
    public void applySettledOrder(final InvestmentOrder order) {
        final InvestmentOrder normalized = Objects.requireNonNull(order, "Order must not be null");
        if (normalized.orderStatus() != OrderStatus.SETTLED) {
            throw new BusinessRuleViolation("Only settled order can be applied to holding");
        }
        if (!investmentAccountId.equals(normalized.investmentAccountId())) {
            throw new BusinessRuleViolation("Order investment account does not match holding");
        }
        if (!productId.equals(normalized.productId())) {
            throw new BusinessRuleViolation("Order product does not match holding");
        }

        if (normalized.orderSide().increasesHolding()) {
            buy(normalized.quantity(), normalized.price());
            return;
        }
        if (normalized.orderSide().decreasesHolding()) {
            sell(normalized.quantity());
        }
    }

    /**
     * @brief 构建持仓变更事件（Build Holding Changed Event）；
     *        Build holding-changed event.
     *
     * @return 持仓变更事件（Holding-changed event）。
     */
    public HoldingChanged changedEvent() {
        return new HoldingChanged(
                holdingId,
                investmentAccountId,
                productId,
                quantity,
                averageCost,
                costCurrencyCode,
                marketValue,
                valuationCurrencyCode,
                unrealizedPnl,
                updatedAt);
    }

    /**
     * @brief 返回持仓 ID（Return Holding ID）；
     *        Return holding identifier.
     *
     * @return 持仓 ID（Holding ID）。
     */
    public HoldingId holdingId() {
        return holdingId;
    }

    /**
     * @brief 返回投资账户 ID（Return Investment Account ID）；
     *        Return investment-account identifier.
     *
     * @return 投资账户 ID（Investment account ID）。
     */
    public InvestmentAccountId investmentAccountId() {
        return investmentAccountId;
    }

    /**
     * @brief 返回产品 ID（Return Product ID）；
     *        Return product identifier.
     *
     * @return 产品 ID（Product ID）。
     */
    public ProductId productId() {
        return productId;
    }

    /**
     * @brief 返回当前份额（Return Current Quantity）；
     *        Return current quantity.
     *
     * @return 当前份额（Current quantity）。
     */
    public Quantity quantity() {
        return quantity;
    }

    /**
     * @brief 返回平均成本（Return Average Cost）；
     *        Return average cost per unit.
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
    public CurrencyCode costCurrencyCode() {
        return costCurrencyCode;
    }

    /**
     * @brief 返回当前市值（Return Market Value）；
     *        Return market value amount.
     *
     * @return 当前市值（Market value）。
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
    public CurrencyCode valuationCurrencyCode() {
        return valuationCurrencyCode;
    }

    /**
     * @brief 返回未实现盈亏（Return Unrealized PnL）；
     *        Return unrealized profit and loss.
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

    /**
     * @brief 触发更新时间（Touch Updated Timestamp）；
     *        Touch updated timestamp.
     */
    private void touch() {
        updatedAt = Instant.now();
    }

    /**
     * @brief 校验持仓不变量（Validate Holding Invariants）；
     *        Validate holding invariants.
     */
    private void validateInvariants() {
        if (quantity.value().signum() < 0) {
            throw new BusinessRuleViolation("Holding quantity must not be negative");
        }
        if (averageCost.signum() < 0) {
            throw new BusinessRuleViolation("Holding average cost must not be negative");
        }
        if (marketValue.signum() < 0) {
            throw new BusinessRuleViolation("Holding market value must not be negative");
        }
        if (quantity.isZero() && averageCost.signum() > 0) {
            throw new BusinessRuleViolation("Empty holding must not keep positive average cost");
        }
    }

    /**
     * @brief 标准化并校验金额字段（Normalize and Validate Amount Field）；
     *        Normalize and validate amount field.
     *
     * @param rawAmount 原始金额（Raw amount）。
     * @param fieldName 字段名（Field name）。
     * @return 标准化金额（Normalized amount）。
     */
    private static BigDecimal normalizeAmount(
            final BigDecimal rawAmount,
            final String fieldName
    ) {
        Objects.requireNonNull(rawAmount, fieldName + " must not be null");
        final BigDecimal normalized = rawAmount.setScale(SCALE, RoundingMode.UNNECESSARY);
        if (normalized.precision() > MAX_PRECISION) {
            throw new IllegalArgumentException(
                    fieldName + " precision must be <= " + MAX_PRECISION + " for NUMERIC(19,6)");
        }
        return normalized;
    }
}
