package com.moesegfault.banking.infrastructure.gui.swing.view;

import com.moesegfault.banking.presentation.gui.view.StatusBarView;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @brief Swing 状态栏视图（Swing Status Bar View），显示当前用户、页面与消息；
 *        Swing status bar adapter showing current user, page and status message.
 */
public final class SwingStatusBarView implements StatusBarView {

    /**
     * @brief 容器面板（Container Panel）；
     *        Root panel for Swing status bar.
     */
    private final JPanel panel = new JPanel(new BorderLayout());

    /**
     * @brief 用户标签（User Label）；
     *        Label displaying current user.
     */
    private final JLabel userLabel = new JLabel("User: -");

    /**
     * @brief 页面标签（Page Label）；
     *        Label displaying current page.
     */
    private final JLabel pageLabel = new JLabel("Page: -");

    /**
     * @brief 消息标签（Message Label）；
     *        Label displaying status message.
     */
    private final JLabel messageLabel = new JLabel("Ready");

    /**
     * @brief 构造状态栏；
     *        Construct Swing status bar view.
     */
    public SwingStatusBarView() {
        final JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(userLabel);
        leftPanel.add(pageLabel);

        final JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(messageLabel);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrentUser(final String username) {
        userLabel.setText("User: " + Objects.requireNonNull(username, "username must not be null"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrentPage(final String pageName) {
        pageLabel.setText("Page: " + Objects.requireNonNull(pageName, "pageName must not be null"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMessage(final String message) {
        messageLabel.setText(Objects.requireNonNull(message, "message must not be null"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object component() {
        return panel;
    }
}
