package com.moesegfault.banking.presentation.gui.credit;

import com.moesegfault.banking.application.credit.result.RepayCreditCardResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 信用卡还款页面模型（Repay Credit Card Page Model），保存还款表单、错误信息和结果快照；
 *        Model for repay-credit-card page storing repayment form, validation errors, and result snapshot.
 */
public final class RepayCreditCardModel extends AbstractGuiModel {

    /**
     * @brief 信用卡账户 ID 字段名（Credit-card-account-id Field Name）；
     *        Canonical field name for credit-card-account identifier.
     */
    public static final String FIELD_CREDIT_CARD_ACCOUNT_ID = "credit_card_account_id";

    /**
     * @brief 还款金额字段名（Repayment-amount Field Name）；
     *        Canonical field name for repayment amount.
     */
    public static final String FIELD_REPAYMENT_AMOUNT = "repayment_amount";

    /**
     * @brief 还款币种字段名（Repayment-currency-code Field Name）；
     *        Canonical field name for repayment currency code.
     */
    public static final String FIELD_REPAYMENT_CURRENCY_CODE = "repayment_currency_code";

    /**
     * @brief 定向账单 ID 字段名（Statement-id Field Name）；
     *        Canonical optional field name for target statement id.
     */
    public static final String FIELD_STATEMENT_ID = "statement_id";

    /**
     * @brief 来源账户 ID 字段名（Source-account-id Field Name）；
     *        Canonical optional field name for source account id.
     */
    public static final String FIELD_SOURCE_ACCOUNT_ID = "source_account_id";

    /**
     * @brief 业务日期字段名（As-of-date Field Name）；
     *        Canonical optional field name for as-of date.
     */
    public static final String FIELD_AS_OF_DATE = "as_of_date";

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
     *        Flag indicating in-flight repayment submission.
     */
    private boolean submitting;

    /**
     * @brief 用户提示消息（User Message）；
     *        User-facing message, nullable.
     */
    private String userMessageOrNull;

    /**
     * @brief 还款结果快照（Repayment Result Snapshot）；
     *        Repayment result snapshot, nullable.
     */
    private RepayCreditCardResult repayResultOrNull;

    /**
     * @brief 构造模型并初始化字段（Construct Model With Default Fields）；
     *        Construct model and initialize canonical field map.
     */
    public RepayCreditCardModel() {
        formValues.put(FIELD_CREDIT_CARD_ACCOUNT_ID, "");
        formValues.put(FIELD_REPAYMENT_AMOUNT, "");
        formValues.put(FIELD_REPAYMENT_CURRENCY_CODE, "");
        formValues.put(FIELD_STATEMENT_ID, "");
        formValues.put(FIELD_SOURCE_ACCOUNT_ID, "");
        formValues.put(FIELD_AS_OF_DATE, "");
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
     *        Clear all field-level validation errors.
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
     * @brief 设置还款结果（Set Repayment Result）；
     *        Set repayment result snapshot.
     *
     * @param repayResult 还款结果（Repayment result）。
     */
    public void setRepayResult(final RepayCreditCardResult repayResult) {
        this.repayResultOrNull = Objects.requireNonNull(repayResult, "repayResult must not be null");
        fireChanged("repayResult");
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
     * @brief 获取还款结果（Get Repayment Result）；
     *        Get repayment result snapshot.
     *
     * @return 还款结果（Repayment result），不存在则 empty。
     */
    public Optional<RepayCreditCardResult> repayResult() {
        return Optional.ofNullable(repayResultOrNull);
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
