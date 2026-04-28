package com.moesegfault.banking.presentation.gui.credit;

import com.moesegfault.banking.presentation.gui.GuiPageId;

/**
 * @brief Credit GUI 页面标识常量（Credit GUI Page Identifier Constants），统一维护 credit 页面 ID；
 *        Credit GUI page identifier constants for credit-page registration.
 */
public final class CreditGuiPageIds {

    /**
     * @brief 生成账单页面 ID（Generate Statement Page ID）；
     *        Page id for statement generation page.
     */
    public static final GuiPageId GENERATE_STATEMENT = GuiPageId.of("credit.generate-statement");

    /**
     * @brief 信用卡还款页面 ID（Repay Credit Card Page ID）；
     *        Page id for credit-card repayment page.
     */
    public static final GuiPageId REPAY_CREDIT_CARD = GuiPageId.of("credit.repay");

    /**
     * @brief 账单查询页面 ID（Show Statement Page ID）；
     *        Page id for statement query page.
     */
    public static final GuiPageId SHOW_STATEMENT = GuiPageId.of("credit.statement");

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private CreditGuiPageIds() {
    }
}
