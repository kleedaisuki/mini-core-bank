package com.moesegfault.banking.presentation.gui.mvc;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @brief GUI 模型抽象基类（Abstract GUI Model），封装监听器管理和变更广播；
 *        Base GUI model implementation with listener management and change publication.
 */
public abstract class AbstractGuiModel implements GuiModel {

    /**
     * @brief 监听器列表（Listener List）；
     *        Thread-safe list storing model change listeners.
     */
    private final List<ModelChangeListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChangeListener(final ModelChangeListener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener must not be null"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeChangeListener(final ModelChangeListener listener) {
        listeners.remove(Objects.requireNonNull(listener, "listener must not be null"));
    }

    /**
     * @brief 广播模型整体变更（Publish Whole-model Change）；
     *        Publish change event without specific field names.
     */
    protected final void fireChanged() {
        fireChanged(Set.of());
    }

    /**
     * @brief 广播指定字段变更（Publish Field-level Change）；
     *        Publish change event for specific changed fields.
     *
     * @param changedFields 变更字段集合（Changed field names）。
     */
    protected final void fireChanged(final Set<String> changedFields) {
        final ModelChangeEvent event = new ModelChangeEvent(this, changedFields);
        for (ModelChangeListener listener : listeners) {
            listener.onModelChanged(event);
        }
    }

    /**
     * @brief 广播可变参数字段变更（Publish Varargs Field Change）；
     *        Publish field-level change from varargs field names.
     *
     * @param changedFields 变更字段数组（Changed field names）。
     */
    protected final void fireChanged(final String... changedFields) {
        Objects.requireNonNull(changedFields, "changedFields must not be null");
        fireChanged(Set.of(changedFields));
    }
}
