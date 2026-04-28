package com.moesegfault.banking.presentation.gui.view;

/**
 * @brief 状态栏视图抽象（Status Bar View），显示用户、页面和提示消息；
 *        Status bar abstraction for user/page context and system message.
 */
public interface StatusBarView {

    /**
     * @brief 设置当前用户（Set Current User）；
     *        Set current user label shown in status bar.
     *
     * @param username 当前用户（Current user name）。
     */
    void setCurrentUser(String username);

    /**
     * @brief 设置当前页面（Set Current Page）；
     *        Set current page label shown in status bar.
     *
     * @param pageName 当前页面名（Current page name）。
     */
    void setCurrentPage(String pageName);

    /**
     * @brief 设置提示消息（Set Status Message）；
     *        Set status message shown in status bar.
     *
     * @param message 提示消息（Status message）。
     */
    void setMessage(String message);

    /**
     * @brief 获取 toolkit 原生组件（Get Toolkit-native Component）；
     *        Return underlying toolkit component object.
     *
     * @return toolkit 原生组件（Toolkit-native component）。
     */
    Object component();
}
