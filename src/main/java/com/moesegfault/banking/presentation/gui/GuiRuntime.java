package com.moesegfault.banking.presentation.gui;

import com.moesegfault.banking.presentation.gui.mvc.GuiModel;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.view.MainWindowView;

/**
 * @brief GUI 运行时抽象（GUI Runtime Abstraction），管理主窗口生命周期与页面挂载；
 *        GUI runtime abstraction for main-window lifecycle and page mounting.
 */
public interface GuiRuntime {

    /**
     * @brief 启动运行时（Start Runtime）；
     *        Start GUI runtime and prepare the main window.
     */
    void start();

    /**
     * @brief 挂载页面视图（Mount Page View）；
     *        Mount one page-level view into the runtime container.
     *
     * @param view 页面视图（Page view）。
     */
    void mount(GuiView<? extends GuiModel> view);

    /**
     * @brief 获取主窗口视图（Get Main Window View）；
     *        Get runtime-owned main window adapter.
     *
     * @return 主窗口视图（Main window view）。
     */
    MainWindowView mainWindow();

    /**
     * @brief 停止运行时（Stop Runtime）；
     *        Stop GUI runtime and release window resources.
     */
    void stop();
}
