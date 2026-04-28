package com.moesegfault.banking.application.investment;

import com.moesegfault.banking.domain.business.BusinessTypeCode;

/**
 * @brief 投资应用业务类型码常量（Investment Application Business Type Code Constants）；
 *        Business-type-code constants used by investment application workflows.
 */
public final class InvestmentBusinessTypeCodes {

    /**
     * @brief 买入产品业务类型码（Buy Product Business Type Code）；
     *        Business type code for buy-product transaction.
     */
    public static final BusinessTypeCode BUY_PRODUCT = BusinessTypeCode.of("BUY_PRODUCT");

    /**
     * @brief 卖出产品业务类型码（Sell Product Business Type Code）；
     *        Business type code for sell-product transaction.
     */
    public static final BusinessTypeCode SELL_PRODUCT = BusinessTypeCode.of("SELL_PRODUCT");

    /**
     * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
     *        Utility class should not be instantiated.
     */
    private InvestmentBusinessTypeCodes() {
    }
}
