package com.moesegfault.banking.presentation.gui.business;

/**
 * @brief 业务流水 GUI 事件类型常量（Business GUI Event Type Constants），统一 controller/view 事件名；
 *        Business GUI event type constants shared by business views and controllers.
 */
public final class BusinessGuiEventTypes {

    /**
     * @brief 详情查询事件（Show Business Transaction Query Event）；
     *        Event type for business-transaction detail query.
     */
    public static final String SHOW_BUSINESS_TRANSACTION_QUERY = "business.show.query";

    /**
     * @brief 列表查询事件（List Business Transactions Query Event）；
     *        Event type for business-transaction list query.
     */
    public static final String LIST_BUSINESS_TRANSACTIONS_QUERY = "business.list.query";

    /**
     * @brief 列表行选择事件（List Business Transactions Row-selected Event）；
     *        Event type for selecting one row in transaction list.
     */
    public static final String LIST_BUSINESS_TRANSACTIONS_ROW_SELECTED = "business.list.row_selected";

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private BusinessGuiEventTypes() {
    }
}
