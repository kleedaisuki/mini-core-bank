package com.moesegfault.banking.presentation.gui.customer;

import com.moesegfault.banking.application.customer.result.RegisterCustomerResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 客户注册页面模型（Register Customer Model），保存表单值、校验错误与提交状态；
 *        Register-customer page model storing form values, validation errors, and submit status.
 */
public final class RegisterCustomerModel extends AbstractGuiModel {

    /** @brief 字段名：证件类型（Field: Identity Document Type）；Field name for identity document type. */
    public static final String FIELD_ID_TYPE = "id_type";

    /** @brief 字段名：证件号码（Field: Identity Document Number）；Field name for identity document number. */
    public static final String FIELD_ID_NUMBER = "id_number";

    /** @brief 字段名：签发地区（Field: Issuing Region）；Field name for issuing region. */
    public static final String FIELD_ISSUING_REGION = "issuing_region";

    /** @brief 字段名：手机号（Field: Mobile Phone）；Field name for mobile phone. */
    public static final String FIELD_MOBILE_PHONE = "mobile_phone";

    /** @brief 字段名：居住地址（Field: Residential Address）；Field name for residential address. */
    public static final String FIELD_RESIDENTIAL_ADDRESS = "residential_address";

    /** @brief 字段名：通信地址（Field: Mailing Address）；Field name for mailing address. */
    public static final String FIELD_MAILING_ADDRESS = "mailing_address";

    /** @brief 字段名：美国税务居民标记（Field: US Tax Resident Flag）；Field name for US tax resident flag. */
    public static final String FIELD_IS_US_TAX_RESIDENT = "is_us_tax_resident";

    /** @brief 字段名：CRS 信息（Field: CRS Information）；Field name for CRS information. */
    public static final String FIELD_CRS_INFO = "crs_info";

    /**
     * @brief 注册表单字段顺序（Register Form Field Order）；
     *        Register form field order aligned with schema language.
     */
    public static final List<String> FIELD_ORDER = List.of(
            FIELD_ID_TYPE,
            FIELD_ID_NUMBER,
            FIELD_ISSUING_REGION,
            FIELD_MOBILE_PHONE,
            FIELD_RESIDENTIAL_ADDRESS,
            FIELD_MAILING_ADDRESS,
            FIELD_IS_US_TAX_RESIDENT,
            FIELD_CRS_INFO);

    /**
     * @brief 表单值映射（Form Value Map）；
     *        Current form values mapped by schema field names.
     */
    private Map<String, String> formValues = emptyFormValues();

    /**
     * @brief 字段错误映射（Field Error Map）；
     *        Current field-level validation errors.
     */
    private Map<String, String> fieldErrors = Map.of();

    /**
     * @brief 页面错误消息（Page Error Message）；
     *        Page-level error message.
     */
    private String errorMessage;

    /**
     * @brief 页面成功消息（Page Success Message）；
     *        Page-level success message.
     */
    private String successMessage;

    /**
     * @brief 提交中状态（Submitting Flag）；
     *        Whether current register action is submitting.
     */
    private boolean submitting;

    /**
     * @brief 最近注册结果（Last Register Result）；
     *        Last successful register result.
     */
    private RegisterCustomerResult lastRegisterResult;

    /**
     * @brief 返回表单值快照（Return Form Value Snapshot）；
     *        Return immutable snapshot of current form values.
     *
     * @return 表单值快照（Form value snapshot）。
     */
    public Map<String, String> formValues() {
        return Map.copyOf(formValues);
    }

    /**
     * @brief 批量覆盖表单值（Replace Form Values）；
     *        Replace current form values with normalized schema map.
     *
     * @param rawValues 原始表单值（Raw form values）。
     */
    public void replaceFormValues(final Map<String, String> rawValues) {
        this.formValues = normalizeFormValues(rawValues);
        fireChanged("form_values");
    }

    /**
     * @brief 返回字段错误快照（Return Field Error Snapshot）；
     *        Return immutable snapshot of field-level errors.
     *
     * @return 字段错误快照（Field error snapshot）。
     */
    public Map<String, String> fieldErrors() {
        return Map.copyOf(fieldErrors);
    }

    /**
     * @brief 批量设置字段错误（Replace Field Errors）；
     *        Replace field-level validation errors.
     *
     * @param errors 字段错误映射（Field-error map）。
     */
    public void replaceFieldErrors(final Map<String, String> errors) {
        this.fieldErrors = normalizeFieldErrors(errors);
        fireChanged("field_errors");
    }

    /**
     * @brief 清空字段错误（Clear Field Errors）；
     *        Clear all field-level validation errors.
     */
    public void clearFieldErrors() {
        this.fieldErrors = Map.of();
        fireChanged("field_errors");
    }

    /**
     * @brief 设置页面错误消息（Set Page Error Message）；
     *        Set page-level error message.
     *
     * @param errorMessage 错误消息（Error message）。
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
        fireChanged("error_message");
    }

    /**
     * @brief 返回页面错误消息（Return Page Error Message）；
     *        Return page-level error message.
     *
     * @return 错误消息可选值（Optional error message）。
     */
    public Optional<String> errorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    /**
     * @brief 设置页面成功消息（Set Page Success Message）；
     *        Set page-level success message.
     *
     * @param successMessage 成功消息（Success message）。
     */
    public void setSuccessMessage(final String successMessage) {
        this.successMessage = successMessage;
        fireChanged("success_message");
    }

    /**
     * @brief 返回页面成功消息（Return Page Success Message）；
     *        Return page-level success message.
     *
     * @return 成功消息可选值（Optional success message）。
     */
    public Optional<String> successMessage() {
        return Optional.ofNullable(successMessage);
    }

    /**
     * @brief 设置提交状态（Set Submitting Flag）；
     *        Set submitting flag.
     *
     * @param submitting 提交状态（Submitting flag）。
     */
    public void setSubmitting(final boolean submitting) {
        this.submitting = submitting;
        fireChanged("submitting");
    }

    /**
     * @brief 返回提交状态（Return Submitting Flag）；
     *        Return submitting flag.
     *
     * @return 提交状态（Submitting flag）。
     */
    public boolean submitting() {
        return submitting;
    }

    /**
     * @brief 标记注册成功（Mark Register Success）；
     *        Mark register success and cache result.
     *
     * @param registerResult 注册结果（Register result）。
     */
    public void markRegisterSuccess(final RegisterCustomerResult registerResult) {
        this.lastRegisterResult = Objects.requireNonNull(registerResult, "registerResult must not be null");
        fireChanged("last_register_result");
    }

    /**
     * @brief 返回最近注册结果（Return Last Register Result）；
     *        Return last successful register result.
     *
     * @return 注册结果可选值（Optional register result）。
     */
    public Optional<RegisterCustomerResult> lastRegisterResult() {
        return Optional.ofNullable(lastRegisterResult);
    }

    /**
     * @brief 创建空表单映射（Create Empty Form Map）；
     *        Create empty form-value map with all schema keys.
     *
     * @return 空表单映射（Empty form map）。
     */
    private static Map<String, String> emptyFormValues() {
        final Map<String, String> values = new LinkedHashMap<>();
        for (String fieldName : FIELD_ORDER) {
            values.put(fieldName, "");
        }
        return Map.copyOf(values);
    }

    /**
     * @brief 规范化表单值（Normalize Form Values）；
     *        Normalize raw form values into schema-aligned ordered map.
     *
     * @param rawValues 原始表单值（Raw form values）。
     * @return 规范化表单值（Normalized form values）。
     */
    private static Map<String, String> normalizeFormValues(final Map<String, String> rawValues) {
        final Map<String, String> normalizedRawValues =
                Map.copyOf(Objects.requireNonNull(rawValues, "rawValues must not be null"));
        final Map<String, String> normalizedValues = new LinkedHashMap<>();
        for (String fieldName : FIELD_ORDER) {
            normalizedValues.put(fieldName, Objects.requireNonNullElse(normalizedRawValues.get(fieldName), ""));
        }
        return Map.copyOf(normalizedValues);
    }

    /**
     * @brief 规范化字段错误（Normalize Field Errors）；
     *        Normalize field-error map with non-null keys and values.
     *
     * @param errors 原始错误映射（Raw error map）。
     * @return 规范化错误映射（Normalized error map）。
     */
    private static Map<String, String> normalizeFieldErrors(final Map<String, String> errors) {
        final Map<String, String> normalizedErrors = new LinkedHashMap<>();
        final Map<String, String> rawErrors = Map.copyOf(Objects.requireNonNull(errors, "errors must not be null"));
        for (Map.Entry<String, String> entry : rawErrors.entrySet()) {
            final String key = Objects.requireNonNull(entry.getKey(), "field-error key must not be null");
            final String value = Objects.requireNonNull(entry.getValue(), "field-error value must not be null");
            normalizedErrors.put(key, value);
        }
        return Map.copyOf(normalizedErrors);
    }
}
