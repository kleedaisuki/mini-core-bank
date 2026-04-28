package com.moesegfault.banking.presentation.gui.view;

/**
 * @brief 确认弹窗抽象（Confirm Dialog View），处理高风险操作的用户确认；
 *        Confirmation dialog abstraction for risky user actions.
 */
public interface ConfirmDialogView {

    /**
     * @brief 打开确认弹窗并返回结果（Open Confirmation Dialog）；
     *        Show confirmation dialog and return user decision.
     *
     * @param title 标题（Dialog title）。
     * @param message 消息内容（Dialog message）。
     * @return 用户确认返回 true（True when user confirms）。
     */
    boolean confirm(String title, String message);
}
