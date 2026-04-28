package com.moesegfault.banking.domain.investment;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @brief 产品估值实体（Product Valuation Entity），对应 `product_valuation` 的联合主键与净值语义；
 *        Product-valuation entity aligned with composite-key and NAV semantics in `product_valuation`.
 */
public final class ProductValuation {

    /**
     * @brief 产品 ID（Product ID）；
     *        Product identifier.
     */
    private final ProductId productId;

    /**
     * @brief 估值日期（Valuation Date）；
     *        Valuation date.
     */
    private final LocalDate valuationDate;

    /**
     * @brief 产品净值（Net Asset Value）；
     *        Product net asset value.
     */
    private final NetAssetValue netAssetValue;

    /**
     * @brief 构造产品估值实体（Construct Product Valuation Entity）；
     *        Construct product-valuation entity.
     *
     * @param productId     产品 ID（Product ID）。
     * @param valuationDate 估值日期（Valuation date）。
     * @param netAssetValue 产品净值（Product NAV）。
     */
    private ProductValuation(
            final ProductId productId,
            final LocalDate valuationDate,
            final NetAssetValue netAssetValue
    ) {
        this.productId = Objects.requireNonNull(productId, "Product ID must not be null");
        this.valuationDate = Objects.requireNonNull(valuationDate, "Valuation date must not be null");
        this.netAssetValue = Objects.requireNonNull(netAssetValue, "Net asset value must not be null");
    }

    /**
     * @brief 创建产品估值（Create Product Valuation）；
     *        Create product valuation.
     *
     * @param productId     产品 ID（Product ID）。
     * @param valuationDate 估值日期（Valuation date）。
     * @param netAssetValue 产品净值（Product NAV）。
     * @return 产品估值实体（Product-valuation entity）。
     */
    public static ProductValuation of(
            final ProductId productId,
            final LocalDate valuationDate,
            final NetAssetValue netAssetValue
    ) {
        return new ProductValuation(productId, valuationDate, netAssetValue);
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
     * @brief 返回估值日期（Return Valuation Date）；
     *        Return valuation date.
     *
     * @return 估值日期（Valuation date）。
     */
    public LocalDate valuationDate() {
        return valuationDate;
    }

    /**
     * @brief 返回净值（Return Net Asset Value）；
     *        Return net asset value.
     *
     * @return 净值值对象（NAV value object）。
     */
    public NetAssetValue netAssetValue() {
        return netAssetValue;
    }
}
