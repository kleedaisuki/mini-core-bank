package com.moesegfault.banking.presentation.gui.card;

import com.moesegfault.banking.presentation.gui.GuiPageId;

/**
 * @brief 卡模块页面标识常量（Card Module Page Identifier Constants），统一管理 card GUI 页面 ID；
 *        Card-module page identifier constants for centralized card GUI page IDs.
 */
final class CardGuiPageIds {

    /**
     * @brief 主借记卡发卡页面 ID（Issue Debit Card Page ID）；
     *        Page id for primary debit-card issuance.
     */
    static final GuiPageId ISSUE_DEBIT_CARD = GuiPageId.of("card.issue-debit");

    /**
     * @brief 借记附属卡发卡页面 ID（Issue Supplementary Debit Card Page ID）；
     *        Page id for supplementary debit-card issuance.
     */
    static final GuiPageId ISSUE_SUPPLEMENTARY_DEBIT_CARD = GuiPageId.of("card.issue-supplementary-debit");

    /**
     * @brief 主信用卡发卡页面 ID（Issue Credit Card Page ID）；
     *        Page id for primary credit-card issuance.
     */
    static final GuiPageId ISSUE_CREDIT_CARD = GuiPageId.of("card.issue-credit");

    /**
     * @brief 信用附属卡发卡页面 ID（Issue Supplementary Credit Card Page ID）；
     *        Page id for supplementary credit-card issuance.
     */
    static final GuiPageId ISSUE_SUPPLEMENTARY_CREDIT_CARD = GuiPageId.of("card.issue-supplementary-credit");

    /**
     * @brief 卡详情页面 ID（Show Card Page ID）；
     *        Page id for card-detail query.
     */
    static final GuiPageId SHOW_CARD = GuiPageId.of("card.show");

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for constants holder.
     */
    private CardGuiPageIds() {
    }
}
