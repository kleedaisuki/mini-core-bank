package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.presentation.gui.GuiPageId;

/**
 * @brief Account GUI 页面标识常量（Account GUI Page IDs），统一账户域页面命名；
 *        Account GUI page-id constants centralizing account-domain page naming.
 */
public final class AccountGuiPageIds {

    /**
     * @brief 开立储蓄账户页面 ID（Open Savings Account Page ID）;
     *        Page identifier for open-savings-account page.
     */
    public static final GuiPageId OPEN_SAVINGS_ACCOUNT = GuiPageId.of("account.open-savings");

    /**
     * @brief 开立外汇账户页面 ID（Open FX Account Page ID）;
     *        Page identifier for open-FX-account page.
     */
    public static final GuiPageId OPEN_FX_ACCOUNT = GuiPageId.of("account.open-fx");

    /**
     * @brief 开立投资账户页面 ID（Open Investment Account Page ID）;
     *        Page identifier for open-investment-account page.
     */
    public static final GuiPageId OPEN_INVESTMENT_ACCOUNT = GuiPageId.of("account.open-investment");

    /**
     * @brief 账户详情页面 ID（Show Account Page ID）;
     *        Page identifier for show-account page.
     */
    public static final GuiPageId SHOW_ACCOUNT = GuiPageId.of("account.show");

    /**
     * @brief 账户列表页面 ID（List Accounts Page ID）;
     *        Page identifier for list-accounts page.
     */
    public static final GuiPageId LIST_ACCOUNTS = GuiPageId.of("account.list");

    /**
     * @brief 私有构造（Private Constructor）;
     *        Private constructor for utility class.
     */
    private AccountGuiPageIds() {
    }
}
