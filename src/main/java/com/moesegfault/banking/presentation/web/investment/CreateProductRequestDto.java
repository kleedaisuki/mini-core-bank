package com.moesegfault.banking.presentation.web.investment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.investment.command.CreateInvestmentProductCommand;
import com.moesegfault.banking.domain.shared.CurrencyCode;

/**
 * @brief 创建投资产品请求 DTO（Create Product Request DTO），映射 POST /investment/products 请求体；
 *        Create-product request DTO mapped to POST /investment/products request body.
 */
public record CreateProductRequestDto(
        @JsonProperty("product_code") String productCode,
        @JsonProperty("product_name") String productName,
        @JsonProperty("product_type") String productType,
        @JsonProperty("currency_code") String currencyCode,
        @JsonProperty("risk_level") String riskLevel,
        @JsonProperty("issuer") String issuer
) {

    /**
     * @brief 规范化并校验请求字段（Normalize and Validate Request Fields）；
     *        Normalize and validate create-product request fields.
     */
    public CreateProductRequestDto {
        productCode = InvestmentWebPayloadParser.normalizeRequiredText(productCode, "product_code");
        productName = InvestmentWebPayloadParser.normalizeRequiredText(productName, "product_name");
        productType = InvestmentWebPayloadParser.normalizeRequiredText(productType, "product_type");
        currencyCode = InvestmentWebPayloadParser.normalizeRequiredText(currencyCode, "currency_code");
        riskLevel = InvestmentWebPayloadParser.normalizeRequiredText(riskLevel, "risk_level");
        issuer = InvestmentWebPayloadParser.normalizeRequiredText(issuer, "issuer");
    }

    /**
     * @brief 转换为应用层命令（Map to Application Command）；
     *        Map request DTO to create-investment-product command.
     *
     * @return 创建产品命令（Create-product command）。
     */
    public CreateInvestmentProductCommand toCommand() {
        return new CreateInvestmentProductCommand(
                productCode,
                productName,
                InvestmentWebPayloadParser.parseRequiredProductType(productType, "product_type"),
                CurrencyCode.of(currencyCode),
                InvestmentWebPayloadParser.parseRequiredRiskLevel(riskLevel, "risk_level"),
                issuer);
    }
}
