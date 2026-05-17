package com.moesegfault.banking.presentation.cli.gui;

import com.moesegfault.banking.presentation.gui.GuiToolkitType;

/**
 * @brief CLI GUI 启动器端口（CLI GUI Launcher Port），把 one-shot CLI 请求转为 GUI 启动动作；
 *        CLI GUI launcher port that turns one-shot CLI requests into GUI launch actions.
 */
@FunctionalInterface
public interface GuiCliLauncher {

    /**
     * @brief 启动 GUI（Launch GUI）；
     *        Launch the GUI with the selected toolkit.
     *
     * @param toolkitType GUI 技术栈（GUI toolkit type）。
     */
    void launch(GuiToolkitType toolkitType);
}
