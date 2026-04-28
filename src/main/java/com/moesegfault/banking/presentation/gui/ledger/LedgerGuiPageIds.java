package com.moesegfault.banking.presentation.gui.ledger;

import com.moesegfault.banking.presentation.gui.GuiPageId;

/**
 * @brief Ledger GUI 页面标识常量（Ledger GUI Page Identifier Constants），统一 ledger 页面 ID；
 *        Ledger GUI page-id constants that centralize ledger page identifiers.
 */
public final class LedgerGuiPageIds {

    /**
     * @brief 余额查询页面 ID（Balance Query Page ID）；
     *        Page id for balance query page.
     */
    public static final GuiPageId SHOW_BALANCE = GuiPageId.of("ledger.balance");

    /**
     * @brief 分录查询页面 ID（Entries Query Page ID）；
     *        Page id for ledger-entry query page.
     */
    public static final GuiPageId SHOW_ENTRIES = GuiPageId.of("ledger.entries");

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private LedgerGuiPageIds() {
    }
}
