package com.moesegfault.banking.application.investment.command;

import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.investment.RiskLevel;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 买入产品命令（Buy Product Command）；
 *        Command object for placing a buy order.
 */
public final class BuyProductCommand {

    /**
     * @brief 投资账户 ID（Investment Account ID）；
     *        Investment account identifier string.
     */
    private final String investmentAccountId;

    /**
     * @brief 产品代码（Product Code）；
     *        Product code string.
     */
    private final String productCode;

    /**
     * @brief 下单份额（Order Quantity）；
     *        Order quantity decimal.
     */
    private final BigDecimal quantity;

    /**
     * @brief 下单价格（Order Price / NAV）；
     *        Execution price (NAV) decimal.
     */
    private final BigDecimal price;

    /**
     * @brief 手续费金额（Fee Amount）；
     *        Fee amount decimal.
     */
    private final BigDecimal feeAmount;

    /**
     * @brief 发起客户 ID（可空）（Initiator Customer ID, Nullable）；
     *        Initiator customer ID, nullable.
     */
    private final String initiatorCustomerIdOrNull;

    /**
     * @brief 交易渠道（Business Channel）；
     *        Business channel enum.
     */
    private final BusinessChannel channel;

    /**
     * @brief 业务参考号（可空）（Business Reference, Nullable）；
     *        Optional caller-provided business reference.
     */
    private final String referenceNoOrNull;

    /**
     * @brief 客户风险承受等级（可空）（Customer Risk Tolerance, Nullable）；
     *        Optional customer risk tolerance for suitability check.
     */
    private final RiskLevel customerRiskToleranceOrNull;

    /**
     * @brief 交易时间（可空）（Trade Timestamp, Nullable）；
     *        Optional trade timestamp.
     */
    private final Instant tradeAtOrNull;

    /**
     * @brief 构造买入命令（Construct Buy Command）；
     *        Construct buy-product command.
     *
     * @param investmentAccountId      投资账户 ID（Investment account ID）。
     * @param productCode              产品代码（Product code）。
     * @param quantity                 下单份额（Order quantity）。
     * @param price                    下单价格（Order price）。
     * @param feeAmount                手续费金额（Fee amount, nullable means zero）。
     * @param initiatorCustomerIdOrNull 发起客户 ID（Initiator customer ID, nullable）。
     * @param channel                  交易渠道（Business channel, nullable means ONLINE）。
     * @param referenceNoOrNull        业务参考号（Business reference, nullable）。
     * @param customerRiskToleranceOrNull 客户风险承受等级（Risk tolerance, nullable）。
     * @param tradeAtOrNull            交易时间（Trade timestamp, nullable）。
     */
    public BuyProductCommand(
            final String investmentAccountId,
            final String productCode,
            final BigDecimal quantity,
            final BigDecimal price,
            final BigDecimal feeAmount,
            final String initiatorCustomerIdOrNull,
            final BusinessChannel channel,
            final String referenceNoOrNull,
            final RiskLevel customerRiskToleranceOrNull,
            final Instant tradeAtOrNull
    ) {
        this.investmentAccountId = Objects.requireNonNull(investmentAccountId, "investmentAccountId must not be null");
        this.productCode = Objects.requireNonNull(productCode, "productCode must not be null");
        this.quantity = Objects.requireNonNull(quantity, "quantity must not be null");
        this.price = Objects.requireNonNull(price, "price must not be null");
        this.feeAmount = feeAmount;
        this.initiatorCustomerIdOrNull = initiatorCustomerIdOrNull;
        this.channel = channel;
        this.referenceNoOrNull = referenceNoOrNull;
        this.customerRiskToleranceOrNull = customerRiskToleranceOrNull;
        this.tradeAtOrNull = tradeAtOrNull;
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
     * @brief 返回产品代码（Return Product Code）；
     *        Return product code.
     *
     * @return 产品代码（Product code）。
     */
    public String productCode() {
        return productCode;
    }

    /**
     * @brief 返回份额（Return Quantity）；
     *        Return quantity decimal.
     *
     * @return 份额（Quantity）。
     */
    public BigDecimal quantity() {
        return quantity;
    }

    /**
     * @brief 返回价格（Return Price）；
     *        Return execution price.
     *
     * @return 价格（Price）。
     */
    public BigDecimal price() {
        return price;
    }

    /**
     * @brief 返回手续费金额（Return Fee Amount）；
     *        Return fee amount decimal.
     *
     * @return 手续费金额（Fee amount, nullable）。
     */
    public BigDecimal feeAmountOrNull() {
        return feeAmount;
    }

    /**
     * @brief 返回发起客户 ID（可空）（Return Initiator Customer ID, Nullable）；
     *        Return initiator customer ID, nullable.
     *
     * @return 发起客户 ID 或 null（Initiator customer ID or null）。
     */
    public String initiatorCustomerIdOrNull() {
        return initiatorCustomerIdOrNull;
    }

    /**
     * @brief 返回交易渠道（Return Business Channel）；
     *        Return business channel.
     *
     * @return 交易渠道（Business channel, nullable）。
     */
    public BusinessChannel channelOrNull() {
        return channel;
    }

    /**
     * @brief 返回业务参考号（可空）（Return Business Reference, Nullable）；
     *        Return optional business reference.
     *
     * @return 业务参考号或 null（Reference or null）。
     */
    public String referenceNoOrNull() {
        return referenceNoOrNull;
    }

    /**
     * @brief 返回客户风险承受等级（可空）（Return Risk Tolerance, Nullable）；
     *        Return optional customer risk tolerance.
     *
     * @return 风险承受等级或 null（Risk tolerance or null）。
     */
    public RiskLevel customerRiskToleranceOrNull() {
        return customerRiskToleranceOrNull;
    }

    /**
     * @brief 返回交易时间（可空）（Return Trade Timestamp, Nullable）；
     *        Return optional trade timestamp.
     *
     * @return 交易时间或 null（Trade timestamp or null）。
     */
    public Instant tradeAtOrNull() {
        return tradeAtOrNull;
    }
}
