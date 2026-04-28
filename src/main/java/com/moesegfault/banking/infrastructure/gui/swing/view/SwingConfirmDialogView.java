package com.moesegfault.banking.infrastructure.gui.swing.view;

import com.moesegfault.banking.presentation.gui.view.ConfirmDialogView;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.util.Objects;
import javax.swing.JOptionPane;

/**
 * @brief Swing 确认弹窗视图（Swing Confirm Dialog View），处理用户确认操作；
 *        Swing confirmation dialog adapter for user decisions.
 */
public final class SwingConfirmDialogView implements ConfirmDialogView {

    /**
     * @brief 父组件（Parent Component）；
     *        Parent component used by JOptionPane.
     */
    private final Component parent;

    /**
     * @brief 使用空父组件构造弹窗；
     *        Construct dialog view without parent component.
     */
    public SwingConfirmDialogView() {
        this(null);
    }

    /**
     * @brief 指定父组件构造弹窗；
     *        Construct dialog view with parent component.
     *
     * @param parent 父组件（Parent component）。
     */
    public SwingConfirmDialogView(final Component parent) {
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean confirm(final String title, final String message) {
        final String resolvedTitle = Objects.requireNonNull(title, "title must not be null");
        final String resolvedMessage = Objects.requireNonNull(message, "message must not be null");
        if (GraphicsEnvironment.isHeadless()) {
            return false;
        }

        final int result = JOptionPane.showConfirmDialog(
                parent,
                resolvedMessage,
                resolvedTitle,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }
}
