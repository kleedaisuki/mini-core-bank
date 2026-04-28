package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.application.account.result.OpenAccountResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 开立外汇账户页面模型（Open FX Account Model），维护 FX 开户输入状态与提交结果；
 *        Open-FX-account page model storing FX input state and submission result.
 */
public final class OpenFxAccountModel extends AbstractGuiModel {

    /**
     * @brief 客户 ID 输入值（Customer ID Input）;
     *        Customer-id input value.
     */
    private String customerId = "";

    /**
     * @brief 账户号输入值（Account Number Input）;
     *        Account-number input value.
     */
    private String accountNo = "";

    /**
     * @brief 绑定储蓄账户 ID 输入值（Linked Savings Account ID Input）;
     *        Linked-savings-account-id input value.
     */
    private String linkedSavingsAccountId = "";

    /**
     * @brief 提交中标志（Submitting Flag）;
     *        Whether page is currently submitting.
     */
    private boolean submitting;

    /**
     * @brief 页面错误消息（Page Error Message）;
     *        User-facing error message, nullable.
     */
    private String errorMessage;

    /**
     * @brief 最近成功结果（Latest Success Result）;
     *        Latest successful open-account result, nullable.
     */
    private OpenAccountResult openAccountResult;

    /**
     * @brief 更新表单值（Update Form Values）;
     *        Update FX form values.
     *
     * @param customerId 客户 ID（Customer ID）。
     * @param accountNo 账户号（Account number）。
     * @param linkedSavingsAccountId 绑定储蓄账户 ID（Linked savings-account ID）。
     */
    public void setFormValues(
            final String customerId,
            final String accountNo,
            final String linkedSavingsAccountId
    ) {
        this.customerId = normalizeNullable(customerId);
        this.accountNo = normalizeNullable(accountNo);
        this.linkedSavingsAccountId = normalizeNullable(linkedSavingsAccountId);
        fireChanged(
                AccountGuiSchema.CUSTOMER_ID,
                AccountGuiSchema.ACCOUNT_NO,
                AccountGuiSchema.LINKED_SAVINGS_ACCOUNT_ID);
    }

    /**
     * @brief 获取表单值快照（Get Form-value Snapshot）;
     *        Get immutable snapshot for form rendering.
     *
     * @return 表单值（Form values）。
     */
    public Map<String, String> formValues() {
        return Map.of(
                AccountGuiSchema.CUSTOMER_ID, customerId,
                AccountGuiSchema.ACCOUNT_NO, accountNo,
                AccountGuiSchema.LINKED_SAVINGS_ACCOUNT_ID, linkedSavingsAccountId
        );
    }

    /**
     * @brief 设置提交状态（Set Submitting State）;
     *        Set submitting flag.
     *
     * @param submitting 提交中标志（Submitting flag）。
     */
    public void setSubmitting(final boolean submitting) {
        this.submitting = submitting;
        fireChanged("submitting");
    }

    /**
     * @brief 设置错误消息（Set Error Message）;
     *        Set user-facing error message.
     *
     * @param errorMessage 错误消息（Error message, nullable）。
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = normalizeNullable(errorMessage);
        this.openAccountResult = null;
        fireChanged("errorMessage", "openAccountResult");
    }

    /**
     * @brief 设置成功结果（Set Success Result）;
     *        Set successful open-account result and clear errors.
     *
     * @param result 开户结果（Open-account result）。
     */
    public void setSuccessResult(final OpenAccountResult result) {
        this.openAccountResult = Objects.requireNonNull(result, "result must not be null");
        this.errorMessage = null;
        fireChanged("openAccountResult", "errorMessage");
    }

    /**
     * @brief 读取提交状态（Get Submitting State）;
     *        Get current submitting flag.
     *
     * @return 提交中标志（Submitting flag）。
     */
    public boolean submitting() {
        return submitting;
    }

    /**
     * @brief 读取错误消息（Get Error Message）;
     *        Get current error message.
     *
     * @return 错误消息（Error message, nullable）。
     */
    public String errorMessage() {
        return errorMessage;
    }

    /**
     * @brief 读取成功结果（Get Success Result）;
     *        Get latest successful result.
     *
     * @return 开户结果（Open-account result, nullable）。
     */
    public OpenAccountResult openAccountResult() {
        return openAccountResult;
    }

    /**
     * @brief 规范化可空文本（Normalize Nullable Text）;
     *        Normalize nullable text by trimming.
     *
     * @param raw 原始文本（Raw text）。
     * @return 规范文本（Normalized text），null 保持为 null。
     */
    private static String normalizeNullable(final String raw) {
        if (raw == null) {
            return null;
        }
        return raw.trim();
    }
}
