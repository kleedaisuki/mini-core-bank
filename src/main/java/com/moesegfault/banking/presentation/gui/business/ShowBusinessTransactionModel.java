package com.moesegfault.banking.presentation.gui.business;

import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;

/**
 * @brief 业务流水详情页面模型（Show Business Transaction Model），保存查询条件、加载态与详情结果；
 *        Business-transaction detail page model holding selectors, loading state, and detail result.
 */
public final class ShowBusinessTransactionModel extends AbstractGuiModel {

    /**
     * @brief 交易 ID 查询条件（Transaction-ID Selector）;
     *        Transaction-id selector text.
     */
    private String transactionId;

    /**
     * @brief 参考号查询条件（Reference-Number Selector）;
     *        Reference-number selector text.
     */
    private String referenceNo;

    /**
     * @brief 加载中标记（Loading Flag）;
     *        Loading flag.
     */
    private boolean loading;

    /**
     * @brief 错误消息（Error Message）;
     *        User-facing error message.
     */
    private String errorMessage;

    /**
     * @brief 详情结果（Detail Result）;
     *        Loaded business-transaction detail result.
     */
    private BusinessTransactionResult transaction;

    /**
     * @brief 设置查询条件（Set Query Selectors）；
     *        Set query selector texts.
     *
     * @param transactionId 交易 ID（Transaction ID, nullable）。
     * @param referenceNo 参考号（Reference number, nullable）。
     */
    public void setSelectors(final String transactionId, final String referenceNo) {
        this.transactionId = normalizeNullableText(transactionId);
        this.referenceNo = normalizeNullableText(referenceNo);
        fireChanged(BusinessGuiSchema.TRANSACTION_ID, BusinessGuiSchema.REFERENCE_NO);
    }

    /**
     * @brief 设置加载状态（Set Loading State）；
     *        Set loading state.
     *
     * @param loading 加载标记（Loading flag）。
     */
    public void setLoading(final boolean loading) {
        this.loading = loading;
        fireChanged("loading");
    }

    /**
     * @brief 设置错误消息（Set Error Message）；
     *        Set user-facing error message.
     *
     * @param errorMessage 错误消息（Error message, nullable）。
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = normalizeNullableText(errorMessage);
        fireChanged("error_message");
    }

    /**
     * @brief 设置详情结果（Set Detail Result）；
     *        Set loaded business-transaction detail result.
     *
     * @param transaction 详情结果（Detail result, nullable）。
     */
    public void setTransaction(final BusinessTransactionResult transaction) {
        this.transaction = transaction;
        fireChanged("transaction");
    }

    /**
     * @brief 获取交易 ID 查询条件（Get Transaction-ID Selector）；
     *        Get transaction-id selector text.
     *
     * @return 交易 ID（Transaction ID, nullable）。
     */
    public String transactionIdOrNull() {
        return transactionId;
    }

    /**
     * @brief 获取参考号查询条件（Get Reference-Number Selector）；
     *        Get reference-number selector text.
     *
     * @return 参考号（Reference number, nullable）。
     */
    public String referenceNoOrNull() {
        return referenceNo;
    }

    /**
     * @brief 判断是否加载中（Check Loading State）；
     *        Check whether page is loading.
     *
     * @return 加载中返回 true（true when loading）。
     */
    public boolean isLoading() {
        return loading;
    }

    /**
     * @brief 获取错误消息（Get Error Message）；
     *        Get user-facing error message.
     *
     * @return 错误消息（Error message, nullable）。
     */
    public String errorMessageOrNull() {
        return errorMessage;
    }

    /**
     * @brief 获取详情结果（Get Detail Result）；
     *        Get business-transaction detail result.
     *
     * @return 详情结果（Detail result, nullable）。
     */
    public BusinessTransactionResult transactionOrNull() {
        return transaction;
    }

    /**
     * @brief 标准化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text by trimming and collapsing blank to null.
     *
     * @param rawValue 原始文本（Raw text, nullable）。
     * @return 标准化文本（Normalized text or null）。
     */
    private static String normalizeNullableText(final String rawValue) {
        if (rawValue == null) {
            return null;
        }
        final String normalized = rawValue.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
