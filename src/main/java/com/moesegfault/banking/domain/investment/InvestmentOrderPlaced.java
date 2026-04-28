package com.moesegfault.banking.domain.investment;

import com.moesegfault.banking.domain.account.InvestmentAccountId;
import com.moesegfault.banking.domain.shared.DomainEvent;
import com.moesegfault.banking.domain.shared.Money;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 投资订单提交事件（Investment Order Placed Event）；
 *        Investment-order-placed event.
 */
public final class InvestmentOrderPlaced implements DomainEvent {

    /**
     * @brief 订单 ID（Order ID）；
     *        Order identifier.
     */
    private final InvestmentOrderId investmentOrderId;

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
     * @brief 订单方向（Order Side）；
     *        Order side.
     */
    private final OrderSide orderSide;

    /**
     * @brief 订单份额（Quantity）；
     *        Order quantity.
     */
    private final Quantity quantity;

    /**
     * @brief 成交价格（Price）；
     *        Execution price.
     */
    private final NetAssetValue price;

    /**
     * @brief 订单总额（Gross Amount）；
     *        Gross amount.
     */
    private final Money grossAmount;

    /**
     * @brief 手续费（Fee Amount）；
     *        Fee amount.
     */
    private final Money feeAmount;

    /**
     * @brief 交易时间（Trade Timestamp）；
     *        Trade timestamp.
     */
    private final Instant tradeAt;

    /**
     * @brief 事件时间（Occurred Timestamp）；
     *        Event occurred timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造订单提交事件（Construct Order Placed Event）；
     *        Construct investment-order-placed event.
     *
     * @param investmentOrderId   订单 ID（Order ID）。
     * @param investmentAccountId 投资账户 ID（Investment account ID）。
     * @param productId           产品 ID（Product ID）。
     * @param orderSide           订单方向（Order side）。
     * @param quantity            订单份额（Quantity）。
     * @param price               成交价格（Price）。
     * @param grossAmount         订单总额（Gross amount）。
     * @param feeAmount           手续费（Fee amount）。
     * @param tradeAt             交易时间（Trade timestamp）。
     * @param occurredAt          事件时间（Occurred timestamp）。
     */
    public InvestmentOrderPlaced(
            final InvestmentOrderId investmentOrderId,
            final InvestmentAccountId investmentAccountId,
            final ProductId productId,
            final OrderSide orderSide,
            final Quantity quantity,
            final NetAssetValue price,
            final Money grossAmount,
            final Money feeAmount,
            final Instant tradeAt,
            final Instant occurredAt
    ) {
        this.investmentOrderId = Objects.requireNonNull(investmentOrderId, "Investment order ID must not be null");
        this.investmentAccountId = Objects.requireNonNull(
                investmentAccountId,
                "Investment account ID must not be null");
        this.productId = Objects.requireNonNull(productId, "Product ID must not be null");
        this.orderSide = Objects.requireNonNull(orderSide, "Order side must not be null");
        this.quantity = Objects.requireNonNull(quantity, "Quantity must not be null");
        this.price = Objects.requireNonNull(price, "Price must not be null");
        this.grossAmount = Objects.requireNonNull(grossAmount, "Gross amount must not be null");
        this.feeAmount = Objects.requireNonNull(feeAmount, "Fee amount must not be null");
        this.tradeAt = Objects.requireNonNull(tradeAt, "Trade-at must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
    }

    /**
     * @brief 返回订单 ID（Return Order ID）；
     *        Return order identifier.
     *
     * @return 订单 ID（Order ID）。
     */
    public InvestmentOrderId investmentOrderId() {
        return investmentOrderId;
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
     * @brief 返回订单方向（Return Order Side）；
     *        Return order side.
     *
     * @return 订单方向（Order side）。
     */
    public OrderSide orderSide() {
        return orderSide;
    }

    /**
     * @brief 返回订单份额（Return Quantity）；
     *        Return order quantity.
     *
     * @return 订单份额（Quantity）。
     */
    public Quantity quantity() {
        return quantity;
    }

    /**
     * @brief 返回成交价格（Return Price）；
     *        Return execution price.
     *
     * @return 成交价格（Price）。
     */
    public NetAssetValue price() {
        return price;
    }

    /**
     * @brief 返回订单总额（Return Gross Amount）；
     *        Return gross amount.
     *
     * @return 订单总额（Gross amount）。
     */
    public Money grossAmount() {
        return grossAmount;
    }

    /**
     * @brief 返回手续费（Return Fee Amount）；
     *        Return fee amount.
     *
     * @return 手续费（Fee amount）。
     */
    public Money feeAmount() {
        return feeAmount;
    }

    /**
     * @brief 返回交易时间（Return Trade Timestamp）；
     *        Return trade timestamp.
     *
     * @return 交易时间（Trade timestamp）。
     */
    public Instant tradeAt() {
        return tradeAt;
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
