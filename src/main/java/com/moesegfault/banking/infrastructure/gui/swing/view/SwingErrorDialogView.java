package com.moesegfault.banking.infrastructure.gui.swing.view;

import com.moesegfault.banking.presentation.gui.view.ErrorDialogView;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.util.Objects;
import javax.swing.JOptionPane;

/**
 * @brief Swing 错误弹窗视图（Swing Error Dialog View），展示错误消息；
 *        Swing error dialog adapter for failure feedback.
 */
public final class SwingErrorDialogView implements ErrorDialogView {

    /**
     * @brief 父组件（Parent Component）；
     *        Parent component used by JOptionPane.
     */
    private final Component parent;

    /**
     * @brief 使用空父组件构造弹窗；
     *        Construct dialog view without parent component.
     */
    public SwingErrorDialogView() {
        this(null);
    }

    /**
     * @brief 指定父组件构造弹窗；
     *        Construct dialog view with parent component.
     *
     * @param parent 父组件（Parent component）。
     */
    public SwingErrorDialogView(final Component parent) {
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showError(final String title, final String message) {
        final String resolvedTitle = Objects.requireNonNull(title, "title must not be null");
        final String resolvedMessage = Objects.requireNonNull(message, "message must not be null");
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("[ERROR] " + resolvedTitle + " - " + resolvedMessage);
            return;
        }
        JOptionPane.showMessageDialog(parent, resolvedMessage, resolvedTitle, JOptionPane.ERROR_MESSAGE);
    }
}
