package com.moesegfault.banking.presentation.gui.card;

import com.moesegfault.banking.application.card.result.IssueCardResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 发卡页面通用模型基类（Common Issue-card Page Model Base），封装表单字段、错误、提交状态与结果快照；
 *        Common base model for issue-card pages encapsulating form fields, errors, submission state, and result snapshot.
 */
abstract class AbstractCardIssueModel extends AbstractGuiModel {

    /**
     * @brief 表单字段值映射（Form Field-value Map）；
     *        Mutable map storing canonical field values.
     */
    private final Map<String, String> formValues = new LinkedHashMap<>();

    /**
     * @brief 字段错误映射（Field Error Map）；
     *        Mutable map storing field-level validation messages.
     */
    private final Map<String, String> fieldErrors = new LinkedHashMap<>();

    /**
     * @brief 提交中标记（Submitting Flag）；
     *        Whether one issuance request is in-flight.
     */
    private boolean submitting;

    /**
     * @brief 全局错误消息（Global Error Message）；
     *        User-facing error message.
     */
    private String errorMessage;

    /**
     * @brief 全局成功消息（Global Success Message）；
     *        User-facing success message.
     */
    private String successMessage;

    /**
     * @brief 最近一次发卡结果（Last Issue Result）；
     *        Snapshot of last successful issue result.
     */
    private IssueCardResult issueResult;

    /**
     * @brief 使用字段顺序构造模型（Construct Model With Field Order）；
     *        Construct model and initialize canonical fields.
     *
     * @param fieldOrder 字段顺序（Field order）。
     */
    protected AbstractCardIssueModel(final List<String> fieldOrder) {
        final List<String> normalizedFieldOrder = List.copyOf(Objects.requireNonNull(fieldOrder, "fieldOrder must not be null"));
        for (String fieldName : normalizedFieldOrder) {
            formValues.put(Objects.requireNonNull(fieldName, "fieldName must not be null"), "");
        }
    }

    /**
     * @brief 更新单个字段值（Update One Field Value）；
     *        Update one field value by canonical field name.
     *
     * @param fieldName 字段名（Field name）。
     * @param fieldValue 字段值（Field value）。
     */
    public void setFieldValue(final String fieldName, final String fieldValue) {
        final String normalizedFieldName = Objects.requireNonNull(fieldName, "fieldName must not be null");
        if (!formValues.containsKey(normalizedFieldName)) {
            throw new IllegalArgumentException("Unsupported field name: " + normalizedFieldName);
        }
        formValues.put(normalizedFieldName, normalizeNullableText(fieldValue));
        fireChanged("formValues");
    }

    /**
     * @brief 批量设置字段值（Set Field Values In Batch）；
     *        Update multiple field values by canonical field names.
     *
     * @param values 字段值映射（Field-value map）。
     */
    public void setFieldValues(final Map<String, String> values) {
        final Map<String, String> normalizedValues = Map.copyOf(Objects.requireNonNull(values, "values must not be null"));
        for (Map.Entry<String, String> entry : normalizedValues.entrySet()) {
            setFieldValue(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @brief 读取单个字段值（Get One Field Value）；
     *        Read one field value by canonical field name.
     *
     * @param fieldName 字段名（Field name）。
     * @return 字段值（Field value）。
     */
    public String fieldValue(final String fieldName) {
        final String normalizedFieldName = Objects.requireNonNull(fieldName, "fieldName must not be null");
        if (!formValues.containsKey(normalizedFieldName)) {
            throw new IllegalArgumentException("Unsupported field name: " + normalizedFieldName);
        }
        return formValues.get(normalizedFieldName);
    }

    /**
     * @brief 获取字段值快照（Get Field-value Snapshot）；
     *        Get immutable snapshot of current field values.
     *
     * @return 字段值快照（Field-value snapshot）。
     */
    public Map<String, String> formValues() {
        return Map.copyOf(formValues);
    }

    /**
     * @brief 设置字段错误（Set Field Error）；
     *        Set one field-level error message.
     *
     * @param fieldName 字段名（Field name）。
     * @param message 错误消息（Error message）。
     */
    public void setFieldError(final String fieldName, final String message) {
        final String normalizedFieldName = Objects.requireNonNull(fieldName, "fieldName must not be null");
        if (!formValues.containsKey(normalizedFieldName)) {
            throw new IllegalArgumentException("Unsupported field name: " + normalizedFieldName);
        }
        fieldErrors.put(normalizedFieldName, normalizeNullableText(message));
        fireChanged("fieldErrors");
    }

    /**
     * @brief 清空字段错误（Clear Field Errors）；
     *        Clear all field-level errors.
     */
    public void clearFieldErrors() {
        fieldErrors.clear();
        fireChanged("fieldErrors");
    }

    /**
     * @brief 获取字段错误快照（Get Field-error Snapshot）；
     *        Get immutable snapshot of field errors.
     *
     * @return 字段错误快照（Field-error snapshot）。
     */
    public Map<String, String> fieldErrors() {
        return Map.copyOf(fieldErrors);
    }

    /**
     * @brief 设置提交状态（Set Submitting Flag）；
     *        Set submission in-flight flag.
     *
     * @param submitting 是否提交中（Whether request is in-flight）。
     */
    public void setSubmitting(final boolean submitting) {
        this.submitting = submitting;
        fireChanged("submitting");
    }

    /**
     * @brief 是否提交中（Is Submitting）；
     *        Whether submission request is in-flight.
     *
     * @return 是否提交中（Submitting flag）。
     */
    public boolean submitting() {
        return submitting;
    }

    /**
     * @brief 设置全局错误消息（Set Global Error Message）；
     *        Set user-facing global error message.
     *
     * @param errorMessage 错误消息（Error message）。
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = normalizeNullableText(errorMessage);
        fireChanged("errorMessage");
    }

    /**
     * @brief 获取全局错误消息（Get Global Error Message）；
     *        Get user-facing global error message.
     *
     * @return 错误消息（Error message）。
     */
    public String errorMessage() {
        return errorMessage;
    }

    /**
     * @brief 设置全局成功消息（Set Global Success Message）；
     *        Set user-facing global success message.
     *
     * @param successMessage 成功消息（Success message）。
     */
    public void setSuccessMessage(final String successMessage) {
        this.successMessage = normalizeNullableText(successMessage);
        fireChanged("successMessage");
    }

    /**
     * @brief 获取全局成功消息（Get Global Success Message）；
     *        Get user-facing global success message.
     *
     * @return 成功消息（Success message）。
     */
    public String successMessage() {
        return successMessage;
    }

    /**
     * @brief 设置最近一次发卡结果（Set Last Issue Result）；
     *        Set snapshot of last successful issue result.
     *
     * @param issueResult 发卡结果（Issue result）。
     */
    public void setIssueResult(final IssueCardResult issueResult) {
        this.issueResult = issueResult;
        fireChanged("issueResult");
    }

    /**
     * @brief 获取最近一次发卡结果（Get Last Issue Result）；
     *        Get snapshot of last successful issue result.
     *
     * @return 发卡结果（Issue result），可能为 null。
     */
    public IssueCardResult issueResultOrNull() {
        return issueResult;
    }

    /**
     * @brief 清理提交反馈消息（Clear Submission Messages）；
     *        Clear global success and error messages.
     */
    public void clearMessages() {
        this.errorMessage = "";
        this.successMessage = "";
        fireChanged("errorMessage", "successMessage");
    }

    /**
     * @brief 规范化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text by trimming and null-to-empty conversion.
     *
     * @param rawText 原始文本（Raw text）。
     * @return 规范化文本（Normalized text）。
     */
    private static String normalizeNullableText(final String rawText) {
        if (rawText == null) {
            return "";
        }
        return rawText.trim();
    }
}
