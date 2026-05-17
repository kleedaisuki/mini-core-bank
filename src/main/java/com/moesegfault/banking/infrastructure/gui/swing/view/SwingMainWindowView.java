package com.moesegfault.banking.infrastructure.gui.swing.view;

import com.moesegfault.banking.presentation.gui.view.MainMenuView;
import com.moesegfault.banking.presentation.gui.view.MainWindowView;
import com.moesegfault.banking.presentation.gui.view.StatusBarView;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

/**
 * @brief Swing 主窗口视图（Swing Main Window View），适配主窗口菜单、内容区与状态栏；
 *        Swing adapter for main window menu/content/status composition.
 */
public final class SwingMainWindowView implements MainWindowView {

    /**
     * @brief 默认窗口标题（Default Window Title）；
     *        Default title shown on Swing frame.
     */
    private static final String DEFAULT_TITLE = "Mini Core Bank";

    /**
     * @brief 默认宽度（Default Width）；
     *        Default Swing frame width.
     */
    private static final int DEFAULT_WIDTH = 1200;

    /**
     * @brief 默认高度（Default Height）；
     *        Default Swing frame height.
     */
    private static final int DEFAULT_HEIGHT = 760;

    /**
     * @brief 窗口对象（Frame）；
     *        Swing frame. Null when running in headless mode.
     */
    private final JFrame frame;

    /**
     * @brief 根面板（Root Panel）；
     *        Root panel hosting content and status components.
     */
    private final JPanel rootPanel;

    /**
     * @brief 内容面板（Content Panel）；
     *        Center panel for page content.
     */
    private final JPanel contentPanel;

    /**
     * @brief 当前状态栏组件（Status Bar Component）；
     *        Current status bar component.
     */
    private JComponent statusBarComponent;

    /**
     * @brief 构造主窗口；
     *        Construct Swing main window view.
     */
    public SwingMainWindowView() {
        configureLookAndFeel();
        this.rootPanel = new JPanel(new BorderLayout());
        this.contentPanel = new JPanel(new BorderLayout());
        this.rootPanel.add(contentPanel, BorderLayout.CENTER);

        if (GraphicsEnvironment.isHeadless()) {
            this.frame = null;
            return;
        }

        this.frame = new JFrame(DEFAULT_TITLE);
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.frame.setLocationRelativeTo(null);
        this.frame.setContentPane(rootPanel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(final String title) {
        final String nonNullTitle = Objects.requireNonNull(title, "title must not be null");
        if (frame != null) {
            frame.setTitle(nonNullTitle);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMainMenu(final MainMenuView mainMenuView) {
        final Object component = Objects.requireNonNull(mainMenuView, "mainMenuView must not be null").component();
        if (frame == null) {
            return;
        }

        if (component instanceof JMenuBar menuBar) {
            frame.setJMenuBar(menuBar);
            frame.revalidate();
            frame.repaint();
            return;
        }

        throw new IllegalArgumentException("SwingMainWindowView expects JMenuBar for main menu component");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatusBar(final StatusBarView statusBarView) {
        final Object component = Objects.requireNonNull(statusBarView, "statusBarView must not be null").component();
        if (!(component instanceof JComponent swingComponent)) {
            throw new IllegalArgumentException("SwingMainWindowView expects JComponent for status bar component");
        }

        if (statusBarComponent != null) {
            rootPanel.remove(statusBarComponent);
        }
        statusBarComponent = swingComponent;
        rootPanel.add(statusBarComponent, BorderLayout.SOUTH);
        rootPanel.revalidate();
        rootPanel.repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContent(final Object content) {
        if (!(Objects.requireNonNull(content, "content must not be null") instanceof JComponent swingComponent)) {
            throw new IllegalArgumentException("SwingMainWindowView expects JComponent for content component");
        }

        contentPanel.removeAll();
        contentPanel.add(swingComponent, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show() {
        if (frame != null) {
            frame.setVisible(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (frame != null) {
            frame.dispose();
        }
    }

    /**
     * @brief 配置外观主题（Configure Look And Feel）；
     *        Configure a readable cross-platform Swing look and feel before components are created.
     */
    private static void configureLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo lookAndFeelInfo : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(lookAndFeelInfo.getName())) {
                    UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException
                 | InstantiationException
                 | IllegalAccessException
                 | UnsupportedLookAndFeelException ignored) {
            // 平台无法加载首选主题时保留 Swing 默认外观；keep Swing's default when the preferred theme is unavailable.
        }
    }
}
