package com.moesegfault.banking.presentation.gui.business;

/**
 * @brief 业务流水 GUI schema 字段常量（Business GUI Schema Field Constants），统一复用 canonical 字段名；
 *        Canonical schema field-name constants reused by business GUI models, views, and controllers.
 */
public final class BusinessGuiSchema {

    /**
     * @brief 交易 ID 字段（Transaction ID Field）；
     *        Canonical `transaction_id` field name.
     */
    public static final String TRANSACTION_ID = "transaction_id";

    /**
     * @brief 参考号字段（Reference Number Field）；
     *        Canonical `reference_no` field name.
     */
    public static final String REFERENCE_NO = "reference_no";

    /**
     * @brief 发起客户 ID 字段（Initiator Customer ID Field）；
     *        Canonical `initiator_customer_id` field name.
     */
    public static final String INITIATOR_CUSTOMER_ID = "initiator_customer_id";

    /**
     * @brief 交易状态字段（Transaction Status Field）；
     *        Canonical `transaction_status` field name.
     */
    public static final String TRANSACTION_STATUS = "transaction_status";

    /**
     * @brief 返回上限字段（Result Limit Field）；
     *        Canonical `limit` field name.
     */
    public static final String LIMIT = "limit";

    /**
     * @brief 行索引属性键（Row Index Attribute Key）；
     *        Event attribute key storing selected row index.
     */
    public static final String ROW_INDEX = "row_index";

    /**
     * @brief 总数字段（Total Field）；
     *        Canonical `total` field name.
     */
    public static final String TOTAL = "total";

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private BusinessGuiSchema() {
    }
}
