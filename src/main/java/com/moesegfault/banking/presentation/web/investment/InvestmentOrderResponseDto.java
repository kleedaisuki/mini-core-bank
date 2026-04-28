package com.moesegfault.banking.presentation.web.investment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.investment.result.InvestmentOrderResult;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 投资订单响应 DTO（Investment Order Response DTO），用于买入/卖出成功响应；
 *        Investment-order response DTO for buy/sell success responses.
 */
public record InvestmentOrderResponseDto(
        @JsonProperty("order_id") String orderId,
        @JsonProperty("reference_no") String referenceNo,
        @JsonProperty("transaction_status") String transactionStatus,
        @JsonProperty("investment_account_id") String investmentAccountId,
        @JsonProperty("product_id") String productId,
        @JsonProperty("product_code") String productCode,
        @JsonProperty("order_side") String orderSide,
        @JsonProperty("quantity") BigDecimal quantity,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("gross_amount") BigDecimal grossAmount,
        @JsonProperty("fee_amount") BigDecimal feeAmount,
        @JsonProperty("currency_code") String currencyCode,
        @JsonProperty("order_status") String orderStatus,
        @JsonProperty("trade_at") Instant tradeAt,
        @JsonProperty("settlement_at") Instant settlementAt,
        @JsonProperty("cash_impact") BigDecimal cashImpact,
        @JsonProperty("holding_quantity_after") BigDecimal holdingQuantityAfter
) {

    /**
     * @brief 规范化并校验响应字段（Normalize and Validate Response Fields）；
     *        Normalize and validate investment-order response fields.
     */
    public InvestmentOrderResponseDto {
        orderId = normalizeRequiredText(orderId, "order_id");
        referenceNo = normalizeRequiredText(referenceNo, "reference_no");
        transactionStatus = normalizeRequiredText(transactionStatus, "transaction_status");
        investmentAccountId = normalizeRequiredText(investmentAccountId, "investment_account_id");
        productId = normalizeRequiredText(productId, "product_id");
        productCode = normalizeRequiredText(productCode, "product_code");
        orderSide = normalizeRequiredText(orderSide, "order_side");
        quantity = Objects.requireNonNull(quantity, "quantity must not be null");
        price = Objects.requireNonNull(price, "price must not be null");
        grossAmount = Objects.requireNonNull(grossAmount, "gross_amount must not be null");
        feeAmount = Objects.requireNonNull(feeAmount, "fee_amount must not be null");
        currencyCode = normalizeRequiredText(currencyCode, "currency_code");
        orderStatus = normalizeRequiredText(orderStatus, "order_status");
        tradeAt = Objects.requireNonNull(tradeAt, "trade_at must not be null");
        cashImpact = Objects.requireNonNull(cashImpact, "cash_impact must not be null");
        holdingQuantityAfter = Objects.requireNonNull(
                holdingQuantityAfter,
                "holding_quantity_after must not be null");
    }

    /**
     * @brief 从应用层结果映射响应 DTO（Map from Application Result）；
     *        Map investment-order result to response DTO.
     *
     * @param result 投资订单结果（Investment order result）。
     * @return 投资订单响应 DTO（Investment-order response DTO）。
     */
    public static InvestmentOrderResponseDto fromResult(final InvestmentOrderResult result) {
        final InvestmentOrderResult normalized = Objects.requireNonNull(result, "result must not be null");
        return new InvestmentOrderResponseDto(
                normalized.orderId(),
                normalized.referenceNo(),
                normalized.transactionStatus(),
                normalized.investmentAccountId(),
                normalized.productId(),
                normalized.productCode(),
                normalized.orderSide(),
                normalized.quantity(),
                normalized.price(),
                normalized.grossAmount(),
                normalized.feeAmount(),
                normalized.currencyCode(),
                normalized.orderStatus(),
                normalized.tradeAt(),
                normalized.settlementAtOrNull(),
                normalized.cashImpact(),
                normalized.holdingQuantityAfter());
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
