package com.moesegfault.banking.presentation.gui.mvc;

/**
 * @brief 模型变更监听器（Model Change Listener），用于接收模型状态更新通知；
 *        Listener that observes model state update events.
 */
@FunctionalInterface
public interface ModelChangeListener {

    /**
     * @brief 处理模型变更事件（Handle Model Change Event）；
     *        Handle one model change event.
     *
     * @param event 模型变更事件（Model change event）。
     */
    void onModelChanged(ModelChangeEvent event);
}
