package com.moesegfault.banking.domain.investment;

import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 投资产品创建事件（Investment Product Created Event）；
 *        Investment-product-created event.
 */
public final class InvestmentProductCreated implements DomainEvent {

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
     * @brief 产品类型（Product Type）；
     *        Product type.
     */
    private final ProductType productType;

    /**
     * @brief 币种代码（Currency Code）；
     *        Currency code.
     */
    private final CurrencyCode currencyCode;

    /**
     * @brief 风险等级（Risk Level）；
     *        Risk level.
     */
    private final RiskLevel riskLevel;

    /**
     * @brief 事件时间（Occurred Timestamp）；
     *        Event occurred timestamp.
     */
    private final Instant occurredAt;

    /**
     * @brief 构造产品创建事件（Construct Product Created Event）；
     *        Construct investment-product-created event.
     *
     * @param productId    产品 ID（Product ID）。
     * @param productCode  产品代码（Product code）。
     * @param productType  产品类型（Product type）。
     * @param currencyCode 币种代码（Currency code）。
     * @param riskLevel    风险等级（Risk level）。
     * @param occurredAt   事件时间（Occurred timestamp）。
     */
    public InvestmentProductCreated(
            final ProductId productId,
            final ProductCode productCode,
            final ProductType productType,
            final CurrencyCode currencyCode,
            final RiskLevel riskLevel,
            final Instant occurredAt
    ) {
        this.productId = Objects.requireNonNull(productId, "Product ID must not be null");
        this.productCode = Objects.requireNonNull(productCode, "Product code must not be null");
        this.productType = Objects.requireNonNull(productType, "Product type must not be null");
        this.currencyCode = Objects.requireNonNull(currencyCode, "Currency code must not be null");
        this.riskLevel = Objects.requireNonNull(riskLevel, "Risk level must not be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred-at must not be null");
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
     * @brief 返回事件时间（Return Occurred Timestamp）；
     *        Return occurred timestamp.
     *
     * @return 事件时间（Occurred timestamp）。
     */
    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
