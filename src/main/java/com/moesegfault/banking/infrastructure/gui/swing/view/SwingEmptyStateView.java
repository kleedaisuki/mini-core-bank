package com.moesegfault.banking.infrastructure.gui.swing.view;

import com.moesegfault.banking.presentation.gui.view.EmptyStateView;
import java.awt.Component;
import java.util.Objects;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @brief Swing 空态视图（Swing Empty State View），用于无数据场景的提示与引导动作；
 *        Swing empty-state adapter for no-data scenarios and optional action.
 */
public final class SwingEmptyStateView implements EmptyStateView {

    /**
     * @brief 根面板（Root Panel）；
     *        Root panel for empty-state UI.
     */
    private final JPanel panel = new JPanel();

    /**
     * @brief 标题标签（Title Label）；
     *        Title label of empty state.
     */
    private final JLabel titleLabel = new JLabel("No Data");

    /**
     * @brief 描述标签（Description Label）；
     *        Description label of empty state.
     */
    private final JLabel descriptionLabel = new JLabel("Nothing to display.");

    /**
     * @brief 动作按钮（Action Button）；
     *        Optional action button.
     */
    private final JButton actionButton = new JButton();

    /**
     * @brief 动作回调（Action Callback）；
     *        Callback invoked on action-button click.
     */
    private Runnable action = () -> {
    };

    /**
     * @brief 构造空态视图；
     *        Construct Swing empty-state view.
     */
    public SwingEmptyStateView() {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        actionButton.setVisible(false);
        actionButton.addActionListener(event -> action.run());

        panel.add(Box.createVerticalGlue());
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(descriptionLabel);
        panel.add(Box.createVerticalStrut(12));
        panel.add(actionButton);
        panel.add(Box.createVerticalGlue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(final String title) {
        titleLabel.setText(Objects.requireNonNull(title, "title must not be null"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(final String description) {
        descriptionLabel.setText(Objects.requireNonNull(description, "description must not be null"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAction(final String actionLabel, final Runnable action) {
        final String label = Objects.requireNonNull(actionLabel, "actionLabel must not be null").trim();
        this.action = Objects.requireNonNull(action, "action must not be null");

        if (label.isEmpty()) {
            actionButton.setVisible(false);
            actionButton.setText("");
            return;
        }

        actionButton.setVisible(true);
        actionButton.setText(label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object component() {
        return panel;
    }
}
