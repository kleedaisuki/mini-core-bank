package com.moesegfault.banking.application.investment.result;

import com.moesegfault.banking.domain.business.BusinessTransaction;
import com.moesegfault.banking.domain.investment.Holding;
import com.moesegfault.banking.domain.investment.InvestmentOrder;
import com.moesegfault.banking.domain.investment.InvestmentProduct;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 投资订单结果视图（Investment Order Result View）；
 *        Read model for placed-and-settled investment order output.
 */
public final class InvestmentOrderResult {

    /**
     * @brief 订单 ID（Order ID）；
     *        Order identifier.
     */
    private final String orderId;

    /**
     * @brief 业务参考号（Business Reference）；
     *        Business reference number.
     */
    private final String referenceNo;

    /**
     * @brief 交易状态（Transaction Status）；
     *        Business transaction status.
     */
    private final String transactionStatus;

    /**
     * @brief 投资账户 ID（Investment Account ID）；
     *        Investment account identifier.
     */
    private final String investmentAccountId;

    /**
     * @brief 产品 ID（Product ID）；
     *        Product identifier.
     */
    private final String productId;

    /**
     * @brief 产品代码（Product Code）；
     *        Product code.
     */
    private final String productCode;

    /**
     * @brief 订单方向（Order Side）；
     *        Order side name.
     */
    private final String orderSide;

    /**
     * @brief 份额（Quantity）；
     *        Order quantity.
     */
    private final BigDecimal quantity;

    /**
     * @brief 价格（Price / NAV）；
     *        Execution price.
     */
    private final BigDecimal price;

    /**
     * @brief 总金额（Gross Amount）；
     *        Gross amount.
     */
    private final BigDecimal grossAmount;

    /**
     * @brief 手续费（Fee Amount）；
     *        Fee amount.
     */
    private final BigDecimal feeAmount;

    /**
     * @brief 币种（Currency Code）；
     *        Currency code.
     */
    private final String currencyCode;

    /**
     * @brief 订单状态（Order Status）；
     *        Order status name.
     */
    private final String orderStatus;

    /**
     * @brief 交易时间（Trade Timestamp）；
     *        Trade timestamp.
     */
    private final Instant tradeAt;

    /**
     * @brief 结算时间（可空）（Settlement Timestamp, Nullable）；
     *        Settlement timestamp, nullable.
     */
    private final Instant settlementAtOrNull;

    /**
     * @brief 订单现金影响（Cash Impact）；
     *        Net cash impact from order.
     */
    private final BigDecimal cashImpact;

    /**
     * @brief 结算后持仓份额（Holding Quantity After Settlement）；
     *        Holding quantity after settlement.
     */
    private final BigDecimal holdingQuantityAfter;

    /**
     * @brief 构造订单结果（Construct Order Result）；
     *        Construct investment order result view.
     *
     * @param orderId               订单 ID（Order ID）。
     * @param referenceNo           业务参考号（Business reference）。
     * @param transactionStatus     交易状态（Transaction status）。
     * @param investmentAccountId   投资账户 ID（Investment account ID）。
     * @param productId             产品 ID（Product ID）。
     * @param productCode           产品代码（Product code）。
     * @param orderSide             订单方向（Order side）。
     * @param quantity              份额（Quantity）。
     * @param price                 价格（Price）。
     * @param grossAmount           总金额（Gross amount）。
     * @param feeAmount             手续费（Fee amount）。
     * @param currencyCode          币种（Currency code）。
     * @param orderStatus           订单状态（Order status）。
     * @param tradeAt               交易时间（Trade timestamp）。
     * @param settlementAtOrNull    结算时间（Settlement timestamp, nullable）。
     * @param cashImpact            现金影响（Cash impact）。
     * @param holdingQuantityAfter  结算后持仓份额（Holding quantity after）。
     */
    public InvestmentOrderResult(
            final String orderId,
            final String referenceNo,
            final String transactionStatus,
            final String investmentAccountId,
            final String productId,
            final String productCode,
            final String orderSide,
            final BigDecimal quantity,
            final BigDecimal price,
            final BigDecimal grossAmount,
            final BigDecimal feeAmount,
            final String currencyCode,
            final String orderStatus,
            final Instant tradeAt,
            final Instant settlementAtOrNull,
            final BigDecimal cashImpact,
            final BigDecimal holdingQuantityAfter
    ) {
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.referenceNo = Objects.requireNonNull(referenceNo, "referenceNo must not be null");
        this.transactionStatus = Objects.requireNonNull(transactionStatus, "transactionStatus must not be null");
        this.investmentAccountId = Objects.requireNonNull(investmentAccountId, "investmentAccountId must not be null");
        this.productId = Objects.requireNonNull(productId, "productId must not be null");
        this.productCode = Objects.requireNonNull(productCode, "productCode must not be null");
        this.orderSide = Objects.requireNonNull(orderSide, "orderSide must not be null");
        this.quantity = Objects.requireNonNull(quantity, "quantity must not be null");
        this.price = Objects.requireNonNull(price, "price must not be null");
        this.grossAmount = Objects.requireNonNull(grossAmount, "grossAmount must not be null");
        this.feeAmount = Objects.requireNonNull(feeAmount, "feeAmount must not be null");
        this.currencyCode = Objects.requireNonNull(currencyCode, "currencyCode must not be null");
        this.orderStatus = Objects.requireNonNull(orderStatus, "orderStatus must not be null");
        this.tradeAt = Objects.requireNonNull(tradeAt, "tradeAt must not be null");
        this.settlementAtOrNull = settlementAtOrNull;
        this.cashImpact = Objects.requireNonNull(cashImpact, "cashImpact must not be null");
        this.holdingQuantityAfter = Objects.requireNonNull(holdingQuantityAfter, "holdingQuantityAfter must not be null");
    }

    /**
     * @brief 从订单聚合映射结果（Map from Order Aggregates）；
     *        Map result from order, transaction, product and holding aggregates.
     *
     * @param order       订单实体（Order entity）。
     * @param transaction 业务交易实体（Business transaction entity）。
     * @param product     产品实体（Product entity）。
     * @param holding     持仓实体（Holding entity）。
     * @return 订单结果视图（Order result view）。
     */
    public static InvestmentOrderResult from(
            final InvestmentOrder order,
            final BusinessTransaction transaction,
            final InvestmentProduct product,
            final Holding holding
    ) {
        final InvestmentOrder normalizedOrder = Objects.requireNonNull(order, "order must not be null");
        final BusinessTransaction normalizedTransaction = Objects.requireNonNull(
                transaction,
                "transaction must not be null");
        final InvestmentProduct normalizedProduct = Objects.requireNonNull(product, "product must not be null");
        final Holding normalizedHolding = Objects.requireNonNull(holding, "holding must not be null");

        return new InvestmentOrderResult(
                normalizedOrder.investmentOrderId().value(),
                normalizedTransaction.referenceNo().value(),
                normalizedTransaction.transactionStatus().name(),
                normalizedOrder.investmentAccountId().value(),
                normalizedOrder.productId().value(),
                normalizedProduct.productCode().value(),
                normalizedOrder.orderSide().name(),
                normalizedOrder.quantity().value(),
                normalizedOrder.price().value(),
                normalizedOrder.grossAmount().amount(),
                normalizedOrder.feeAmount().amount(),
                normalizedOrder.currencyCode().value(),
                normalizedOrder.orderStatus().name(),
                normalizedOrder.tradeAt(),
                normalizedOrder.settlementAtOrNull(),
                normalizedOrder.cashImpact().amount(),
                normalizedHolding.quantity().value());
    }

    /**
     * @brief 返回订单 ID（Return Order ID）；
     *        Return order ID.
     *
     * @return 订单 ID（Order ID）。
     */
    public String orderId() {
        return orderId;
    }

    /**
     * @brief 返回业务参考号（Return Reference No）；
     *        Return business reference.
     *
     * @return 业务参考号（Business reference）。
     */
    public String referenceNo() {
        return referenceNo;
    }

    /**
     * @brief 返回交易状态（Return Transaction Status）；
     *        Return transaction status.
     *
     * @return 交易状态（Transaction status）。
     */
    public String transactionStatus() {
        return transactionStatus;
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
     * @brief 返回产品代码（Return Product Code）；
     *        Return product code.
     *
     * @return 产品代码（Product code）。
     */
    public String productCode() {
        return productCode;
    }

    /**
     * @brief 返回订单方向（Return Order Side）；
     *        Return order side.
     *
     * @return 订单方向（Order side）。
     */
    public String orderSide() {
        return orderSide;
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
     * @brief 返回价格（Return Price）；
     *        Return price.
     *
     * @return 价格（Price）。
     */
    public BigDecimal price() {
        return price;
    }

    /**
     * @brief 返回总金额（Return Gross Amount）；
     *        Return gross amount.
     *
     * @return 总金额（Gross amount）。
     */
    public BigDecimal grossAmount() {
        return grossAmount;
    }

    /**
     * @brief 返回手续费（Return Fee Amount）；
     *        Return fee amount.
     *
     * @return 手续费（Fee amount）。
     */
    public BigDecimal feeAmount() {
        return feeAmount;
    }

    /**
     * @brief 返回币种（Return Currency Code）；
     *        Return currency code.
     *
     * @return 币种代码（Currency code）。
     */
    public String currencyCode() {
        return currencyCode;
    }

    /**
     * @brief 返回订单状态（Return Order Status）；
     *        Return order status.
     *
     * @return 订单状态（Order status）。
     */
    public String orderStatus() {
        return orderStatus;
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
     * @brief 返回结算时间（可空）（Return Settlement Timestamp, Nullable）；
     *        Return settlement timestamp, nullable.
     *
     * @return 结算时间或 null（Settlement timestamp or null）。
     */
    public Instant settlementAtOrNull() {
        return settlementAtOrNull;
    }

    /**
     * @brief 返回现金影响（Return Cash Impact）；
     *        Return net cash impact amount.
     *
     * @return 现金影响（Cash impact）。
     */
    public BigDecimal cashImpact() {
        return cashImpact;
    }

    /**
     * @brief 返回结算后份额（Return Holding Quantity After）；
     *        Return holding quantity after settlement.
     *
     * @return 结算后持仓份额（Holding quantity after settlement）。
     */
    public BigDecimal holdingQuantityAfter() {
        return holdingQuantityAfter;
    }
}
