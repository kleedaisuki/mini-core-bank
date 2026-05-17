package com.moesegfault.banking.infrastructure.gui.swing;

import com.moesegfault.banking.infrastructure.gui.swing.view.SwingMainWindowView;
import com.moesegfault.banking.presentation.gui.GuiRuntime;
import com.moesegfault.banking.presentation.gui.UiThreadScheduler;
import com.moesegfault.banking.presentation.gui.mvc.GuiModel;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import com.moesegfault.banking.presentation.gui.view.MainWindowView;
import com.moesegfault.banking.presentation.gui.view.NativeComponentView;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;

/**
 * @brief Swing GUI 运行时（Swing GUI Runtime），管理主窗口生命周期与页面挂载；
 *        Swing runtime implementation that manages main window lifecycle and page mounting.
 */
public final class SwingGuiRuntime implements GuiRuntime {

    /**
     * @brief 主窗口适配器（Main Window Adapter）；
     *        Swing-backed main window view.
     */
    private final MainWindowView mainWindowView;

    /**
     * @brief UI 线程调度器（UI Thread Scheduler）；
     *        UI thread scheduler used for EDT dispatching.
     */
    private final UiThreadScheduler uiThreadScheduler;

    /**
     * @brief 当前挂载页面（Current Mounted View）；
     *        Currently mounted page view.
     */
    private GuiView<? extends GuiModel> mountedView;

    /**
     * @brief 运行状态（Started Flag）；
     *        Runtime started flag.
     */
    private final AtomicBoolean started = new AtomicBoolean(false);

    /**
     * @brief 使用默认 Swing 组件构造运行时；
     *        Construct runtime with default Swing collaborators.
     */
    public SwingGuiRuntime() {
        this(new SwingMainWindowView(), new SwingUiThreadScheduler());
    }

    /**
     * @brief 构造运行时并注入依赖；
     *        Construct runtime with injected dependencies.
     *
     * @param mainWindowView 主窗口视图（Main window view）。
     * @param uiThreadScheduler UI 线程调度器（UI thread scheduler）。
     */
    public SwingGuiRuntime(final MainWindowView mainWindowView,
                           final UiThreadScheduler uiThreadScheduler) {
        this.mainWindowView = Objects.requireNonNull(mainWindowView, "mainWindowView must not be null");
        this.uiThreadScheduler = Objects.requireNonNull(uiThreadScheduler, "uiThreadScheduler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        if (!started.compareAndSet(false, true)) {
            return;
        }
        uiThreadScheduler.executeAndWait(mainWindowView::show);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mount(final GuiView<? extends GuiModel> view) {
        final GuiView<? extends GuiModel> nextView = Objects.requireNonNull(view, "view must not be null");
        if (!started.get()) {
            start();
        }

        uiThreadScheduler.executeAndWait(() -> {
            if (mountedView != null) {
                mountedView.unmount();
            }
            mountedView = nextView;
            mountedView.mount();
            mountedView.render();
            mountNativeComponent(mountedView);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MainWindowView mainWindow() {
        return mainWindowView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (!started.compareAndSet(true, false)) {
            return;
        }

        uiThreadScheduler.executeAndWait(() -> {
            if (mountedView != null) {
                mountedView.unmount();
                mountedView = null;
            }
            mainWindowView.close();
        });
    }

    /**
     * @brief 挂载页面原生组件（Mount Page Native Component）；
     *        Mount the page native component when the view exposes one.
     *
     * @param view 页面视图（Page view）。
     */
    private void mountNativeComponent(final GuiView<? extends GuiModel> view) {
        if (!(view instanceof NativeComponentView nativeComponentView)) {
            return;
        }

        final Object component = Objects.requireNonNull(
                nativeComponentView.component(),
                "native component view must not return null component");
        if (!(component instanceof JComponent swingComponent)) {
            throw new IllegalArgumentException(
                    "SwingGuiRuntime expects JComponent for native component view: "
                            + component.getClass().getName());
        }
        mainWindowView.setContent(swingComponent);
    }
}
