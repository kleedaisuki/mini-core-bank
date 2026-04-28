package com.moesegfault.banking.presentation.gui.view;

import java.util.List;
import java.util.function.Consumer;

/**
 * @brief 主菜单视图抽象（Main Menu View），提供菜单项渲染与选择事件；
 *        Main menu abstraction exposing menu items and selection callback.
 */
public interface MainMenuView {

    /**
     * @brief 设置菜单项（Set Menu Items）；
     *        Replace current menu items by provided ids.
     *
     * @param itemIds 菜单项标识列表（Menu item ids）。
     */
    void setItems(List<String> itemIds);

    /**
     * @brief 绑定菜单选择监听器（Bind Selection Listener）；
     *        Register callback invoked when one menu item is selected.
     *
     * @param listener 选择监听器（Selection listener）。
     */
    void onItemSelected(Consumer<String> listener);

    /**
     * @brief 获取 toolkit 原生组件（Get Toolkit-native Component）；
     *        Return underlying toolkit component object.
     *
     * @return toolkit 原生组件（Toolkit-native component）。
     */
    Object component();
}
