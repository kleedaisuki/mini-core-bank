package com.moesegfault.banking.presentation.gui.view;

/**
 * @brief 空态视图抽象（Empty State View），用于无数据/无结果页面的友好提示；
 *        Empty-state abstraction for no-data or no-result scenarios.
 */
public interface EmptyStateView {

    /**
     * @brief 设置空态标题（Set Empty-state Title）；
     *        Set empty-state title text.
     *
     * @param title 标题文本（Title text）。
     */
    void setTitle(String title);

    /**
     * @brief 设置空态描述（Set Empty-state Description）；
     *        Set empty-state description text.
     *
     * @param description 描述文本（Description text）。
     */
    void setDescription(String description);

    /**
     * @brief 设置空态动作（Set Empty-state Action）；
     *        Set action button label and click handler.
     *
     * @param actionLabel 动作文案（Action label）。
     * @param action 动作回调（Action callback）。
     */
    void setAction(String actionLabel, Runnable action);

    /**
     * @brief 获取 toolkit 原生组件（Get Toolkit-native Component）；
     *        Return underlying toolkit component object.
     *
     * @return toolkit 原生组件（Toolkit-native component）。
     */
    Object component();
}
