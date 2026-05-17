package com.moesegfault.banking.presentation.gui.view;

/**
 * @brief 原生组件页面视图（Native Component Page View），暴露可由具体 GUI 运行时挂载的 toolkit 组件；
 *        Native component page view exposing a toolkit component that a concrete GUI runtime can mount.
 */
public interface NativeComponentView {

    /**
     * @brief 获取页面原生组件（Get Page Native Component）；
     *        Get the toolkit-native component representing the whole page.
     *
     * @return 页面原生组件（Page native component）。
     */
    Object component();
}
