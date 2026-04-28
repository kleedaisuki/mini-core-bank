package com.moesegfault.banking.presentation.gui.view;

import java.util.List;
import java.util.Map;

/**
 * @brief 表单视图抽象（Form View），封装字段布局、取值、错误提示和提交动作；
 *        Form abstraction for field layout, values, validation errors and submit action.
 */
public interface FormView {

    /**
     * @brief 设置字段顺序（Set Field Order）；
     *        Define visible fields in given order.
     *
     * @param fieldNames 字段名列表（Field names）。
     */
    void setFieldOrder(List<String> fieldNames);

    /**
     * @brief 批量设置字段值（Set Field Values）；
     *        Apply field values in batch.
     *
     * @param values 字段值映射（Field-value map）。
     */
    void setValues(Map<String, String> values);

    /**
     * @brief 获取当前字段值（Get Field Values）；
     *        Read current field values.
     *
     * @return 字段值映射（Field-value map）。
     */
    Map<String, String> values();

    /**
     * @brief 设置字段错误提示（Set Field Error）；
     *        Set one field-level validation error message.
     *
     * @param fieldName 字段名（Field name）。
     * @param message 错误消息（Error message）。
     */
    void setFieldError(String fieldName, String message);

    /**
     * @brief 清空全部错误提示（Clear All Errors）；
     *        Clear all field-level validation messages.
     */
    void clearErrors();

    /**
     * @brief 绑定提交动作（Bind Submit Action）；
     *        Bind submit action invoked by form submit button.
     *
     * @param submitAction 提交动作（Submit action）。
     */
    void onSubmit(Runnable submitAction);

    /**
     * @brief 获取 toolkit 原生组件（Get Toolkit-native Component）；
     *        Return underlying toolkit component object.
     *
     * @return toolkit 原生组件（Toolkit-native component）。
     */
    Object component();
}
