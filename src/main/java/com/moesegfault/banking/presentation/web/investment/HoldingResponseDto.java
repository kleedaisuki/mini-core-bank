package com.moesegfault.banking.presentation.web.investment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.investment.result.HoldingResult;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 持仓响应 DTO（Holding Response DTO），用于单条持仓输出；
 *        Holding response DTO for one holding item output.
 */
public record HoldingResponseDto(
        @JsonProperty("holding_id") String holdingId,
        @JsonProperty("investment_account_id") String investmentAccountId,
        @JsonProperty("product_id") String productId,
        @JsonProperty("product_code") String productCode,
        @JsonProperty("product_name") String productName,
        @JsonProperty("product_type") String productType,
        @JsonProperty("quantity") BigDecimal quantity,
        @JsonProperty("average_cost") BigDecimal averageCost,
        @JsonProperty("cost_currency_code") String costCurrencyCode,
        @JsonProperty("market_value") BigDecimal marketValue,
        @JsonProperty("valuation_currency_code") String valuationCurrencyCode,
        @JsonProperty("unrealized_pnl") BigDecimal unrealizedPnl,
        @JsonProperty("updated_at") Instant updatedAt
) {

    /**
     * @brief 规范化并校验响应字段（Normalize and Validate Response Fields）；
     *        Normalize and validate holding-response fields.
     */
    public HoldingResponseDto {
        holdingId = normalizeRequiredText(holdingId, "holding_id");
        investmentAccountId = normalizeRequiredText(investmentAccountId, "investment_account_id");
        productId = normalizeRequiredText(productId, "product_id");
        productCode = InvestmentWebPayloadParser.normalizeOptionalText(productCode);
        productName = InvestmentWebPayloadParser.normalizeOptionalText(productName);
        productType = InvestmentWebPayloadParser.normalizeOptionalText(productType);
        quantity = Objects.requireNonNull(quantity, "quantity must not be null");
        averageCost = Objects.requireNonNull(averageCost, "average_cost must not be null");
        costCurrencyCode = normalizeRequiredText(costCurrencyCode, "cost_currency_code");
        marketValue = Objects.requireNonNull(marketValue, "market_value must not be null");
        valuationCurrencyCode = normalizeRequiredText(valuationCurrencyCode, "valuation_currency_code");
        unrealizedPnl = Objects.requireNonNull(unrealizedPnl, "unrealized_pnl must not be null");
        updatedAt = Objects.requireNonNull(updatedAt, "updated_at must not be null");
    }

    /**
     * @brief 从应用层结果映射响应 DTO（Map from Application Result）；
     *        Map holding result to holding response DTO.
     *
     * @param result 持仓结果（Holding result）。
     * @return 持仓响应 DTO（Holding response DTO）。
     */
    public static HoldingResponseDto fromResult(final HoldingResult result) {
        final HoldingResult normalized = Objects.requireNonNull(result, "result must not be null");
        return new HoldingResponseDto(
                normalized.holdingId(),
                normalized.investmentAccountId(),
                normalized.productId(),
                normalized.productCodeOrNull(),
                normalized.productNameOrNull(),
                normalized.productTypeOrNull(),
                normalized.quantity(),
                normalized.averageCost(),
                normalized.costCurrencyCode(),
                normalized.marketValue(),
                normalized.valuationCurrencyCode(),
                normalized.unrealizedPnl(),
                normalized.updatedAt());
    }

    /**
     * @brief 规范化必填文本（Normalize Required Text）；
     *        Normalize required text and reject blank value.
     *
     * @param rawValue 原始值（Raw value）。
     * @param fieldName 字段名（Field name）。
     * @return 规范化文本（Normalized text）。
     */
    private static String normalizeRequiredText(final String rawValue, final String fieldName) {
        return InvestmentWebPayloadParser.normalizeRequiredText(rawValue, fieldName);
    }
}
