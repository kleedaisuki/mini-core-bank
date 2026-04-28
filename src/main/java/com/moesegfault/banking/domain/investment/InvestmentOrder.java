package com.moesegfault.banking.domain.investment;

import com.moesegfault.banking.domain.account.InvestmentAccountId;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 投资订单实体（Investment Order Entity），映射 `investment_order_detail` 并维护订单状态机（State Machine）；
 *        Investment-order entity mapped to `investment_order_detail` and enforcing order state-machine invariants.
 */
public final class InvestmentOrder {

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
     * @brief 订单份额（Order Quantity）；
     *        Order quantity.
     */
    private final Quantity quantity;

    /**
     * @brief 成交价格（Execution Price）；
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
     * @brief 结算时间（Settlement Timestamp）；
     *        Settlement timestamp.
     */
    private Instant settlementAt;

    /**
     * @brief 订单状态（Order Status）；
     *        Order status.
     */
    private OrderStatus orderStatus;

    /**
     * @brief 构造投资订单实体（Construct Investment Order Entity）；
     *        Construct investment-order entity.
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
     * @param settlementAt        结算时间（Settlement timestamp）。
     * @param orderStatus         订单状态（Order status）。
     */
    private InvestmentOrder(
            final InvestmentOrderId investmentOrderId,
            final InvestmentAccountId investmentAccountId,
            final ProductId productId,
            final OrderSide orderSide,
            final Quantity quantity,
            final NetAssetValue price,
            final Money grossAmount,
            final Money feeAmount,
            final Instant tradeAt,
            final Instant settlementAt,
            final OrderStatus orderStatus
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
        this.settlementAt = settlementAt;
        this.orderStatus = Objects.requireNonNull(orderStatus, "Order status must not be null");
        validateInvariants();
    }

    /**
     * @brief 提交投资订单（Place Investment Order）；
     *        Place a new investment order with initial status `PLACED`.
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
     * @return 投资订单实体（Investment-order entity）。
     */
    public static InvestmentOrder place(
            final InvestmentOrderId investmentOrderId,
            final InvestmentAccountId investmentAccountId,
            final ProductId productId,
            final OrderSide orderSide,
            final Quantity quantity,
            final NetAssetValue price,
            final Money grossAmount,
            final Money feeAmount,
            final Instant tradeAt
    ) {
        return new InvestmentOrder(
                investmentOrderId,
                investmentAccountId,
                productId,
                orderSide,
                quantity,
                price,
                grossAmount,
                feeAmount,
                tradeAt,
                null,
                OrderStatus.PLACED);
    }

    /**
     * @brief 从持久化状态重建订单（Restore Investment Order）；
     *        Restore investment order from persistence state.
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
     * @param settlementAt        结算时间（Settlement timestamp）。
     * @param orderStatus         订单状态（Order status）。
     * @return 投资订单实体（Investment-order entity）。
     */
    public static InvestmentOrder restore(
            final InvestmentOrderId investmentOrderId,
            final InvestmentAccountId investmentAccountId,
            final ProductId productId,
            final OrderSide orderSide,
            final Quantity quantity,
            final NetAssetValue price,
            final Money grossAmount,
            final Money feeAmount,
            final Instant tradeAt,
            final Instant settlementAt,
            final OrderStatus orderStatus
    ) {
        return new InvestmentOrder(
                investmentOrderId,
                investmentAccountId,
                productId,
                orderSide,
                quantity,
                price,
                grossAmount,
                feeAmount,
                tradeAt,
                settlementAt,
                orderStatus);
    }

    /**
     * @brief 结算订单（Settle Order）；
     *        Settle order and mark status as `SETTLED`.
     *
     * @param settledAt 结算时间（Settlement timestamp）。
     */
    public void settle(final Instant settledAt) {
        final Instant normalized = Objects.requireNonNull(settledAt, "Settled-at must not be null");
        if (orderStatus == OrderStatus.SETTLED) {
            return;
        }
        if (orderStatus != OrderStatus.PLACED) {
            throw new BusinessRuleViolation("Only PLACED order can be settled");
        }
        if (normalized.isBefore(tradeAt)) {
            throw new BusinessRuleViolation("Settlement timestamp must not be before trade timestamp");
        }
        settlementAt = normalized;
        orderStatus = OrderStatus.SETTLED;
    }

    /**
     * @brief 标记订单失败（Mark Order Failed）；
     *        Mark order as failed.
     */
    public void fail() {
        if (orderStatus == OrderStatus.FAILED) {
            return;
        }
        if (orderStatus != OrderStatus.PLACED) {
            throw new BusinessRuleViolation("Only PLACED order can be marked as FAILED");
        }
        orderStatus = OrderStatus.FAILED;
    }

    /**
     * @brief 取消订单（Cancel Order）；
     *        Cancel order.
     */
    public void cancel() {
        if (orderStatus == OrderStatus.CANCELLED) {
            return;
        }
        if (orderStatus != OrderStatus.PLACED) {
            throw new BusinessRuleViolation("Only PLACED order can be cancelled");
        }
        orderStatus = OrderStatus.CANCELLED;
    }

    /**
     * @brief 计算资金净影响（Compute Net Cash Impact）；
     *        Compute net cash impact of this order.
     *
     * @return 资金净影响（Net cash impact）。
     */
    public Money cashImpact() {
        final Money total = grossAmount.add(feeAmount);
        if (orderSide == OrderSide.BUY) {
            return total.negate();
        }
        return grossAmount.subtract(feeAmount);
    }

    /**
     * @brief 构建订单提交事件（Build Order Placed Event）；
     *        Build order-placed event.
     *
     * @return 订单提交事件（Order-placed event）。
     */
    public InvestmentOrderPlaced placedEvent() {
        return new InvestmentOrderPlaced(
                investmentOrderId,
                investmentAccountId,
                productId,
                orderSide,
                quantity,
                price,
                grossAmount,
                feeAmount,
                tradeAt,
                Instant.now());
    }

    /**
     * @brief 构建订单结算事件（Build Order Settled Event）；
     *        Build order-settled event.
     *
     * @return 订单结算事件（Order-settled event）。
     */
    public InvestmentOrderSettled settledEvent() {
        if (orderStatus != OrderStatus.SETTLED || settlementAt == null) {
            throw new BusinessRuleViolation("Only settled order can emit settled event");
        }
        return new InvestmentOrderSettled(
                investmentOrderId,
                investmentAccountId,
                productId,
                orderSide,
                quantity,
                settlementAt,
                Instant.now());
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
     * @return 份额（Quantity）。
     */
    public Quantity quantity() {
        return quantity;
    }

    /**
     * @brief 返回成交价格（Return Execution Price）；
     *        Return execution price.
     *
     * @return 成交价格（Price）。
     */
    public NetAssetValue price() {
        return price;
    }

    /**
     * @brief 返回总额（Return Gross Amount）；
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
     * @brief 返回订单币种（Return Order Currency Code）；
     *        Return order currency code.
     *
     * @return 币种代码（Currency code）。
     */
    public CurrencyCode currencyCode() {
        return grossAmount.currencyCode();
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
     * @brief 返回结算时间（Return Settlement Timestamp）；
     *        Return settlement timestamp.
     *
     * @return 结算时间（Settlement timestamp），未结算返回 null。
     */
    public Instant settlementAtOrNull() {
        return settlementAt;
    }

    /**
     * @brief 返回订单状态（Return Order Status）；
     *        Return order status.
     *
     * @return 订单状态（Order status）。
     */
    public OrderStatus orderStatus() {
        return orderStatus;
    }

    /**
     * @brief 校验订单不变量（Validate Order Invariants）；
     *        Validate order invariants.
     */
    private void validateInvariants() {
        if (!feeAmount.currencyCode().equals(grossAmount.currencyCode())) {
            throw new BusinessRuleViolation("Fee currency must match gross amount currency");
        }
        if (!price.currencyCode().equals(grossAmount.currencyCode())) {
            throw new BusinessRuleViolation("Price currency must match order currency");
        }
        if (grossAmount.isNegative()) {
            throw new BusinessRuleViolation("Gross amount must not be negative");
        }
        if (feeAmount.isNegative()) {
            throw new BusinessRuleViolation("Fee amount must not be negative");
        }
        if (feeAmount.compareTo(grossAmount) > 0) {
            throw new BusinessRuleViolation("Fee amount must not exceed gross amount");
        }
        if (orderSide != OrderSide.DIVIDEND && !quantity.isPositive()) {
            throw new BusinessRuleViolation("Quantity must be positive for BUY/SELL/REDEMPTION order");
        }
        if (orderSide != OrderSide.DIVIDEND && !grossAmount.isPositive()) {
            throw new BusinessRuleViolation("Gross amount must be positive for BUY/SELL/REDEMPTION order");
        }
        if (settlementAt != null && settlementAt.isBefore(tradeAt)) {
            throw new BusinessRuleViolation("Settlement timestamp must not be before trade timestamp");
        }
        if (orderStatus == OrderStatus.SETTLED && settlementAt == null) {
            throw new BusinessRuleViolation("SETTLED order must have settlement timestamp");
        }
        if (orderStatus != OrderStatus.SETTLED && settlementAt != null) {
            throw new BusinessRuleViolation("Only SETTLED order can have settlement timestamp");
        }
    }
}
