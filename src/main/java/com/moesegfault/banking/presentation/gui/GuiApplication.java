package com.moesegfault.banking.presentation.gui;

import java.util.Objects;

/**
 * @brief GUI 应用对象（GUI Application），管理运行时生命周期和首页导航；
 *        GUI application object that manages runtime lifecycle and initial page navigation.
 */
public final class GuiApplication {

    /**
     * @brief 会话上下文（Session Context）；
     *        Session context.
     */
    private final GuiContext context;

    /**
     * @brief 页面导航器（Page Navigator）；
     *        Page navigator.
     */
    private final GuiNavigator navigator;

    /**
     * @brief GUI 运行时（GUI Runtime）；
     *        GUI runtime.
     */
    private final GuiRuntime runtime;

    /**
     * @brief 首页面标识（Home Page Identifier）；
     *        Home page identifier.
     */
    private final GuiPageId homePageId;

    /**
     * @brief 已启动标记（Started Flag）；
     *        Started flag.
     */
    private boolean started;

    /**
     * @brief 构造 GUI 应用对象（Construct GUI Application）；
     *        Construct GUI application object.
     *
     * @param context 会话上下文（Session context）。
     * @param navigator 页面导航器（Page navigator）。
     * @param runtime GUI 运行时（GUI runtime）。
     * @param homePageId 首页面标识（Home page identifier）。
     */
    public GuiApplication(final GuiContext context,
                          final GuiNavigator navigator,
                          final GuiRuntime runtime,
                          final GuiPageId homePageId) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.navigator = Objects.requireNonNull(navigator, "navigator must not be null");
        this.runtime = Objects.requireNonNull(runtime, "runtime must not be null");
        this.homePageId = Objects.requireNonNull(homePageId, "homePageId must not be null");
    }

    /**
     * @brief 启动 GUI 应用（Start GUI Application）；
     *        Start GUI application.
     */
    public void start() {
        if (started) {
            return;
        }
        runtime.start();
        navigator.navigateTo(homePageId);
        started = true;
    }

    /**
     * @brief 停止 GUI 应用（Stop GUI Application）；
     *        Stop GUI application.
     */
    public void stop() {
        if (!started) {
            return;
        }
        runtime.stop();
        started = false;
    }

    /**
     * @brief 获取会话上下文（Get Session Context）；
     *        Get session context.
     *
     * @return 会话上下文（Session context）。
     */
    public GuiContext context() {
        return context;
    }

    /**
     * @brief 获取页面导航器（Get Page Navigator）；
     *        Get page navigator.
     *
     * @return 页面导航器（Page navigator）。
     */
    public GuiNavigator navigator() {
        return navigator;
    }
}
