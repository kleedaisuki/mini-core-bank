package com.moesegfault.banking.domain.investment;

import com.moesegfault.banking.domain.shared.EntityId;

/**
 * @brief 投资产品标识值对象（Investment Product Identifier Value Object），对应 `investment_product.product_id`；
 *        Investment-product identifier value object mapped to `investment_product.product_id`.
 */
public final class ProductId extends EntityId<ProductId> {

    /**
     * @brief 构造产品标识（Construct Product Identifier）；
     *        Construct product identifier.
     *
     * @param value 产品 ID 值（Product ID value）。
     */
    private ProductId(final String value) {
        super(value);
    }

    /**
     * @brief 从原始字符串创建产品标识（Factory from Raw String）；
     *        Create product identifier from raw string.
     *
     * @param rawValue 原始 ID（Raw ID value）。
     * @return 产品标识值对象（Product identifier value object）。
     */
    public static ProductId of(final String rawValue) {
        return new ProductId(rawValue);
    }
}
