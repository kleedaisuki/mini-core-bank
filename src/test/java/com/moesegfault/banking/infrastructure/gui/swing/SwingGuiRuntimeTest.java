package com.moesegfault.banking.infrastructure.gui.swing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.moesegfault.banking.presentation.gui.UiThreadScheduler;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.view.MainMenuView;
import com.moesegfault.banking.presentation.gui.view.MainWindowView;
import com.moesegfault.banking.presentation.gui.view.NativeComponentView;
import com.moesegfault.banking.presentation.gui.view.StatusBarView;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JPanel;
import org.junit.jupiter.api.Test;

/**
 * @brief Swing GUI 运行时测试（Swing GUI Runtime Test），验证页面挂载到主窗口的行为；
 *        Tests for Swing GUI runtime page mounting into the main window.
 */
class SwingGuiRuntimeTest {

    /**
     * @brief 验证原生组件页面会被设置为主窗口内容；
     *        Verify a native-component page is installed as main-window content.
     */
    @Test
    void shouldInstallNativeComponentViewIntoMainWindow() {
        final RecordingMainWindowView mainWindowView = new RecordingMainWindowView();
        final SwingGuiRuntime runtime = new SwingGuiRuntime(mainWindowView, new DirectUiThreadScheduler());
        final NativeComponentTestView view = new NativeComponentTestView();

        runtime.mount(view);

        assertEquals(1, mainWindowView.showCount());
        assertEquals(1, view.mountCount());
        assertEquals(1, view.renderCount());
        assertSame(view.component(), mainWindowView.content());
    }

    /**
     * @brief 直接执行调度器（Direct Scheduler）；
     *        Direct scheduler that runs tasks immediately in tests.
     */
    private static final class DirectUiThreadScheduler implements UiThreadScheduler {

        /** {@inheritDoc} */
        @Override
        public void execute(final Runnable task) {
            task.run();
        }

        /** {@inheritDoc} */
        @Override
        public void executeAndWait(final Runnable task) {
            task.run();
        }

        /** {@inheritDoc} */
        @Override
        public boolean isUiThread() {
            return true;
        }
    }

    /**
     * @brief 记录型主窗口视图（Recording Main Window View）；
     *        Recording main-window view for mount assertions.
     */
    private static final class RecordingMainWindowView implements MainWindowView {

        /**
         * @brief 当前内容组件（Current Content Component）；
         *        Current content component.
         */
        private Object content;

        /**
         * @brief 展示次数（Show Count）；
         *        Show invocation count.
         */
        private final AtomicInteger showCounter = new AtomicInteger();

        /** {@inheritDoc} */
        @Override
        public void setTitle(final String title) {
        }

        /** {@inheritDoc} */
        @Override
        public void setMainMenu(final MainMenuView mainMenuView) {
        }

        /** {@inheritDoc} */
        @Override
        public void setStatusBar(final StatusBarView statusBarView) {
        }

        /** {@inheritDoc} */
        @Override
        public void setContent(final Object content) {
            this.content = content;
        }

        /** {@inheritDoc} */
        @Override
        public void show() {
            showCounter.incrementAndGet();
        }

        /** {@inheritDoc} */
        @Override
        public void close() {
        }

        /**
         * @brief 获取当前内容（Get Current Content）；
         *        Get current content component.
         *
         * @return 当前内容组件（Current content component）。
         */
        Object content() {
            return content;
        }

        /**
         * @brief 获取展示次数（Get Show Count）；
         *        Get show invocation count.
         *
         * @return 展示次数（Show count）。
         */
        int showCount() {
            return showCounter.get();
        }
    }

    /**
     * @brief 测试模型（Test Model）；
     *        Test model.
     */
    private static final class TestModel extends AbstractGuiModel {
    }

    /**
     * @brief 原生组件测试视图（Native Component Test View）；
     *        Native-component test view.
     */
    private static final class NativeComponentTestView implements GuiView<TestModel>, NativeComponentView {

        /**
         * @brief 页面组件（Page Component）；
         *        Page component exposed to the runtime.
         */
        private final JPanel component = new JPanel();

        /**
         * @brief 挂载次数（Mount Count）；
         *        Mount invocation count.
         */
        private final AtomicInteger mountCounter = new AtomicInteger();

        /**
         * @brief 渲染次数（Render Count）；
         *        Render invocation count.
         */
        private final AtomicInteger renderCounter = new AtomicInteger();

        /** {@inheritDoc} */
        @Override
        public void bindModel(final TestModel model) {
        }

        /** {@inheritDoc} */
        @Override
        public void mount() {
            mountCounter.incrementAndGet();
        }

        /** {@inheritDoc} */
        @Override
        public void unmount() {
        }

        /** {@inheritDoc} */
        @Override
        public void render() {
            renderCounter.incrementAndGet();
        }

        /** {@inheritDoc} */
        @Override
        public Object component() {
            return component;
        }

        /**
         * @brief 获取挂载次数（Get Mount Count）；
         *        Get mount invocation count.
         *
         * @return 挂载次数（Mount count）。
         */
        int mountCount() {
            return mountCounter.get();
        }

        /**
         * @brief 获取渲染次数（Get Render Count）；
         *        Get render invocation count.
         *
         * @return 渲染次数（Render count）。
         */
        int renderCount() {
            return renderCounter.get();
        }
    }
}
