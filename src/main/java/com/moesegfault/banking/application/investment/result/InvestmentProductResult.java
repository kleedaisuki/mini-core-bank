package com.moesegfault.banking.application.investment.result;

import com.moesegfault.banking.domain.investment.InvestmentProduct;
import java.util.Objects;

/**
 * @brief 投资产品结果视图（Investment Product Result View）；
 *        Read model for investment product output.
 */
public final class InvestmentProductResult {

    /**
     * @brief 产品 ID（Product ID）；
     *        Product identifier string.
     */
    private final String productId;

    /**
     * @brief 产品代码（Product Code）；
     *        Product code string.
     */
    private final String productCode;

    /**
     * @brief 产品名称（Product Name）；
     *        Product name.
     */
    private final String productName;

    /**
     * @brief 产品类型（Product Type）；
     *        Product type name.
     */
    private final String productType;

    /**
     * @brief 币种代码（Currency Code）；
     *        Product currency code.
     */
    private final String currencyCode;

    /**
     * @brief 风险等级（Risk Level）；
     *        Risk-level name.
     */
    private final String riskLevel;

    /**
     * @brief 发行方（Issuer）；
     *        Product issuer.
     */
    private final String issuer;

    /**
     * @brief 产品状态（Product Status）；
     *        Product status name.
     */
    private final String productStatus;

    /**
     * @brief 构造投资产品结果（Construct Product Result）；
     *        Construct investment product result view.
     *
     * @param productId    产品 ID（Product ID）。
     * @param productCode  产品代码（Product code）。
     * @param productName  产品名称（Product name）。
     * @param productType  产品类型（Product type）。
     * @param currencyCode 币种代码（Currency code）。
     * @param riskLevel    风险等级（Risk level）。
     * @param issuer       发行方（Issuer）。
     * @param productStatus 产品状态（Product status）。
     */
    public InvestmentProductResult(
            final String productId,
            final String productCode,
            final String productName,
            final String productType,
            final String currencyCode,
            final String riskLevel,
            final String issuer,
            final String productStatus
    ) {
        this.productId = Objects.requireNonNull(productId, "productId must not be null");
        this.productCode = Objects.requireNonNull(productCode, "productCode must not be null");
        this.productName = Objects.requireNonNull(productName, "productName must not be null");
        this.productType = Objects.requireNonNull(productType, "productType must not be null");
        this.currencyCode = Objects.requireNonNull(currencyCode, "currencyCode must not be null");
        this.riskLevel = Objects.requireNonNull(riskLevel, "riskLevel must not be null");
        this.issuer = Objects.requireNonNull(issuer, "issuer must not be null");
        this.productStatus = Objects.requireNonNull(productStatus, "productStatus must not be null");
    }

    /**
     * @brief 从产品实体映射结果（Map from Product Entity）；
     *        Map result from investment-product entity.
     *
     * @param product 产品实体（Investment product entity）。
     * @return 产品结果（Product result view）。
     */
    public static InvestmentProductResult from(final InvestmentProduct product) {
        final InvestmentProduct normalized = Objects.requireNonNull(product, "product must not be null");
        return new InvestmentProductResult(
                normalized.productId().value(),
                normalized.productCode().value(),
                normalized.productName(),
                normalized.productType().name(),
                normalized.currencyCode().value(),
                normalized.riskLevel().name(),
                normalized.issuer(),
                normalized.productStatus().name());
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
    public String productType() {
        return productType;
    }

    /**
     * @brief 返回币种代码（Return Currency Code）；
     *        Return currency code.
     *
     * @return 币种代码（Currency code）。
     */
    public String currencyCode() {
        return currencyCode;
    }

    /**
     * @brief 返回风险等级（Return Risk Level）；
     *        Return risk level.
     *
     * @return 风险等级（Risk level）。
     */
    public String riskLevel() {
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

    /**
     * @brief 返回产品状态（Return Product Status）；
     *        Return product status.
     *
     * @return 产品状态（Product status）。
     */
    public String productStatus() {
        return productStatus;
    }
}
