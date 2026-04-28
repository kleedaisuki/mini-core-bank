package com.moesegfault.banking.presentation.gui.view;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @brief 表格视图抽象（Table View），用于列表型数据展示与行选择；
 *        Table abstraction for tabular data rendering and row selection.
 */
public interface TableView {

    /**
     * @brief 设置列定义（Set Columns）；
     *        Replace current column headers.
     *
     * @param columns 列名列表（Column names）。
     */
    void setColumns(List<String> columns);

    /**
     * @brief 设置行数据（Set Rows）；
     *        Replace current row data.
     *
     * @param rows 行数据（Row data）。
     */
    void setRows(List<List<String>> rows);

    /**
     * @brief 获取当前选中行下标（Get Selected Row Index）；
     *        Get selected row index if present.
     *
     * @return 选中行下标（Selected row index），无选中则 empty。
     */
    Optional<Integer> selectedRowIndex();

    /**
     * @brief 绑定行选择监听器（Bind Row Selection Listener）；
     *        Register callback invoked on row selection.
     *
     * @param listener 行选择监听器（Row selection listener）。
     */
    void onRowSelected(Consumer<Integer> listener);

    /**
     * @brief 获取 toolkit 原生组件（Get Toolkit-native Component）；
     *        Return underlying toolkit component object.
     *
     * @return toolkit 原生组件（Toolkit-native component）。
     */
    Object component();
}
