package com.moesegfault.banking.presentation.gui.credit;

import com.moesegfault.banking.application.credit.result.CreditCardStatementResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 生成信用卡账单页面模型（Generate Statement Page Model），保存表单输入、校验错误与结果快照；
 *        Model for generate-statement page storing form inputs, validation errors, and result snapshot.
 */
public final class GenerateStatementModel extends AbstractGuiModel {

    /**
     * @brief 信用卡账户 ID 字段名（Credit-card-account-id Field Name）；
     *        Canonical field name for credit-card-account identifier.
     */
    public static final String FIELD_CREDIT_CARD_ACCOUNT_ID = "credit_card_account_id";

    /**
     * @brief 出账日期字段名（Statement-date Field Name）；
     *        Canonical field name for statement date.
     */
    public static final String FIELD_STATEMENT_DATE = "statement_date";

    /**
     * @brief 最低还款比例字段名（Minimum-payment-rate Field Name）；
     *        Canonical field name for minimum-payment-rate decimal.
     */
    public static final String FIELD_MINIMUM_PAYMENT_RATE_DECIMAL = "minimum_payment_rate_decimal";

    /**
     * @brief 最低还款保底金额字段名（Minimum-payment-floor Field Name）；
     *        Canonical field name for minimum-payment-floor amount.
     */
    public static final String FIELD_MINIMUM_PAYMENT_FLOOR_AMOUNT = "minimum_payment_floor_amount";

    /**
     * @brief 表单值映射（Form Value Map）；
     *        Ordered map storing current form values.
     */
    private final Map<String, String> formValues = new LinkedHashMap<>();

    /**
     * @brief 字段错误映射（Field Error Map）；
     *        Ordered map storing field-level validation errors.
     */
    private final Map<String, String> fieldErrors = new LinkedHashMap<>();

    /**
     * @brief 提交中标记（Submitting Flag）；
     *        Flag indicating in-flight submission.
     */
    private boolean submitting;

    /**
     * @brief 用户提示消息（User Message）；
     *        User-facing message, nullable.
     */
    private String userMessageOrNull;

    /**
     * @brief 账单结果快照（Statement Result Snapshot）；
     *        Generated statement snapshot, nullable.
     */
    private CreditCardStatementResult statementResultOrNull;

    /**
     * @brief 构造模型并初始化字段（Construct Model With Default Fields）；
     *        Construct model and initialize canonical field map.
     */
    public GenerateStatementModel() {
        formValues.put(FIELD_CREDIT_CARD_ACCOUNT_ID, "");
        formValues.put(FIELD_STATEMENT_DATE, "");
        formValues.put(FIELD_MINIMUM_PAYMENT_RATE_DECIMAL, "");
        formValues.put(FIELD_MINIMUM_PAYMENT_FLOOR_AMOUNT, "");
    }

    /**
     * @brief 批量更新表单值（Replace Form Values）；
     *        Replace known form values from incoming map.
     *
     * @param values 表单值映射（Form value map）。
     */
    public void replaceFormValues(final Map<String, String> values) {
        final Map<String, String> normalizedValues = Map.copyOf(Objects.requireNonNull(values, "values must not be null"));
        for (String fieldName : formValues.keySet()) {
            if (normalizedValues.containsKey(fieldName)) {
                formValues.put(fieldName, normalizeNullableText(normalizedValues.get(fieldName)));
            }
        }
        fireChanged("formValues");
    }

    /**
     * @brief 设置字段错误（Set Field Error）；
     *        Set one field-level validation error.
     *
     * @param fieldName 字段名（Field name）。
     * @param errorMessage 错误消息（Error message）。
     */
    public void setFieldError(final String fieldName, final String errorMessage) {
        final String normalizedFieldName = requireText(fieldName, "fieldName");
        final String normalizedErrorMessage = requireText(errorMessage, "errorMessage");
        fieldErrors.put(normalizedFieldName, normalizedErrorMessage);
        fireChanged("fieldErrors");
    }

    /**
     * @brief 清空字段错误（Clear Field Errors）；
     *        Clear all field-level errors.
     */
    public void clearFieldErrors() {
        if (fieldErrors.isEmpty()) {
            return;
        }
        fieldErrors.clear();
        fireChanged("fieldErrors");
    }

    /**
     * @brief 设置提交状态（Set Submitting State）；
     *        Set in-flight submitting state.
     *
     * @param submitting 提交中标记（Submitting flag）。
     */
    public void setSubmitting(final boolean submitting) {
        if (this.submitting == submitting) {
            return;
        }
        this.submitting = submitting;
        fireChanged("submitting");
    }

    /**
     * @brief 设置用户消息（Set User Message）；
     *        Set user-facing message.
     *
     * @param userMessageOrNull 用户消息（User message, nullable）。
     */
    public void setUserMessageOrNull(final String userMessageOrNull) {
        final String normalizedMessage = normalizeNullableText(userMessageOrNull);
        if (Objects.equals(this.userMessageOrNull, normalizedMessage)) {
            return;
        }
        this.userMessageOrNull = normalizedMessage;
        fireChanged("userMessage");
    }

    /**
     * @brief 设置账单结果（Set Statement Result）；
     *        Set generated statement result snapshot.
     *
     * @param statementResult 账单结果（Statement result）。
     */
    public void setStatementResult(final CreditCardStatementResult statementResult) {
        this.statementResultOrNull = Objects.requireNonNull(statementResult, "statementResult must not be null");
        fireChanged("statementResult");
    }

    /**
     * @brief 获取表单值快照（Get Form Value Snapshot）；
     *        Get immutable snapshot of form values.
     *
     * @return 表单值快照（Form value snapshot）。
     */
    public Map<String, String> formValues() {
        return Map.copyOf(formValues);
    }

    /**
     * @brief 获取字段错误快照（Get Field Error Snapshot）；
     *        Get immutable snapshot of field errors.
     *
     * @return 字段错误快照（Field error snapshot）。
     */
    public Map<String, String> fieldErrors() {
        return Map.copyOf(fieldErrors);
    }

    /**
     * @brief 是否提交中（Is Submitting）；
     *        Check whether submission is in-flight.
     *
     * @return 提交中标记（Submitting flag）。
     */
    public boolean submitting() {
        return submitting;
    }

    /**
     * @brief 获取用户消息（Get User Message）；
     *        Get user-facing message.
     *
     * @return 用户消息（User message），不存在则 empty。
     */
    public Optional<String> userMessage() {
        return Optional.ofNullable(userMessageOrNull);
    }

    /**
     * @brief 获取账单结果（Get Statement Result）；
     *        Get generated statement result snapshot.
     *
     * @return 账单结果（Statement result），不存在则 empty。
     */
    public Optional<CreditCardStatementResult> statementResult() {
        return Optional.ofNullable(statementResultOrNull);
    }

    /**
     * @brief 校验非空文本（Require Non-blank Text）；
     *        Require non-blank text.
     *
     * @param value 值（Value）。
     * @param fieldName 字段名（Field name）。
     * @return 规范化文本（Normalized text）。
     */
    private static String requireText(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    /**
     * @brief 规范化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text by trimming or empty-string fallback.
     *
     * @param value 值（Value）。
     * @return 规范化文本（Normalized text）。
     */
    private static String normalizeNullableText(final String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
}
