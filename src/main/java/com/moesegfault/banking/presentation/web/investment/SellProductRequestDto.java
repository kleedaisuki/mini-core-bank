package com.moesegfault.banking.presentation.web.investment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.investment.command.SellProductCommand;
import com.moesegfault.banking.domain.business.BusinessChannel;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 投资卖出请求 DTO（Sell Product Request DTO），映射 POST /investment/orders/sell 请求体；
 *        Sell-product request DTO mapped to POST /investment/orders/sell request body.
 */
public record SellProductRequestDto(
        @JsonProperty("investment_account_id") String investmentAccountId,
        @JsonProperty("product_code") String productCode,
        @JsonProperty("quantity") BigDecimal quantity,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("fee_amount") BigDecimal feeAmount,
        @JsonProperty("initiator_customer_id") String initiatorCustomerId,
        @JsonProperty("channel") String channel,
        @JsonProperty("reference_no") String referenceNo,
        @JsonProperty("trade_at") Instant tradeAt
) {

    /**
     * @brief 规范化并校验请求字段（Normalize and Validate Request Fields）；
     *        Normalize and validate sell-product request fields.
     */
    public SellProductRequestDto {
        investmentAccountId = InvestmentWebPayloadParser.normalizeRequiredText(
                investmentAccountId,
                "investment_account_id");
        productCode = InvestmentWebPayloadParser.normalizeRequiredText(productCode, "product_code");
        quantity = Objects.requireNonNull(quantity, "quantity must not be null");
        price = Objects.requireNonNull(price, "price must not be null");
        initiatorCustomerId = InvestmentWebPayloadParser.normalizeOptionalText(initiatorCustomerId);
        channel = InvestmentWebPayloadParser.normalizeOptionalText(channel);
        referenceNo = InvestmentWebPayloadParser.normalizeOptionalText(referenceNo);
    }

    /**
     * @brief 转换为应用层命令（Map to Application Command）；
     *        Map request DTO to sell-product command.
     *
     * @return 卖出命令（Sell-product command）。
     */
    public SellProductCommand toCommand() {
        final BusinessChannel parsedChannel = InvestmentWebPayloadParser.parseOptionalBusinessChannel(channel);
        return new SellProductCommand(
                investmentAccountId,
                productCode,
                quantity,
                price,
                feeAmount,
                initiatorCustomerId,
                parsedChannel,
                referenceNo,
                tradeAt);
    }
}
