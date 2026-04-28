package com.moesegfault.banking.presentation.gui;

/**
 * @brief GUI 页面工厂接口（GUI Page Factory Interface），负责创建页面三件套；
 *        GUI page factory contract responsible for creating model-view-controller page bundle.
 */
@FunctionalInterface
public interface GuiPageFactory {

    /**
     * @brief 创建页面实例（Create GUI Page Instance）；
     *        Create one GUI page instance.
     *
     * @param context GUI 会话上下文（GUI session context）。
     * @return GUI 页面（GUI page）。
     */
    GuiPage createPage(GuiContext context);
}
