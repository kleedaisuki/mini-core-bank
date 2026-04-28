package com.moesegfault.banking.infrastructure.gui.swing.view;

import javax.swing.JComponent;

/**
 * @brief Swing 组件视图接口（Swing Component View Interface），供 runtime 获取页面原生组件；
 *        Swing component view interface exposing native JComponent for runtime mounting.
 */
public interface SwingComponentView {

    /**
     * @brief 获取 Swing 原生组件（Get Native Swing Component）；
     *        Get native Swing component to be mounted by runtime.
     *
     * @return Swing 组件（Swing component）。
     */
    JComponent component();
}
