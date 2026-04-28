package com.moesegfault.banking.presentation.gui.view;

/**
 * @brief 成功弹窗抽象（Success Dialog View），展示操作完成提示；
 *        Success dialog abstraction for positive completion feedback.
 */
public interface SuccessDialogView {

    /**
     * @brief 显示成功弹窗（Show Success Dialog）；
     *        Display one success dialog.
     *
     * @param title 标题（Dialog title）。
     * @param message 消息内容（Dialog message）。
     */
    void showSuccess(String title, String message);
}
