package com.moesegfault.banking.presentation.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.GuiModel;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import com.moesegfault.banking.presentation.gui.view.MainMenuView;
import com.moesegfault.banking.presentation.gui.view.MainWindowView;
import com.moesegfault.banking.presentation.gui.view.StatusBarView;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * @brief GUI 导航器测试（GUI Navigator Test），验证页面切换与卸载行为；
 *        Tests for GUI navigator page switch and unmount behavior.
 */
class GuiNavigatorTest {

    /**
     * @brief 验证导航时会初始化并挂载页面；
     *        Verify navigating initializes and mounts the page.
     */
    @Test
    void shouldInitializeAndMountPageOnNavigation() {
        final GuiContext context = new GuiContext();
        final RecordingRuntime runtime = new RecordingRuntime();
        final GuiPageRegistry registry = new GuiPageRegistry();
        final GuiPageId pageId = GuiPageId.of("customer.register");
        final RecordingView view = new RecordingView();
        final RecordingController controller = new RecordingController();
        registry.register(pageId, ctx -> new GuiPage(pageId, new TestModel(), view, controller));

        final GuiNavigator navigator = new GuiNavigator(context, registry, runtime);
        navigator.navigateTo(pageId);

        assertEquals(1, controller.initCount());
        assertEquals(1, view.mountCount());
        assertEquals(1, runtime.showCount());
        assertEquals(pageId, context.currentPageId().orElseThrow());
    }

    /**
     * @brief 验证切换页面时会卸载旧页面；
     *        Verify previous page is unmounted when navigating to another page.
     */
    @Test
    void shouldUnmountPreviousPageWhenSwitching() {
        final GuiContext context = new GuiContext();
        final RecordingRuntime runtime = new RecordingRuntime();
        final GuiPageRegistry registry = new GuiPageRegistry();

        final RecordingView firstView = new RecordingView();
        final RecordingView secondView = new RecordingView();
        registry.register(GuiPageId.of("p1"), ctx -> new GuiPage(GuiPageId.of("p1"), new TestModel(), firstView, new RecordingController()));
        registry.register(GuiPageId.of("p2"), ctx -> new GuiPage(GuiPageId.of("p2"), new TestModel(), secondView, new RecordingController()));

        final GuiNavigator navigator = new GuiNavigator(context, registry, runtime);
        navigator.navigateTo(GuiPageId.of("p1"));
        navigator.navigateTo(GuiPageId.of("p2"));

        assertEquals(1, firstView.unmountCount());
        assertEquals(1, secondView.mountCount());
    }

    /**
     * @brief 验证未知页面会抛出异常；
     *        Verify unknown page throws exception.
     */
    @Test
    void shouldRejectUnknownPageId() {
        final GuiNavigator navigator = new GuiNavigator(new GuiContext(), new GuiPageRegistry(), new RecordingRuntime());
        assertThrows(IllegalArgumentException.class, () -> navigator.navigateTo(GuiPageId.of("unknown")));
    }

    /**
     * @brief 验证工厂返回不匹配页面标识会抛出异常；
     *        Verify factory returning mismatched page id throws exception.
     */
    @Test
    void shouldRejectMismatchedPageIdFromFactory() {
        final GuiPageRegistry registry = new GuiPageRegistry();
        registry.register(GuiPageId.of("expected"), ctx -> new GuiPage(
                GuiPageId.of("actual"),
                new TestModel(),
                new RecordingView(),
                new RecordingController()));
        final GuiNavigator navigator = new GuiNavigator(new GuiContext(), registry, new RecordingRuntime());

        assertThrows(IllegalStateException.class, () -> navigator.navigateTo(GuiPageId.of("expected")));
    }

    /**
     * @brief 记录型运行时（Recording Runtime）；
     *        Recording runtime for invocation verification.
     */
    private static final class RecordingRuntime implements GuiRuntime {

        /**
         * @brief 页面展示计数（Show Count）；
         *        Show invocation count.
         */
        private final AtomicInteger showCounter = new AtomicInteger();

        /**
         * @brief 当前挂载视图（Current Mounted View）；
         *        Current mounted view.
         */
        private GuiView<? extends GuiModel> mountedView;

        /**
         * {@inheritDoc}
         */
        @Override
        public void start() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void stop() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mount(final GuiView<? extends GuiModel> view) {
            if (mountedView != null) {
                mountedView.unmount();
            }
            mountedView = view;
            mountedView.mount();
            mountedView.render();
            showCounter.incrementAndGet();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MainWindowView mainWindow() {
            return new NoopMainWindowView();
        }

        /**
         * @brief 获取挂载次数（Get Mount Count）；
         *        Get mount invocation count.
         *
         * @return 挂载次数（Mount invocation count）。
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
     * @brief 记录型视图（Recording View）；
     *        Recording view for lifecycle verification.
     */
    private static final class RecordingView implements GuiView<GuiModel> {

        /**
         * @brief 挂载计数（Mount Count）；
         *        Mount invocation count.
         */
        private final AtomicInteger mountCounter = new AtomicInteger();

        /**
         * @brief 卸载计数（Unmount Count）；
         *        Unmount invocation count.
         */
        private final AtomicInteger unmountCounter = new AtomicInteger();

        /**
         * {@inheritDoc}
         */
        @Override
        public void bindModel(final GuiModel model) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mount() {
            mountCounter.incrementAndGet();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void unmount() {
            unmountCounter.incrementAndGet();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void render() {
        }

        /**
         * @brief 获取挂载次数（Get Mount Count）；
         *        Get mount invocation count.
         *
         * @return 挂载次数（Mount invocation count）。
         */
        int mountCount() {
            return mountCounter.get();
        }

        /**
         * @brief 获取卸载次数（Get Unmount Count）；
         *        Get unmount invocation count.
         *
         * @return 卸载次数（Unmount invocation count）。
         */
        int unmountCount() {
            return unmountCounter.get();
        }
    }

    /**
     * @brief 记录型控制器（Recording Controller）；
     *        Recording controller for invocation verification.
     */
    private static final class RecordingController implements GuiController {

        /**
         * @brief 初始化计数（Init Count）；
         *        Init invocation count.
         */
        private final AtomicInteger initCounter = new AtomicInteger();

        /**
         * {@inheritDoc}
         */
        @Override
        public void init() {
            initCounter.incrementAndGet();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onViewEvent(final ViewEvent event) {
        }

        /**
         * @brief 获取初始化次数（Get Init Count）；
         *        Get init invocation count.
         *
         * @return 初始化次数（Init invocation count）。
         */
        int initCount() {
            return initCounter.get();
        }
    }

    /**
     * @brief 空实现主窗口视图（No-op Main Window View）；
     *        No-op main window view for runtime stub.
     */
    private static final class NoopMainWindowView implements MainWindowView {

        /**
         * {@inheritDoc}
         */
        @Override
        public void setTitle(final String title) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setMainMenu(final MainMenuView mainMenuView) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setStatusBar(final StatusBarView statusBarView) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setContent(final Object content) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void show() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void close() {
        }
    }
}
