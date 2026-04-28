package com.moesegfault.banking.infrastructure.gui.swing.view;

import com.moesegfault.banking.presentation.gui.view.SuccessDialogView;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.util.Objects;
import javax.swing.JOptionPane;

/**
 * @brief Swing 成功弹窗视图（Swing Success Dialog View），展示成功消息；
 *        Swing success dialog adapter for positive feedback.
 */
public final class SwingSuccessDialogView implements SuccessDialogView {

    /**
     * @brief 父组件（Parent Component）；
     *        Parent component used by JOptionPane.
     */
    private final Component parent;

    /**
     * @brief 使用空父组件构造弹窗；
     *        Construct dialog view without parent component.
     */
    public SwingSuccessDialogView() {
        this(null);
    }

    /**
     * @brief 指定父组件构造弹窗；
     *        Construct dialog view with parent component.
     *
     * @param parent 父组件（Parent component）。
     */
    public SwingSuccessDialogView(final Component parent) {
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showSuccess(final String title, final String message) {
        final String resolvedTitle = Objects.requireNonNull(title, "title must not be null");
        final String resolvedMessage = Objects.requireNonNull(message, "message must not be null");
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("[SUCCESS] " + resolvedTitle + " - " + resolvedMessage);
            return;
        }
        JOptionPane.showMessageDialog(parent, resolvedMessage, resolvedTitle, JOptionPane.INFORMATION_MESSAGE);
    }
}
