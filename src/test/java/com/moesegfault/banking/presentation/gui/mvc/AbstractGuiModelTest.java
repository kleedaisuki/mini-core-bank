package com.moesegfault.banking.presentation.gui.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

/**
 * @brief GUI 模型抽象基类测试（Abstract GUI Model Test），验证监听器与事件行为；
 *        Tests for abstract GUI model listener registration and change event behavior.
 */
class AbstractGuiModelTest {

    /**
     * @brief 验证 `fireChanged()` 会广播空字段事件；
     *        Verify `fireChanged()` publishes an event with empty changed fields.
     */
    @Test
    void shouldPublishEmptyFieldSetWhenFireChangedWithoutArgs() {
        final TestModel model = new TestModel();
        final AtomicReference<ModelChangeEvent> captured = new AtomicReference<>();
        model.addChangeListener(captured::set);

        model.publishWithoutFields();

        assertEquals(model, captured.get().source());
        assertEquals(Set.of(), captured.get().changedFields());
    }

    /**
     * @brief 验证 `fireChanged(varargs)` 会广播指定字段；
     *        Verify `fireChanged(varargs)` publishes specified field names.
     */
    @Test
    void shouldPublishProvidedFieldNames() {
        final TestModel model = new TestModel();
        final AtomicReference<ModelChangeEvent> captured = new AtomicReference<>();
        model.addChangeListener(captured::set);

        model.publishFields("name", "status");

        assertEquals(Set.of("name", "status"), captured.get().changedFields());
    }

    /**
     * @brief 验证移除监听器后不再接收事件；
     *        Verify removed listener does not receive following events.
     */
    @Test
    void shouldNotNotifyRemovedListener() {
        final TestModel model = new TestModel();
        final AtomicReference<ModelChangeEvent> captured = new AtomicReference<>();
        final ModelChangeListener listener = captured::set;
        model.addChangeListener(listener);
        model.removeChangeListener(listener);

        model.publishWithoutFields();

        assertNull(captured.get());
    }

    /**
     * @brief 验证不允许注册空监听器；
     *        Verify adding null listener is rejected.
     */
    @Test
    void shouldRejectNullListenerOnAdd() {
        final TestModel model = new TestModel();
        assertThrows(NullPointerException.class, () -> model.addChangeListener(null));
    }

    /**
     * @brief 测试模型（Test Model），暴露受保护 fireChanged 方法；
     *        Test model exposing protected fireChanged methods.
     */
    private static final class TestModel extends AbstractGuiModel {

        /**
         * @brief 发布无字段变更（Publish Change Without Fields）；
         *        Publish model change with no field names.
         */
        void publishWithoutFields() {
            fireChanged();
        }

        /**
         * @brief 发布字段变更（Publish Field Changes）；
         *        Publish model change with specified field names.
         *
         * @param fields 字段名数组（Field names）。
         */
        void publishFields(final String... fields) {
            fireChanged(fields);
        }
    }
}
