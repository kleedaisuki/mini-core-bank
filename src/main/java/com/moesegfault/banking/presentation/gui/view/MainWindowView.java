package com.moesegfault.banking.presentation.gui.view;

/**
 * @brief 主窗口视图抽象（Main Window View），封装菜单区、内容区、状态栏与窗口生命周期；
 *        Main window abstraction for menu/content/status areas and window lifecycle.
 */
public interface MainWindowView {

    /**
     * @brief 设置窗口标题（Set Window Title）；
     *        Set visible title for the main window.
     *
     * @param title 标题文本（Title text）。
     */
    void setTitle(String title);

    /**
     * @brief 设置主菜单（Set Main Menu）；
     *        Attach main menu view into window.
     *
     * @param mainMenuView 主菜单视图（Main menu view）。
     */
    void setMainMenu(MainMenuView mainMenuView);

    /**
     * @brief 设置状态栏（Set Status Bar）；
     *        Attach status bar view into window.
     *
     * @param statusBarView 状态栏视图（Status bar view）。
     */
    void setStatusBar(StatusBarView statusBarView);

    /**
     * @brief 设置内容组件（Set Content Component）；
     *        Replace center content with a toolkit-native component object.
     *
     * @param content 内容组件（Toolkit-native content component）。
     */
    void setContent(Object content);

    /**
     * @brief 显示窗口（Show Window）；
     *        Show and focus the window.
     */
    void show();

    /**
     * @brief 关闭窗口（Close Window）；
     *        Close and dispose window resources.
     */
    void close();
}
