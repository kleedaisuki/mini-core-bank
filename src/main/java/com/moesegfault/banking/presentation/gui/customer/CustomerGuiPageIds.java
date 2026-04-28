package com.moesegfault.banking.presentation.gui.customer;

import com.moesegfault.banking.presentation.gui.GuiPageId;

/**
 * @brief 客户 GUI 页面标识常量（Customer GUI Page Identifier Constants），统一 customer 域页面 ID；
 *        Customer GUI page identifier constants centralizing page IDs in customer domain.
 */
public final class CustomerGuiPageIds {

    /**
     * @brief 客户注册页面 ID（Register Customer Page ID）；
     *        Page identifier for customer registration page.
     */
    public static final GuiPageId REGISTER_CUSTOMER = GuiPageId.of("customer.register");

    /**
     * @brief 客户详情页面 ID（Show Customer Page ID）；
     *        Page identifier for customer-detail page.
     */
    public static final GuiPageId SHOW_CUSTOMER = GuiPageId.of("customer.show");

    /**
     * @brief 客户列表页面 ID（List Customers Page ID）；
     *        Page identifier for customer-list page.
     */
    public static final GuiPageId LIST_CUSTOMERS = GuiPageId.of("customer.list");

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private CustomerGuiPageIds() {
    }
}
