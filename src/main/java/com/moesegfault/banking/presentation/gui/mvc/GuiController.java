package com.moesegfault.banking.presentation.gui.mvc;

/**
 * @brief GUI 控制器接口（GUI Controller Interface），处理初始化与视图事件；
 *        GUI controller contract for initialization and incoming view events.
 */
public interface GuiController {

    /**
     * @brief 初始化控制器（Initialize Controller）；
     *        Initialize controller before accepting view events.
     */
    void init();

    /**
     * @brief 处理视图事件（Handle View Event）；
     *        Handle one event emitted by the view.
     *
     * @param event 视图事件（View event）。
     */
    void onViewEvent(ViewEvent event);
}
