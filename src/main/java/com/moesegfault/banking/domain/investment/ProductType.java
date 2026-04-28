package com.moesegfault.banking.domain.investment;

/**
 * @brief 投资产品类型枚举（Investment Product Type Enum），对齐 `investment_product.product_type` 领域语言；
 *        Investment-product-type enum aligned with domain language for `investment_product.product_type`.
 */
public enum ProductType {

    /**
     * @brief 基金（Fund）；
     *        Fund product.
     */
    FUND,

    /**
     * @brief 债券（Bond）；
     *        Bond product.
     */
    BOND,

    /**
     * @brief 结构性产品（Structured Product）；
     *        Structured product.
     */
    STRUCTURED_PRODUCT,

    /**
     * @brief 理财产品（Wealth Management）；
     *        Wealth-management product.
     */
    WEALTH_MANAGEMENT,

    /**
     * @brief 定期类产品（Time Deposit）；
     *        Time-deposit-like product.
     */
    TIME_DEPOSIT
}
