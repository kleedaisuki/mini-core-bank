package com.moesegfault.banking.presentation.gui.mvc;

/**
 * @brief GUI 模型接口（GUI Model Interface），定义可观察页面状态模型契约；
 *        GUI model contract for observable page state.
 */
public interface GuiModel {

    /**
     * @brief 注册模型变更监听器（Register Model Change Listener）；
     *        Register one model change listener.
     *
     * @param listener 监听器（Listener）。
     */
    void addChangeListener(ModelChangeListener listener);

    /**
     * @brief 移除模型变更监听器（Remove Model Change Listener）；
     *        Remove one model change listener.
     *
     * @param listener 监听器（Listener）。
     */
    void removeChangeListener(ModelChangeListener listener);
}
