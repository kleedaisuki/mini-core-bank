package com.moesegfault.banking.presentation.gui.investment;

/**
 * @brief 投资 GUI 统一 Schema 常量（Investment GUI Unified Schema Constants）；
 *        Unified schema constants for investment GUI page ids, event types and payload keys.
 */
public final class InvestmentGuiSchema {

    /**
     * @brief 创建产品页面标识（Create Product Page ID）；
     *        Page identifier for create-product page.
     */
    public static final String PAGE_CREATE_PRODUCT = "investment.product-create";

    /**
     * @brief 买入页面标识（Buy Product Page ID）；
     *        Page identifier for buy-product page.
     */
    public static final String PAGE_BUY_PRODUCT = "investment.buy";

    /**
     * @brief 卖出页面标识（Sell Product Page ID）；
     *        Page identifier for sell-product page.
     */
    public static final String PAGE_SELL_PRODUCT = "investment.sell";

    /**
     * @brief 持仓查询页面标识（Show Holdings Page ID）；
     *        Page identifier for holdings-query page.
     */
    public static final String PAGE_SHOW_HOLDING = "investment.holdings";

    /**
     * @brief 通用提交事件（Submit Event Type）；
     *        Generic submit event type emitted by form-like views.
     */
    public static final String EVENT_SUBMIT = "submit";

    /**
     * @brief 查询事件（Query Event Type）；
     *        Query event type emitted by holdings view.
     */
    public static final String EVENT_QUERY = "query";

    /**
     * @brief 投资账户 ID 字段（Investment Account ID Field）；
     *        Payload key for investment account id.
     */
    public static final String FIELD_INVESTMENT_ACCOUNT_ID = "investment_account_id";

    /**
     * @brief 产品代码字段（Product Code Field）；
     *        Payload key for product code.
     */
    public static final String FIELD_PRODUCT_CODE = "product_code";

    /**
     * @brief 产品名称字段（Product Name Field）；
     *        Payload key for product name.
     */
    public static final String FIELD_PRODUCT_NAME = "product_name";

    /**
     * @brief 产品类型字段（Product Type Field）；
     *        Payload key for product type.
     */
    public static final String FIELD_PRODUCT_TYPE = "product_type";

    /**
     * @brief 币种字段（Currency Code Field）；
     *        Payload key for currency code.
     */
    public static final String FIELD_CURRENCY_CODE = "currency_code";

    /**
     * @brief 风险等级字段（Risk Level Field）；
     *        Payload key for risk level.
     */
    public static final String FIELD_RISK_LEVEL = "risk_level";

    /**
     * @brief 发行方字段（Issuer Field）；
     *        Payload key for issuer.
     */
    public static final String FIELD_ISSUER = "issuer";

    /**
     * @brief 下单份额字段（Quantity Field）；
     *        Payload key for order quantity.
     */
    public static final String FIELD_QUANTITY = "quantity";

    /**
     * @brief 下单价格字段（Price Field）；
     *        Payload key for order price.
     */
    public static final String FIELD_PRICE = "price";

    /**
     * @brief 手续费字段（Fee Amount Field）；
     *        Payload key for fee amount.
     */
    public static final String FIELD_FEE_AMOUNT = "fee_amount";

    /**
     * @brief 发起客户字段（Initiator Customer Field）；
     *        Payload key for initiator customer id.
     */
    public static final String FIELD_INITIATOR_CUSTOMER_ID = "initiator_customer_id";

    /**
     * @brief 渠道字段（Business Channel Field）；
     *        Payload key for business channel.
     */
    public static final String FIELD_CHANNEL = "channel";

    /**
     * @brief 参考号字段（Reference Number Field）；
     *        Payload key for business reference number.
     */
    public static final String FIELD_REFERENCE_NO = "reference_no";

    /**
     * @brief 风险承受等级字段（Risk Tolerance Field）；
     *        Payload key for customer risk tolerance.
     */
    public static final String FIELD_CUSTOMER_RISK_TOLERANCE = "customer_risk_tolerance";

    /**
     * @brief 交易时间字段（Trade Timestamp Field）；
     *        Payload key for trade timestamp.
     */
    public static final String FIELD_TRADE_AT = "trade_at";

    /**
     * @brief 包含产品详情字段（Include Product Details Field）；
     *        Payload key for include-product-details flag.
     */
    public static final String FIELD_INCLUDE_PRODUCT_DETAILS = "include_product_details";

    /**
     * @brief 禁止实例化（No Instance）；
     *        Prevent instantiation.
     */
    private InvestmentGuiSchema() {
    }
}
