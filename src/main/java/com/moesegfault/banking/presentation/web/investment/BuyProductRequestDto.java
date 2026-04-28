package com.moesegfault.banking.presentation.web.investment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.investment.command.BuyProductCommand;
import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.investment.RiskLevel;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 投资买入请求 DTO（Buy Product Request DTO），映射 POST /investment/orders/buy 请求体；
 *        Buy-product request DTO mapped to POST /investment/orders/buy request body.
 */
public record BuyProductRequestDto(
        @JsonProperty("investment_account_id") String investmentAccountId,
        @JsonProperty("product_code") String productCode,
        @JsonProperty("quantity") BigDecimal quantity,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("fee_amount") BigDecimal feeAmount,
        @JsonProperty("initiator_customer_id") String initiatorCustomerId,
        @JsonProperty("channel") String channel,
        @JsonProperty("reference_no") String referenceNo,
        @JsonProperty("customer_risk_tolerance") String customerRiskTolerance,
        @JsonProperty("trade_at") Instant tradeAt
) {

    /**
     * @brief 规范化并校验请求字段（Normalize and Validate Request Fields）；
     *        Normalize and validate buy-product request fields.
     */
    public BuyProductRequestDto {
        investmentAccountId = InvestmentWebPayloadParser.normalizeRequiredText(
                investmentAccountId,
                "investment_account_id");
        productCode = InvestmentWebPayloadParser.normalizeRequiredText(productCode, "product_code");
        quantity = Objects.requireNonNull(quantity, "quantity must not be null");
        price = Objects.requireNonNull(price, "price must not be null");
        initiatorCustomerId = InvestmentWebPayloadParser.normalizeOptionalText(initiatorCustomerId);
        channel = InvestmentWebPayloadParser.normalizeOptionalText(channel);
        referenceNo = InvestmentWebPayloadParser.normalizeOptionalText(referenceNo);
        customerRiskTolerance = InvestmentWebPayloadParser.normalizeOptionalText(customerRiskTolerance);
    }

    /**
     * @brief 转换为应用层命令（Map to Application Command）；
     *        Map request DTO to buy-product command.
     *
     * @return 买入命令（Buy-product command）。
     */
    public BuyProductCommand toCommand() {
        final BusinessChannel parsedChannel = InvestmentWebPayloadParser.parseOptionalBusinessChannel(channel);
        final RiskLevel parsedRiskTolerance = InvestmentWebPayloadParser.parseOptionalRiskLevel(customerRiskTolerance);
        return new BuyProductCommand(
                investmentAccountId,
                productCode,
                quantity,
                price,
                feeAmount,
                initiatorCustomerId,
                parsedChannel,
                referenceNo,
                parsedRiskTolerance,
                tradeAt);
    }
}
