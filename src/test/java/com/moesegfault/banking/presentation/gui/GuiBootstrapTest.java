package com.moesegfault.banking.presentation.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
 * @brief GUI 装配器测试（GUI Bootstrap Test），验证技术栈选择与应用启动链路；
 *        Tests for GUI bootstrap toolkit selection and application launch chain.
 */
class GuiBootstrapTest {

    /**
     * @brief 验证装配器会按技术栈选择运行时；
     *        Verify bootstrap selects runtime by toolkit type.
     */
    @Test
    void shouldSelectRuntimeByToolkitType() {
        final RecordingRuntime swingRuntime = new RecordingRuntime();
        final RecordingRuntime javaFxRuntime = new RecordingRuntime();
        final GuiBootstrap bootstrap = new GuiBootstrap(
                toolkitType -> switch (toolkitType) {
                    case SWING -> swingRuntime;
                    case JAVAFX -> javaFxRuntime;
                },
                GuiContext::new,
                GuiPageId.of("home"),
                registrar -> registrar.register("home", ctx -> createSimplePage("home")));

        final GuiApplication application = bootstrap.bootstrap(GuiToolkitType.JAVAFX);
        application.start();

        assertEquals(1, javaFxRuntime.startCount());
        assertEquals(0, swingRuntime.startCount());
    }

    /**
     * @brief 验证 BankingGui.launch 会启动应用；
     *        Verify BankingGui.launch starts the application.
     */
    @Test
    void shouldLaunchApplicationViaBankingGui() {
        final RecordingRuntime runtime = new RecordingRuntime();
        final GuiBootstrap bootstrap = new GuiBootstrap(
                toolkitType -> runtime,
                GuiContext::new,
                GuiPageId.of("home"),
                registrar -> registrar.register("home", ctx -> createSimplePage("home")));
        final BankingGui bankingGui = new BankingGui(bootstrap);

        final GuiApplication application = bankingGui.launch(GuiToolkitType.SWING);

        assertNotNull(application);
        assertEquals(1, runtime.startCount());
    }

    /**
     * @brief 验证缺失运行时会被拒绝；
     *        Verify missing runtime is rejected.
     */
    @Test
    void shouldRejectNullRuntimeFromResolver() {
        final GuiBootstrap bootstrap = new GuiBootstrap(
                toolkitType -> null,
                GuiContext::new,
                GuiPageId.of("home"));

        assertThrows(NullPointerException.class, () -> bootstrap.bootstrap(GuiToolkitType.SWING));
    }

    /**
     * @brief 验证支持通过模块批量注册页面；
     *        Verify bootstrap supports bulk page registration through modules.
     */
    @Test
    void shouldRegisterPagesThroughGuiModules() {
        final RecordingRuntime runtime = new RecordingRuntime();
        final GuiModule coreModule = registrar -> registrar.register("home", ctx -> createSimplePage("home"));
        final GuiBootstrap bootstrap = new GuiBootstrap(
                toolkitType -> runtime,
                GuiContext::new,
                GuiPageId.of("home"),
                coreModule);

        final GuiApplication application = bootstrap.bootstrap(GuiToolkitType.SWING);
        application.start();

        assertEquals(1, runtime.startCount());
        assertEquals(GuiPageId.of("home"), application.context().currentPageId().orElseThrow());
    }

    /**
     * @brief 创建简易测试页面（Create Simple Test Page）；
     *        Create a simple test page.
     *
     * @param pageName 页面名（Page name）。
     * @return 页面对象（Page object）。
     */
    private static GuiPage createSimplePage(final String pageName) {
        return new GuiPage(
                GuiPageId.of(pageName),
                new TestModel(),
                new NoopView(),
                new NoopController());
    }

    /**
     * @brief 记录型运行时（Recording Runtime）；
     *        Recording runtime for start-count verification.
     */
    private static final class RecordingRuntime implements GuiRuntime {

        /**
         * @brief 启动计数（Start Count）；
         *        Start invocation count.
         */
        private final AtomicInteger startCounter = new AtomicInteger();

        /**
         * {@inheritDoc}
         */
        @Override
        public void start() {
            startCounter.incrementAndGet();
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
            view.mount();
            view.render();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MainWindowView mainWindow() {
            return new NoopMainWindowView();
        }

        /**
         * @brief 获取启动次数（Get Start Count）；
         *        Get start invocation count.
         *
         * @return 启动次数（Start invocation count）。
         */
        int startCount() {
            return startCounter.get();
        }
    }

    /**
     * @brief 空实现模型（No-op Model）；
     *        No-op model.
     */
    private static final class TestModel extends AbstractGuiModel {
    }

    /**
     * @brief 空实现视图（No-op View）；
     *        No-op view.
     */
    private static final class NoopView implements GuiView<GuiModel> {

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
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void unmount() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void render() {
        }
    }

    /**
     * @brief 空实现控制器（No-op Controller）；
     *        No-op controller.
     */
    private static final class NoopController implements GuiController {

        /**
         * {@inheritDoc}
         */
        @Override
        public void init() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onViewEvent(final ViewEvent event) {
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
