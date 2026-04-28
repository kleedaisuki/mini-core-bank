package com.moesegfault.banking.domain.investment;

import com.moesegfault.banking.domain.account.InvestmentAccountId;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 持仓变更事件（Holding Changed Event）；
 *        Holding-changed event.
 */
public final class HoldingChanged implements DomainEvent {

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
     * @brief 当前份额（Quantity）；
     *        Current quantity.
     */
    private final Quantity quantity;

    /**
     * @brief 平均成本（Average Cost）；
     *        Average cost.
     */
    private final BigDecimal averageCost;

    /**
     * @brief 成本币种（Cost Currency Code）；
     *        Cost currency code.
     */
    private final CurrencyCode costCurrencyCode;

    /**
     * @brief 当前市值（Market Value）；
     *        Market value.
     */
    private final BigDecimal marketValue;

    /**
     * @brief 估值币种（Valuation Currency Code）；
     *        Valuation currency code.
     */
    private final CurrencyCode valuationCurrencyCode;

    /**
     * @brief 未实现盈亏（Unrealized PnL）；
     *        Unrealized PnL.
     */
    private final BigDecimal unrealizedPnl;

    /**
     * @brief 事件时间（Occurred Timestamp）；
     *        Event occurred timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造持仓变更事件（Construct Holding Changed Event）；
     *        Construct holding-changed event.
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
     * @param occurredAt           事件时间（Occurred timestamp）。
     */
    public HoldingChanged(
            final HoldingId holdingId,
            final InvestmentAccountId investmentAccountId,
            final ProductId productId,
            final Quantity quantity,
            final BigDecimal averageCost,
            final CurrencyCode costCurrencyCode,
            final BigDecimal marketValue,
            final CurrencyCode valuationCurrencyCode,
            final BigDecimal unrealizedPnl,
            final Instant occurredAt
    ) {
        this.holdingId = Objects.requireNonNull(holdingId, "Holding ID must not be null");
        this.investmentAccountId = Objects.requireNonNull(
                investmentAccountId,
                "Investment account ID must not be null");
        this.productId = Objects.requireNonNull(productId, "Product ID must not be null");
        this.quantity = Objects.requireNonNull(quantity, "Quantity must not be null");
        this.averageCost = Objects.requireNonNull(averageCost, "Average cost must not be null");
        this.costCurrencyCode = Objects.requireNonNull(costCurrencyCode, "Cost currency code must not be null");
        this.marketValue = Objects.requireNonNull(marketValue, "Market value must not be null");
        this.valuationCurrencyCode = Objects.requireNonNull(
                valuationCurrencyCode,
                "Valuation currency code must not be null");
        this.unrealizedPnl = Objects.requireNonNull(unrealizedPnl, "Unrealized PnL must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
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
     * @brief 返回份额（Return Quantity）；
     *        Return current quantity.
     *
     * @return 份额（Quantity）。
     */
    public Quantity quantity() {
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
    public CurrencyCode costCurrencyCode() {
        return costCurrencyCode;
    }

    /**
     * @brief 返回当前市值（Return Market Value）；
     *        Return market value.
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
     *        Return unrealized PnL.
     *
     * @return 未实现盈亏（Unrealized PnL）。
     */
    public BigDecimal unrealizedPnl() {
        return unrealizedPnl;
    }

    /**
     * @brief 返回事件时间（Return Occurred Timestamp）；
     *        Return occurred timestamp.
     *
     * @return 事件时间（Occurred timestamp）。
     */
    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
