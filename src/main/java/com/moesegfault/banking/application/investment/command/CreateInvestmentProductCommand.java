package com.moesegfault.banking.application.investment.command;

import com.moesegfault.banking.domain.investment.ProductType;
import com.moesegfault.banking.domain.investment.RiskLevel;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import java.util.Objects;

/**
 * @brief 创建投资产品命令（Create Investment Product Command）；
 *        Command object for creating a new investment product.
 */
public final class CreateInvestmentProductCommand {

    /**
     * @brief 产品代码（Product Code）；
     *        Product code in ubiquitous language.
     */
    private final String productCode;

    /**
     * @brief 产品名称（Product Name）；
     *        Product display name.
     */
    private final String productName;

    /**
     * @brief 产品类型（Product Type）；
     *        Product type enum.
     */
    private final ProductType productType;

    /**
     * @brief 产品币种（Product Currency Code）；
     *        Product currency code value object.
     */
    private final CurrencyCode currencyCode;

    /**
     * @brief 风险等级（Risk Level）；
     *        Product risk level.
     */
    private final RiskLevel riskLevel;

    /**
     * @brief 发行方（Issuer）；
     *        Product issuer.
     */
    private final String issuer;

    /**
     * @brief 构造创建产品命令（Construct Create-Product Command）；
     *        Construct create-investment-product command.
     *
     * @param productCode  产品代码（Product code）。
     * @param productName  产品名称（Product name）。
     * @param productType  产品类型（Product type）。
     * @param currencyCode 产品币种（Currency code）。
     * @param riskLevel    风险等级（Risk level）。
     * @param issuer       发行方（Issuer）。
     */
    public CreateInvestmentProductCommand(
            final String productCode,
            final String productName,
            final ProductType productType,
            final CurrencyCode currencyCode,
            final RiskLevel riskLevel,
            final String issuer
    ) {
        this.productCode = Objects.requireNonNull(productCode, "productCode must not be null");
        this.productName = Objects.requireNonNull(productName, "productName must not be null");
        this.productType = Objects.requireNonNull(productType, "productType must not be null");
        this.currencyCode = Objects.requireNonNull(currencyCode, "currencyCode must not be null");
        this.riskLevel = Objects.requireNonNull(riskLevel, "riskLevel must not be null");
        this.issuer = Objects.requireNonNull(issuer, "issuer must not be null");
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
     * @brief 返回产品名称（Return Product Name）；
     *        Return product name.
     *
     * @return 产品名称（Product name）。
     */
    public String productName() {
        return productName;
    }

    /**
     * @brief 返回产品类型（Return Product Type）；
     *        Return product type.
     *
     * @return 产品类型（Product type）。
     */
    public ProductType productType() {
        return productType;
    }

    /**
     * @brief 返回产品币种（Return Currency Code）；
     *        Return product currency code.
     *
     * @return 产品币种（Currency code）。
     */
    public CurrencyCode currencyCode() {
        return currencyCode;
    }

    /**
     * @brief 返回风险等级（Return Risk Level）；
     *        Return risk level.
     *
     * @return 风险等级（Risk level）。
     */
    public RiskLevel riskLevel() {
        return riskLevel;
    }

    /**
     * @brief 返回发行方（Return Issuer）；
     *        Return issuer.
     *
     * @return 发行方（Issuer）。
     */
    public String issuer() {
        return issuer;
    }
}
