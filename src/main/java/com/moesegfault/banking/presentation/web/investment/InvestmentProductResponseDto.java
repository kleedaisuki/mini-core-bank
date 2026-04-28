package com.moesegfault.banking.presentation.web.investment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.investment.result.InvestmentProductResult;
import java.util.Objects;

/**
 * @brief 投资产品响应 DTO（Investment Product Response DTO），用于产品创建成功响应；
 *        Investment-product response DTO for create-product success response.
 */
public record InvestmentProductResponseDto(
        @JsonProperty("product_id") String productId,
        @JsonProperty("product_code") String productCode,
        @JsonProperty("product_name") String productName,
        @JsonProperty("product_type") String productType,
        @JsonProperty("currency_code") String currencyCode,
        @JsonProperty("risk_level") String riskLevel,
        @JsonProperty("issuer") String issuer,
        @JsonProperty("product_status") String productStatus
) {

    /**
     * @brief 规范化并校验响应字段（Normalize and Validate Response Fields）；
     *        Normalize and validate investment-product response fields.
     */
    public InvestmentProductResponseDto {
        productId = normalizeRequiredText(productId, "product_id");
        productCode = normalizeRequiredText(productCode, "product_code");
        productName = normalizeRequiredText(productName, "product_name");
        productType = normalizeRequiredText(productType, "product_type");
        currencyCode = normalizeRequiredText(currencyCode, "currency_code");
        riskLevel = normalizeRequiredText(riskLevel, "risk_level");
        issuer = normalizeRequiredText(issuer, "issuer");
        productStatus = normalizeRequiredText(productStatus, "product_status");
    }

    /**
     * @brief 从应用层结果映射响应 DTO（Map from Application Result）；
     *        Map investment-product result to response DTO.
     *
     * @param result 投资产品结果（Investment product result）。
     * @return 投资产品响应 DTO（Investment-product response DTO）。
     */
    public static InvestmentProductResponseDto fromResult(final InvestmentProductResult result) {
        final InvestmentProductResult normalized = Objects.requireNonNull(result, "result must not be null");
        return new InvestmentProductResponseDto(
                normalized.productId(),
                normalized.productCode(),
                normalized.productName(),
                normalized.productType(),
                normalized.currencyCode(),
                normalized.riskLevel(),
                normalized.issuer(),
                normalized.productStatus());
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
