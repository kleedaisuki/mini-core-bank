package com.moesegfault.banking.presentation.gui;

import com.moesegfault.banking.presentation.gui.mvc.GuiModel;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.view.MainWindowView;
import java.util.Objects;

/**
 * @brief GUI 运行时接口（GUI Runtime Interface），封装窗口生命周期和页面展示；
 *        GUI runtime contract encapsulating window lifecycle and page presentation.
 */
public interface GuiRuntime {

    /**
     * @brief 启动运行时（Start Runtime）；
     *        Start GUI runtime.
     */
    void start();

    /**
     * @brief 停止运行时（Stop Runtime）；
     *        Stop GUI runtime.
     */
    void stop();

    /**
     * @brief 挂载页面视图（Mount Page View）；
     *        Mount one page view into runtime container.
     *
     * @param view 页面视图（Page view）。
     */
    void mount(GuiView<? extends GuiModel> view);

    /**
     * @brief 获取主窗口视图（Get Main Window View）；
     *        Get main window abstraction of current runtime.
     *
     * @return 主窗口视图（Main window view）。
     */
    MainWindowView mainWindow();

    /**
     * @brief 展示页面组合（Show Page Bundle）；
     *        Show one full page bundle.
     *
     * @param page 页面对象（Page object）。
     */
    default void showPage(final GuiPage page) {
        final GuiPage nonNullPage = Objects.requireNonNull(page, "page must not be null");
        mount(nonNullPage.view());
    }
}
