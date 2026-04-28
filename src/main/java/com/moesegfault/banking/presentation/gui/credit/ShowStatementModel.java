package com.moesegfault.banking.presentation.gui.credit;

import com.moesegfault.banking.application.credit.result.CreditCardStatementResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 信用卡账单查询页面模型（Show Statement Page Model），保存查询条件、错误信息与查询结果；
 *        Model for show-statement page storing query fields, validation errors, and query result.
 */
public final class ShowStatementModel extends AbstractGuiModel {

    /**
     * @brief 账单 ID 字段名（Statement-id Field Name）；
     *        Canonical optional field name for statement identifier.
     */
    public static final String FIELD_STATEMENT_ID = "statement_id";

    /**
     * @brief 信用卡账户 ID 字段名（Credit-card-account-id Field Name）；
     *        Canonical optional field name for credit-card-account identifier.
     */
    public static final String FIELD_CREDIT_CARD_ACCOUNT_ID = "credit_card_account_id";

    /**
     * @brief 账期开始字段名（Statement-period-start Field Name）；
     *        Canonical optional field name for statement period start.
     */
    public static final String FIELD_STATEMENT_PERIOD_START = "statement_period_start";

    /**
     * @brief 账期结束字段名（Statement-period-end Field Name）；
     *        Canonical optional field name for statement period end.
     */
    public static final String FIELD_STATEMENT_PERIOD_END = "statement_period_end";

    /**
     * @brief 表单值映射（Form Value Map）；
     *        Ordered map storing current query values.
     */
    private final Map<String, String> formValues = new LinkedHashMap<>();

    /**
     * @brief 字段错误映射（Field Error Map）；
     *        Ordered map storing field-level validation errors.
     */
    private final Map<String, String> fieldErrors = new LinkedHashMap<>();

    /**
     * @brief 加载中标记（Loading Flag）；
     *        Flag indicating query is in-flight.
     */
    private boolean loading;

    /**
     * @brief 未找到标记（Not-found Flag）；
     *        Flag indicating query executed but no statement found.
     */
    private boolean notFound;

    /**
     * @brief 用户提示消息（User Message）；
     *        User-facing message, nullable.
     */
    private String userMessageOrNull;

    /**
     * @brief 账单结果快照（Statement Result Snapshot）；
     *        Statement result snapshot, nullable.
     */
    private CreditCardStatementResult statementResultOrNull;

    /**
     * @brief 构造模型并初始化字段（Construct Model With Default Fields）；
     *        Construct model and initialize canonical field map.
     */
    public ShowStatementModel() {
        formValues.put(FIELD_STATEMENT_ID, "");
        formValues.put(FIELD_CREDIT_CARD_ACCOUNT_ID, "");
        formValues.put(FIELD_STATEMENT_PERIOD_START, "");
        formValues.put(FIELD_STATEMENT_PERIOD_END, "");
    }

    /**
     * @brief 批量更新表单值（Replace Form Values）；
     *        Replace known query values from incoming map.
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
     * @brief 设置加载状态（Set Loading State）；
     *        Set in-flight loading state.
     *
     * @param loading 加载中标记（Loading flag）。
     */
    public void setLoading(final boolean loading) {
        if (this.loading == loading) {
            return;
        }
        this.loading = loading;
        fireChanged("loading");
    }

    /**
     * @brief 设置未找到标记（Set Not-found Flag）；
     *        Set not-found state.
     *
     * @param notFound 未找到标记（Not-found flag）。
     */
    public void setNotFound(final boolean notFound) {
        if (this.notFound == notFound) {
            return;
        }
        this.notFound = notFound;
        fireChanged("notFound");
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
     *        Set statement result snapshot.
     *
     * @param statementResult 账单结果（Statement result）。
     */
    public void setStatementResult(final CreditCardStatementResult statementResult) {
        this.statementResultOrNull = Objects.requireNonNull(statementResult, "statementResult must not be null");
        fireChanged("statementResult");
    }

    /**
     * @brief 清空账单结果（Clear Statement Result）；
     *        Clear statement result snapshot.
     */
    public void clearStatementResult() {
        if (statementResultOrNull == null) {
            return;
        }
        statementResultOrNull = null;
        fireChanged("statementResult");
    }

    /**
     * @brief 获取表单值快照（Get Form Value Snapshot）；
     *        Get immutable snapshot of query values.
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
     * @brief 是否加载中（Is Loading）；
     *        Check whether query is in-flight.
     *
     * @return 加载中标记（Loading flag）。
     */
    public boolean loading() {
        return loading;
    }

    /**
     * @brief 是否未找到（Is Not Found）；
     *        Check whether query returned not-found.
     *
     * @return 未找到标记（Not-found flag）。
     */
    public boolean notFound() {
        return notFound;
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
     *        Get statement result snapshot.
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
