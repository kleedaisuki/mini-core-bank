package com.moesegfault.banking.presentation.web.investment;

/**
 * @brief 投资 Web 统一 Schema 常量（Investment Web Unified Schema Constants）；
 *        Unified schema constants for investment web routes and query keys.
 */
final class InvestmentWebSchema {

    /**
     * @brief 投资路由基础路径（Investment Base Route Path）；
     *        Base route path for investment APIs.
     */
    static final String BASE_PATH = "/investment";

    /**
     * @brief 创建产品路由（Create Product Route Path）；
     *        Route path for creating one investment product.
     */
    static final String PATH_PRODUCTS = BASE_PATH + "/products";

    /**
     * @brief 买入下单路由（Buy Order Route Path）；
     *        Route path for placing one buy order.
     */
    static final String PATH_BUY_ORDER = BASE_PATH + "/orders/buy";

    /**
     * @brief 卖出下单路由（Sell Order Route Path）；
     *        Route path for placing one sell order.
     */
    static final String PATH_SELL_ORDER = BASE_PATH + "/orders/sell";

    /**
     * @brief 持仓查询路由（Holdings Query Route Path）；
     *        Route path for listing holdings under one investment account.
     */
    static final String PATH_HOLDINGS = BASE_PATH + "/accounts/{investmentAccountId}/holdings";

    /**
     * @brief 持仓查询路径参数名（Holdings Path Parameter Name）；
     *        Path parameter name for investment-account identifier.
     */
    static final String PATH_PARAM_INVESTMENT_ACCOUNT_ID = "investmentAccountId";

    /**
     * @brief 包含产品详情查询参数（Include Product Details Query Key）；
     *        Query key controlling whether product details are enriched.
     */
    static final String QUERY_INCLUDE_PRODUCT_DETAILS = "include_product_details";

    /**
     * @brief 禁止实例化（No Instance）；
     *        Prevent instantiation.
     */
    private InvestmentWebSchema() {
    }
}
