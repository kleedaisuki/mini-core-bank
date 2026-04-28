package com.moesegfault.banking.domain.investment;

import com.moesegfault.banking.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 投资订单结算事件（Investment Order Settled Event）；
 *        Investment-order-settled event.
 */
public final class InvestmentOrderSettled implements DomainEvent {

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
     * @brief 结算时间（Settlement Timestamp）；
     *        Settlement timestamp.
     */
    private final Instant settlementAt;

    /**
     * @brief 事件时间（Occurred Timestamp）；
     *        Event occurred timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造订单结算事件（Construct Order Settled Event）；
     *        Construct investment-order-settled event.
     *
     * @param investmentOrderId   订单 ID（Order ID）。
     * @param investmentAccountId 投资账户 ID（Investment account ID）。
     * @param productId           产品 ID（Product ID）。
     * @param orderSide           订单方向（Order side）。
     * @param quantity            订单份额（Quantity）。
     * @param settlementAt        结算时间（Settlement timestamp）。
     * @param occurredAt          事件时间（Occurred timestamp）。
     */
    public InvestmentOrderSettled(
            final InvestmentOrderId investmentOrderId,
            final InvestmentAccountId investmentAccountId,
            final ProductId productId,
            final OrderSide orderSide,
            final Quantity quantity,
            final Instant settlementAt,
            final Instant occurredAt
    ) {
        this.investmentOrderId = Objects.requireNonNull(investmentOrderId, "Investment order ID must not be null");
        this.investmentAccountId = Objects.requireNonNull(
                investmentAccountId,
                "Investment account ID must not be null");
        this.productId = Objects.requireNonNull(productId, "Product ID must not be null");
        this.orderSide = Objects.requireNonNull(orderSide, "Order side must not be null");
        this.quantity = Objects.requireNonNull(quantity, "Quantity must not be null");
        this.settlementAt = Objects.requireNonNull(settlementAt, "Settlement-at must not be null");
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
     * @brief 返回结算时间（Return Settlement Timestamp）；
     *        Return settlement timestamp.
     *
     * @return 结算时间（Settlement timestamp）。
     */
    public Instant settlementAt() {
        return settlementAt;
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
