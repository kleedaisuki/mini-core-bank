package com.moesegfault.banking.presentation.gui.mvc;

/**
 * @brief GUI 视图接口（GUI View Interface），定义绑定模型与渲染生命周期；
 *        GUI view contract for model binding and render lifecycle.
 *
 * @param <M> 模型类型（Model type）。
 */
public interface GuiView<M extends GuiModel> {

    /**
     * @brief 绑定模型（Bind Model）；
     *        Bind view with a model instance.
     *
     * @param model 模型对象（Model instance）。
     */
    void bindModel(M model);

    /**
     * @brief 挂载视图（Mount View）；
     *        Mount view into UI container.
     */
    void mount();

    /**
     * @brief 卸载视图（Unmount View）；
     *        Unmount view from UI container.
     */
    void unmount();

    /**
     * @brief 渲染视图（Render View）；
     *        Render current model state.
     */
    void render();
}
