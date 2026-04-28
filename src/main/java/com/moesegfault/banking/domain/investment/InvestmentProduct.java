package com.moesegfault.banking.domain.investment;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 投资产品实体（Investment Product Entity），映射 `investment_product` 并维护产品状态不变量（Invariant）；
 *        Investment-product entity mapped to `investment_product` and enforcing status invariants.
 */
public final class InvestmentProduct {

    /**
     * @brief 产品 ID（Product ID）；
     *        Product identifier.
     */
    private final ProductId productId;

    /**
     * @brief 产品代码（Product Code）；
     *        Product code.
     */
    private final ProductCode productCode;

    /**
     * @brief 产品名称（Product Name）；
     *        Product name.
     */
    private final String productName;

    /**
     * @brief 产品类型（Product Type）；
     *        Product type.
     */
    private final ProductType productType;

    /**
     * @brief 产品币种（Product Currency Code）；
     *        Product currency code.
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
     * @brief 产品状态（Product Status）；
     *        Product status.
     */
    private ProductStatus productStatus;

    /**
     * @brief 构造投资产品实体（Construct Investment Product Entity）；
     *        Construct investment-product entity.
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
    private InvestmentProduct(
            final ProductId productId,
            final ProductCode productCode,
            final String productName,
            final ProductType productType,
            final CurrencyCode currencyCode,
            final RiskLevel riskLevel,
            final String issuer,
            final ProductStatus productStatus
    ) {
        this.productId = Objects.requireNonNull(productId, "Product ID must not be null");
        this.productCode = Objects.requireNonNull(productCode, "Product code must not be null");
        this.productName = normalizeText(productName, 128, "Product name");
        this.productType = Objects.requireNonNull(productType, "Product type must not be null");
        this.currencyCode = Objects.requireNonNull(currencyCode, "Currency code must not be null");
        this.riskLevel = Objects.requireNonNull(riskLevel, "Risk level must not be null");
        this.issuer = normalizeText(issuer, 128, "Issuer");
        this.productStatus = Objects.requireNonNull(productStatus, "Product status must not be null");
    }

    /**
     * @brief 创建投资产品（Create Investment Product）；
     *        Create investment product with default status `ACTIVE`.
     *
     * @param productId    产品 ID（Product ID）。
     * @param productCode  产品代码（Product code）。
     * @param productName  产品名称（Product name）。
     * @param productType  产品类型（Product type）。
     * @param currencyCode 币种代码（Currency code）。
     * @param riskLevel    风险等级（Risk level）。
     * @param issuer       发行方（Issuer）。
     * @return 投资产品实体（Investment-product entity）。
     */
    public static InvestmentProduct create(
            final ProductId productId,
            final ProductCode productCode,
            final String productName,
            final ProductType productType,
            final CurrencyCode currencyCode,
            final RiskLevel riskLevel,
            final String issuer
    ) {
        return new InvestmentProduct(
                productId,
                productCode,
                productName,
                productType,
                currencyCode,
                riskLevel,
                issuer,
                ProductStatus.ACTIVE);
    }

    /**
     * @brief 从持久化状态重建产品（Restore Product from Persistence）；
     *        Restore product from persistence state.
     *
     * @param productId    产品 ID（Product ID）。
     * @param productCode  产品代码（Product code）。
     * @param productName  产品名称（Product name）。
     * @param productType  产品类型（Product type）。
     * @param currencyCode 币种代码（Currency code）。
     * @param riskLevel    风险等级（Risk level）。
     * @param issuer       发行方（Issuer）。
     * @param productStatus 产品状态（Product status）。
     * @return 投资产品实体（Investment-product entity）。
     */
    public static InvestmentProduct restore(
            final ProductId productId,
            final ProductCode productCode,
            final String productName,
            final ProductType productType,
            final CurrencyCode currencyCode,
            final RiskLevel riskLevel,
            final String issuer,
            final ProductStatus productStatus
    ) {
        return new InvestmentProduct(
                productId,
                productCode,
                productName,
                productType,
                currencyCode,
                riskLevel,
                issuer,
                productStatus);
    }

    /**
     * @brief 激活产品（Activate Product）；
     *        Activate product from `INACTIVE`.
     */
    public void activate() {
        if (productStatus == ProductStatus.ACTIVE) {
            return;
        }
        if (productStatus != ProductStatus.INACTIVE) {
            throw new BusinessRuleViolation("Only INACTIVE product can be activated");
        }
        productStatus = ProductStatus.ACTIVE;
    }

    /**
     * @brief 暂停产品（Deactivate Product）；
     *        Deactivate product from `ACTIVE`.
     */
    public void deactivate() {
        if (productStatus == ProductStatus.INACTIVE) {
            return;
        }
        if (productStatus != ProductStatus.ACTIVE) {
            throw new BusinessRuleViolation("Only ACTIVE product can be deactivated");
        }
        productStatus = ProductStatus.INACTIVE;
    }

    /**
     * @brief 关闭产品（Close Product）；
     *        Close product permanently.
     */
    public void close() {
        if (productStatus == ProductStatus.CLOSED) {
            return;
        }
        productStatus = ProductStatus.CLOSED;
    }

    /**
     * @brief 校验产品是否允许交易（Ensure Tradable Product）；
     *        Ensure product can be traded.
     */
    public void ensureTradable() {
        if (!productStatus.isTradable()) {
            throw new BusinessRuleViolation(
                    "Product " + productCode + " cannot be traded when status is " + productStatus);
        }
    }

    /**
     * @brief 构建产品创建事件（Build Product Created Event）；
     *        Build product-created event.
     *
     * @return 产品创建事件（Product-created event）。
     */
    public InvestmentProductCreated createdEvent() {
        return new InvestmentProductCreated(
                productId,
                productCode,
                productType,
                currencyCode,
                riskLevel,
                Instant.now());
    }

    /**
     * @brief 返回产品 ID（Return Product ID）；
     *        Return product identifier.
     *
     * @return 产品 ID（Product ID）。
     */
    public ProductId productId() {
        return productId;
    }

    /**
     * @brief 返回产品代码（Return Product Code）；
     *        Return product code.
     *
     * @return 产品代码（Product code）。
     */
    public ProductCode productCode() {
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
     * @brief 返回币种代码（Return Currency Code）；
     *        Return currency code.
     *
     * @return 币种代码（Currency code）。
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

    /**
     * @brief 返回产品状态（Return Product Status）；
     *        Return product status.
     *
     * @return 产品状态（Product status）。
     */
    public ProductStatus productStatus() {
        return productStatus;
    }

    /**
     * @brief 标准化并校验文本字段（Normalize and Validate Text Field）；
     *        Normalize and validate text field.
     *
     * @param rawText   原始文本（Raw text）。
     * @param maxLength 最大长度（Max length）。
     * @param fieldName 字段名（Field name）。
     * @return 标准化文本（Normalized text）。
     */
    private static String normalizeText(
            final String rawText,
            final int maxLength,
            final String fieldName
    ) {
        if (rawText == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        final String normalized = rawText.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " length must be <= " + maxLength);
        }
        return normalized;
    }
}
