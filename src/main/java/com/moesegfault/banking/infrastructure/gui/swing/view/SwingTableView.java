package com.moesegfault.banking.infrastructure.gui.swing.view;

import com.moesegfault.banking.presentation.gui.view.TableView;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 * @brief Swing 表格视图（Swing Table View），基于 JTable 展示列表数据；
 *        Swing table adapter backed by JTable.
 */
public final class SwingTableView implements TableView {

    /**
     * @brief 表格模型（Table Model）；
     *        Backing table model for rows and columns.
     */
    private final DefaultTableModel tableModel = new NonEditableTableModel();

    /**
     * @brief 表格组件（Table）；
     *        JTable component.
     */
    private final JTable table = new JTable(tableModel);

    /**
     * @brief 滚动容器（Scroll Pane）；
     *        Scroll pane wrapping table.
     */
    private final JScrollPane scrollPane = new JScrollPane(table);

    /**
     * @brief 行选择回调（Row Selection Listener）；
     *        Callback invoked on row selection.
     */
    private Consumer<Integer> rowSelectedListener = ignored -> {
    };

    /**
     * @brief Swing 选择监听器（Swing Selection Listener）；
     *        Swing listener instance for row selection.
     */
    private ListSelectionListener swingSelectionListener;

    /**
     * @brief 构造表格视图；
     *        Construct Swing table view.
     */
    public SwingTableView() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColumns(final List<String> columns) {
        final List<String> nonNullColumns = List.copyOf(Objects.requireNonNull(columns, "columns must not be null"));
        tableModel.setColumnIdentifiers(nonNullColumns.toArray(new Object[0]));
        tableModel.setRowCount(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRows(final List<List<String>> rows) {
        final List<List<String>> nonNullRows = List.copyOf(Objects.requireNonNull(rows, "rows must not be null"));
        tableModel.setRowCount(0);
        for (List<String> row : nonNullRows) {
            final List<String> nonNullRow = List.copyOf(Objects.requireNonNull(row, "row must not be null"));
            tableModel.addRow(nonNullRow.toArray(new Object[0]));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Integer> selectedRowIndex() {
        final int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return Optional.empty();
        }
        return Optional.of(table.convertRowIndexToModel(selectedRow));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRowSelected(final Consumer<Integer> listener) {
        this.rowSelectedListener = Objects.requireNonNull(listener, "listener must not be null");
        if (swingSelectionListener != null) {
            table.getSelectionModel().removeListSelectionListener(swingSelectionListener);
        }

        swingSelectionListener = event -> {
            if (event.getValueIsAdjusting()) {
                return;
            }
            selectedRowIndex().ifPresent(rowSelectedListener);
        };
        table.getSelectionModel().addListSelectionListener(swingSelectionListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object component() {
        return scrollPane;
    }

    /**
     * @brief 不可编辑表格模型（Non-editable Table Model）；
     *        Table model that disallows inline cell editing.
     */
    private static final class NonEditableTableModel extends DefaultTableModel {

        /**
         * @brief 单元格是否可编辑（Is Cell Editable）；
         *        Disable inline editing for all cells.
         *
         * @param row 行号（Row index）。
         * @param column 列号（Column index）。
         * @return 恒为 false（Always false）。
         */
        @Override
        public boolean isCellEditable(final int row, final int column) {
            return false;
        }
    }
}
