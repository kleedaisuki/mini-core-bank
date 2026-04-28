package com.moesegfault.banking.presentation.gui.business;

import com.moesegfault.banking.presentation.gui.GuiPageId;

/**
 * @brief 业务流水 GUI 页面标识常量（Business GUI Page Identifier Constants），统一管理 business 域页面 ID；
 *        Business GUI page identifier constants centralizing page IDs in business domain.
 */
public final class BusinessGuiPageIds {

    /**
     * @brief 业务流水详情页面 ID（Show Business Transaction Page ID）；
     *        Page identifier for business-transaction detail page.
     */
    public static final GuiPageId SHOW_BUSINESS_TRANSACTION = GuiPageId.of("business.show");

    /**
     * @brief 业务流水列表页面 ID（List Business Transactions Page ID）；
     *        Page identifier for business-transaction list page.
     */
    public static final GuiPageId LIST_BUSINESS_TRANSACTIONS = GuiPageId.of("business.list");

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private BusinessGuiPageIds() {
    }
}
