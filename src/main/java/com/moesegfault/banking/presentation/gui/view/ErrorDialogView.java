package com.moesegfault.banking.presentation.gui.view;

/**
 * @brief 错误弹窗抽象（Error Dialog View），展示可读错误信息；
 *        Error dialog abstraction for user-readable failure messages.
 */
public interface ErrorDialogView {

    /**
     * @brief 显示错误弹窗（Show Error Dialog）；
     *        Display one error dialog.
     *
     * @param title 标题（Dialog title）。
     * @param message 消息内容（Dialog message）。
     */
    void showError(String title, String message);
}
