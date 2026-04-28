package com.moesegfault.banking.infrastructure.gui.swing.view;

import com.moesegfault.banking.presentation.gui.view.FormView;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @brief Swing 表单视图（Swing Form View），基于 GridBagLayout 构建动态字段表单；
 *        Swing form adapter backed by GridBagLayout with dynamic fields.
 */
public final class SwingFormView implements FormView {

    /**
     * @brief 表单面板（Form Panel）；
     *        Root form panel.
     */
    private final JPanel panel = new JPanel(new GridBagLayout());

    /**
     * @brief 字段输入组件映射（Field Input Map）；
     *        Ordered map from field name to input text field.
     */
    private final Map<String, JTextField> fieldInputs = new LinkedHashMap<>();

    /**
     * @brief 字段错误标签映射（Field Error Map）；
     *        Ordered map from field name to error label.
     */
    private final Map<String, JLabel> fieldErrors = new LinkedHashMap<>();

    /**
     * @brief 提交按钮（Submit Button）；
     *        Submit button for form action.
     */
    private final JButton submitButton = new JButton("Submit");

    /**
     * @brief 提交动作回调（Submit Action）；
     *        Callback invoked when submit button is clicked.
     */
    private Runnable submitAction = () -> {
    };

    /**
     * @brief 构造表单视图；
     *        Construct Swing form view.
     */
    public SwingFormView() {
        submitButton.addActionListener(event -> submitAction.run());
        rebuildPanel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFieldOrder(final List<String> fieldNames) {
        final List<String> nonNullFields = List.copyOf(Objects.requireNonNull(fieldNames, "fieldNames must not be null"));
        fieldInputs.clear();
        fieldErrors.clear();

        for (String fieldName : nonNullFields) {
            final String nonNullFieldName = Objects.requireNonNull(fieldName, "fieldName must not be null");
            fieldInputs.put(nonNullFieldName, new JTextField(24));
            final JLabel errorLabel = new JLabel(" ", SwingConstants.LEFT);
            errorLabel.setForeground(new java.awt.Color(180, 0, 0));
            fieldErrors.put(nonNullFieldName, errorLabel);
        }
        rebuildPanel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValues(final Map<String, String> values) {
        final Map<String, String> nonNullValues = Map.copyOf(Objects.requireNonNull(values, "values must not be null"));
        for (Map.Entry<String, String> entry : nonNullValues.entrySet()) {
            final JTextField input = fieldInputs.get(entry.getKey());
            if (input != null) {
                input.setText(Objects.requireNonNullElse(entry.getValue(), ""));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> values() {
        final Map<String, String> snapshot = new LinkedHashMap<>();
        for (Map.Entry<String, JTextField> entry : fieldInputs.entrySet()) {
            snapshot.put(entry.getKey(), entry.getValue().getText());
        }
        return snapshot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFieldError(final String fieldName, final String message) {
        final JLabel errorLabel = fieldErrors.get(Objects.requireNonNull(fieldName, "fieldName must not be null"));
        if (errorLabel == null) {
            return;
        }
        errorLabel.setText(Objects.requireNonNullElse(message, " "));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearErrors() {
        for (JLabel errorLabel : fieldErrors.values()) {
            errorLabel.setText(" ");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSubmit(final Runnable submitAction) {
        this.submitAction = Objects.requireNonNull(submitAction, "submitAction must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object component() {
        return panel;
    }

    /**
     * @brief 重建面板布局（Rebuild Panel Layout）；
     *        Rebuild Swing layout based on current field set.
     */
    private void rebuildPanel() {
        panel.removeAll();

        int row = 0;
        for (Map.Entry<String, JTextField> entry : fieldInputs.entrySet()) {
            final String fieldName = entry.getKey();
            final JTextField input = entry.getValue();
            final JLabel errorLabel = fieldErrors.get(fieldName);

            panel.add(new JLabel(fieldName + ':'), constraints(0, row, 1, 0.0));
            panel.add(input, constraints(1, row, 1, 1.0));
            row++;
            panel.add(errorLabel, constraints(1, row, 1, 1.0));
            row++;
        }

        panel.add(submitButton, constraints(1, row, 1, 0.0));
        panel.revalidate();
        panel.repaint();
    }

    /**
     * @brief 构造 GridBag 约束（Build GridBag Constraints）；
     *        Build a GridBagConstraints instance for one cell.
     *
     * @param gridX 列坐标（Grid x）。
     * @param gridY 行坐标（Grid y）。
     * @param gridWidth 跨列宽度（Grid width）。
     * @param weightX 横向权重（Horizontal weight）。
     * @return GridBagConstraints（布局约束）。
     */
    private static GridBagConstraints constraints(final int gridX,
                                                  final int gridY,
                                                  final int gridWidth,
                                                  final double weightX) {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridX;
        constraints.gridy = gridY;
        constraints.gridwidth = gridWidth;
        constraints.weightx = weightX;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(6, 6, 2, 6);
        return constraints;
    }
}
