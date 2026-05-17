package com.moesegfault.banking.infrastructure.gui.swing.view;

import com.moesegfault.banking.presentation.gui.view.FormView;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

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
     * @brief 标签颜色（Label Color）；
     *        Color used by field labels.
     */
    private static final Color LABEL_COLOR = new Color(36, 41, 47);

    /**
     * @brief 错误颜色（Error Color）；
     *        Color used by field validation messages.
     */
    private static final Color ERROR_COLOR = new Color(176, 42, 55);

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
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));

        submitButton.setPreferredSize(new Dimension(96, 30));
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
            errorLabel.setForeground(ERROR_COLOR);
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
    public void setSubmitLabel(final String label) {
        submitButton.setText(Objects.requireNonNull(label, "label must not be null"));
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

            final JLabel fieldLabel = new JLabel(displayFieldName(fieldName));
            fieldLabel.setForeground(LABEL_COLOR);

            input.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(208, 215, 222)),
                    new EmptyBorder(4, 8, 4, 8)));

            panel.add(fieldLabel, fieldConstraints(0, row));
            panel.add(input, inputConstraints(1, row));
            row++;
            panel.add(errorLabel, errorConstraints(1, row));
            row++;
        }

        panel.add(submitButton, buttonConstraints(1, row));
        panel.revalidate();
        panel.repaint();
    }

    /**
     * @brief 构造字段标签约束（Build Field-label Constraints）；
     *        Build constraints for a field label.
     *
     * @param gridX 列坐标（Grid x）。
     * @param gridY 行坐标（Grid y）。
     * @return GridBagConstraints（布局约束）。
     */
    private static GridBagConstraints fieldConstraints(final int gridX, final int gridY) {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridX;
        constraints.gridy = gridY;
        constraints.weightx = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(5, 0, 2, 12);
        return constraints;
    }

    /**
     * @brief 构造输入框约束（Build Input Constraints）；
     *        Build constraints for a text input.
     *
     * @param gridX 列坐标（Grid x）。
     * @param gridY 行坐标（Grid y）。
     * @return GridBagConstraints（布局约束）。
     */
    private static GridBagConstraints inputConstraints(final int gridX, final int gridY) {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridX;
        constraints.gridy = gridY;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(0, 0, 2, 12);
        return constraints;
    }

    /**
     * @brief 构造错误提示约束（Build Error-label Constraints）；
     *        Build constraints for a field error label.
     *
     * @param gridX 列坐标（Grid x）。
     * @param gridY 行坐标（Grid y）。
     * @return GridBagConstraints（布局约束）。
     */
    private static GridBagConstraints errorConstraints(final int gridX, final int gridY) {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridX;
        constraints.gridy = gridY;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(0, 0, 4, 12);
        return constraints;
    }

    /**
     * @brief 构造按钮约束（Build Button Constraints）；
     *        Build constraints for the submit button.
     *
     * @param gridX 列坐标（Grid x）。
     * @param gridY 行坐标（Grid y）。
     * @return GridBagConstraints（布局约束）。
     */
    private static GridBagConstraints buttonConstraints(final int gridX, final int gridY) {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridX;
        constraints.gridy = gridY;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.insets = new Insets(4, 0, 0, 12);
        return constraints;
    }

    /**
     * @brief 格式化字段名（Format Field Name）；
     *        Format a canonical field name for display.
     *
     * @param fieldName 字段名（Field name）。
     * @return 展示字段名（Display field name）。
     */
    private static String displayFieldName(final String fieldName) {
        final String normalized = Objects.requireNonNull(fieldName, "fieldName must not be null")
                .replace('_', ' ')
                .trim();
        if (normalized.isEmpty()) {
            return "";
        }
        return normalized.substring(0, 1).toUpperCase(Locale.ROOT) + normalized.substring(1) + ':';
    }
}
