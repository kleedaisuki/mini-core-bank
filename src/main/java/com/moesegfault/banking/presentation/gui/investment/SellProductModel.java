package com.moesegfault.banking.presentation.gui.investment;

import com.moesegfault.banking.application.investment.result.InvestmentOrderResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 卖出投资产品页面模型（Sell Product Page Model）；
 *        GUI model holding sell-order form state and result feedback.
 */
public final class SellProductModel extends AbstractGuiModel {

    /**
     * @brief 表单字段快照（Form Field Snapshot）；
     *        Form field values keyed by unified schema names.
     */
    private final Map<String, String> formValues = new LinkedHashMap<>();

    /**
     * @brief 提交中标记（Submitting Flag）；
     *        Whether sell command submission is in progress.
     */
    private boolean submitting;

    /**
     * @brief 错误消息（可空）（Error Message, Nullable）；
     *        Error message from failed sell command.
     */
    private String errorMessageOrNull;

    /**
     * @brief 成功消息（可空）（Success Message, Nullable）；
     *        Success message from successful sell command.
     */
    private String successMessageOrNull;

    /**
     * @brief 最近订单结果（可空）（Latest Order Result, Nullable）；
     *        Latest sell-order result for display.
     */
    private InvestmentOrderResult latestResultOrNull;

    /**
     * @brief 构造模型并初始化默认字段（Construct Model with Default Fields）；
     *        Construct model and initialize default form values.
     */
    public SellProductModel() {
        formValues.put(InvestmentGuiSchema.FIELD_CHANNEL, "ONLINE");
    }

    /**
     * @brief 读取表单字段（Read Form Values）；
     *        Read immutable snapshot of current form values.
     *
     * @return 表单字段映射（Form-value map snapshot）。
     */
    public Map<String, String> formValues() {
        return Map.copyOf(formValues);
    }

    /**
     * @brief 批量更新表单字段（Update Form Values In Batch）；
     *        Update form values by replacing keys present in given map.
     *
     * @param values 字段映射（Field-value map）。
     */
    public void updateFormValues(final Map<String, String> values) {
        final Map<String, String> normalizedValues = Objects.requireNonNull(values, "values must not be null");
        normalizedValues.forEach((fieldName, fieldValue) -> {
            final String normalizedFieldName = Objects.requireNonNull(fieldName, "fieldName must not be null");
            formValues.put(normalizedFieldName, fieldValue == null ? "" : fieldValue);
        });
        fireChanged("formValues");
    }

    /**
     * @brief 设置默认发起客户 ID（Set Default Initiator Customer ID）；
     *        Fill initiator customer id when absent.
     *
     * @param customerId 客户 ID（Customer identifier）。
     */
    public void fillInitiatorCustomerIdIfAbsent(final String customerId) {
        final String normalizedCustomerId = Objects.requireNonNull(customerId, "customerId must not be null").trim();
        if (normalizedCustomerId.isEmpty()) {
            return;
        }
        final String currentValue = formValues.get(InvestmentGuiSchema.FIELD_INITIATOR_CUSTOMER_ID);
        if (currentValue == null || currentValue.trim().isEmpty()) {
            formValues.put(InvestmentGuiSchema.FIELD_INITIATOR_CUSTOMER_ID, normalizedCustomerId);
            fireChanged("formValues");
        }
    }

    /**
     * @brief 读取提交中标记（Read Submitting Flag）；
     *        Read whether command submission is in progress.
     *
     * @return 提交中返回 true（true when submitting）。
     */
    public boolean submitting() {
        return submitting;
    }

    /**
     * @brief 读取错误消息（可空）（Read Error Message, Nullable）；
     *        Read error message from last failed submission.
     *
     * @return 错误消息或 null（Error message or null）。
     */
    public String errorMessageOrNull() {
        return errorMessageOrNull;
    }

    /**
     * @brief 读取成功消息（可空）（Read Success Message, Nullable）；
     *        Read success message from last successful submission.
     *
     * @return 成功消息或 null（Success message or null）。
     */
    public String successMessageOrNull() {
        return successMessageOrNull;
    }

    /**
     * @brief 读取最近订单结果（可空）（Read Latest Order Result, Nullable）；
     *        Read latest order result.
     *
     * @return 订单结果或 null（Order result or null）。
     */
    public InvestmentOrderResult latestResultOrNull() {
        return latestResultOrNull;
    }

    /**
     * @brief 标记开始提交（Mark Submission Started）；
     *        Mark model state entering submission phase.
     */
    public void markSubmitting() {
        this.submitting = true;
        this.errorMessageOrNull = null;
        this.successMessageOrNull = null;
        fireChanged("submitting", "errorMessageOrNull", "successMessageOrNull");
    }

    /**
     * @brief 标记提交成功（Mark Submission Succeeded）；
     *        Mark model state after successful sell command.
     *
     * @param result 订单结果（Order result）。
     * @param successMessage 成功消息（Success message）。
     */
    public void markSuccess(final InvestmentOrderResult result, final String successMessage) {
        this.submitting = false;
        this.latestResultOrNull = Objects.requireNonNull(result, "result must not be null");
        this.errorMessageOrNull = null;
        this.successMessageOrNull = Objects.requireNonNull(successMessage, "successMessage must not be null");
        fireChanged("submitting", "latestResultOrNull", "errorMessageOrNull", "successMessageOrNull");
    }

    /**
     * @brief 标记提交失败（Mark Submission Failed）；
     *        Mark model state after failed sell command.
     *
     * @param errorMessage 错误消息（Error message）。
     */
    public void markFailure(final String errorMessage) {
        this.submitting = false;
        this.errorMessageOrNull = Objects.requireNonNull(errorMessage, "errorMessage must not be null");
        this.successMessageOrNull = null;
        fireChanged("submitting", "errorMessageOrNull", "successMessageOrNull");
    }
}
