package com.moesegfault.banking.presentation.gui;

import java.util.Objects;

/**
 * @brief GUI 启动入口（GUI Entry Point），负责触发装配并启动 GUI 应用；
 *        GUI entry point responsible for bootstrapping and starting GUI application.
 */
public final class BankingGui {

    /**
     * @brief GUI 装配器（GUI Bootstrap）；
     *        GUI bootstrap.
     */
    private final GuiBootstrap guiBootstrap;

    /**
     * @brief 构造 GUI 启动器（Construct GUI Launcher）；
     *        Construct GUI launcher.
     *
     * @param guiBootstrap GUI 装配器（GUI bootstrap）。
     */
    public BankingGui(final GuiBootstrap guiBootstrap) {
        this.guiBootstrap = Objects.requireNonNull(guiBootstrap, "guiBootstrap must not be null");
    }

    /**
     * @brief 启动 GUI 应用（Launch GUI Application）；
     *        Launch GUI application with selected toolkit.
     *
     * @param toolkitType GUI 技术栈（GUI toolkit type）。
     * @return 已启动应用对象（Started application object）。
     */
    public GuiApplication launch(final GuiToolkitType toolkitType) {
        final GuiApplication application = guiBootstrap.bootstrap(toolkitType);
        application.start();
        return application;
    }
}
