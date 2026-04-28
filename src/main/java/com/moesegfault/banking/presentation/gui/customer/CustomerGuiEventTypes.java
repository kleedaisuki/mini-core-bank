package com.moesegfault.banking.presentation.gui.customer;

/**
 * @brief 客户 GUI 事件类型常量（Customer GUI Event Type Constants），统一 controller/view 事件名；
 *        Customer GUI event type constants shared by views and controllers.
 */
public final class CustomerGuiEventTypes {

    /**
     * @brief 客户注册提交事件（Register Customer Submit Event）；
     *        Event type for register-customer form submission.
     */
    public static final String REGISTER_CUSTOMER_SUBMIT = "customer.register.submit";

    /**
     * @brief 客户详情查询事件（Show Customer Submit Event）；
     *        Event type for show-customer form submission.
     */
    public static final String SHOW_CUSTOMER_SUBMIT = "customer.show.submit";

    /**
     * @brief 客户列表查询事件（List Customers Query Event）；
     *        Event type for customer-list query submission.
     */
    public static final String LIST_CUSTOMERS_QUERY = "customer.list.query";

    /**
     * @brief 客户列表行选择事件（List Customers Row-selected Event）；
     *        Event type for selecting one row in customer list.
     */
    public static final String LIST_CUSTOMERS_ROW_SELECTED = "customer.list.row_selected";

    /**
     * @brief 事件属性：表单字段映射（Event Attribute: Form Values）；
     *        Event attribute key storing form value map.
     */
    public static final String ATTR_FORM_VALUES = "form_values";

    /**
     * @brief 事件属性：行索引（Event Attribute: Row Index）；
     *        Event attribute key storing selected row index.
     */
    public static final String ATTR_ROW_INDEX = "row_index";

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private CustomerGuiEventTypes() {
    }
}
