package com.moesegfault.banking.presentation.gui.card;

import com.moesegfault.banking.presentation.gui.mvc.GuiModel;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import java.util.Objects;

/**
 * @brief 被动 GUI 视图基类（Passive GUI View Base），提供通用 bind/mount/unmount 生命周期实现；
 *        Passive GUI view base class with common bind, mount, and unmount lifecycle handling.
 *
 * @param <M> 模型类型（Model type）。
 */
abstract class AbstractPassiveGuiView<M extends GuiModel> implements GuiView<M> {

    /**
     * @brief 绑定模型（Bound Model）；
     *        Model currently bound to the view.
     */
    private M model;

    /**
     * @brief 挂载状态（Mounted Flag）；
     *        Whether this view is mounted.
     */
    private boolean mounted;

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindModel(final M model) {
        this.model = Objects.requireNonNull(model, "model must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mount() {
        mounted = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unmount() {
        mounted = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
    }

    /**
     * @brief 获取绑定模型（Get Bound Model）；
     *        Get currently bound model instance.
     *
     * @return 模型对象（Model instance）。
     */
    protected final M model() {
        return Objects.requireNonNull(model, "model must be bound before usage");
    }

    /**
     * @brief 判断是否已挂载（Check Mounted State）；
     *        Check whether this view is mounted.
     *
     * @return 是否已挂载（Mounted flag）。
     */
    protected final boolean mounted() {
        return mounted;
    }
}
