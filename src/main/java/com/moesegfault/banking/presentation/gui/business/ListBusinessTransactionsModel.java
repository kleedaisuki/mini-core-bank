package com.moesegfault.banking.presentation.gui.business;

import com.moesegfault.banking.application.business.query.ListBusinessTransactionsQuery;
import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import java.util.List;

import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;

/**
 * @brief 业务流水列表页面模型（List Business Transactions Model），保存筛选条件、列表结果和选择状态；
 *        Business-transaction list page model holding filters, result list, and row-selection state.
 */
public final class ListBusinessTransactionsModel extends AbstractGuiModel {

    /**
     * @brief 发起客户 ID 过滤条件（Initiator-Customer-ID Filter）；
     *        Initiator-customer-id filter text.
     */
    private String initiatorCustomerId;

    /**
     * @brief 交易状态过滤条件（Transaction-Status Filter）；
     *        Transaction-status filter text.
     */
    private String transactionStatus;

    /**
     * @brief 返回上限（Result Limit）；
     *        Result row limit.
     */
    private int limit = ListBusinessTransactionsQuery.DEFAULT_LIMIT;

    /**
     * @brief 加载中标记（Loading Flag）；
     *        Loading flag.
     */
    private boolean loading;

    /**
     * @brief 错误消息（Error Message）；
     *        User-facing error message.
     */
    private String errorMessage;

    /**
     * @brief 列表结果（List Result）；
     *        Loaded business-transaction list result.
     */
    private List<BusinessTransactionResult> transactions = List.of();

    /**
     * @brief 当前选中交易 ID（Selected Transaction ID）；
     *        Selected transaction id in current list.
     */
    private String selectedTransactionId;

    /**
     * @brief 设置筛选条件（Set Filters）；
     *        Set list filter values.
     *
     * @param initiatorCustomerId 发起客户 ID（Initiator customer ID, nullable）。
     * @param transactionStatus 交易状态（Transaction status, nullable）。
     * @param limit 返回上限（Result limit, > 0）。
     */
    public void setFilters(final String initiatorCustomerId, final String transactionStatus, final int limit) {
        this.initiatorCustomerId = normalizeNullableText(initiatorCustomerId);
        this.transactionStatus = normalizeNullableText(transactionStatus);
        this.limit = limit;
        fireChanged(BusinessGuiSchema.INITIATOR_CUSTOMER_ID, BusinessGuiSchema.TRANSACTION_STATUS, BusinessGuiSchema.LIMIT);
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
     * @brief 设置列表结果（Set List Result）；
     *        Set loaded list result.
     *
     * @param transactions 业务流水列表（Business-transaction list）。
     */
    public void setTransactions(final List<BusinessTransactionResult> transactions) {
        this.transactions = List.copyOf(transactions);
        fireChanged("transactions");
    }

    /**
     * @brief 设置选中交易 ID（Set Selected Transaction ID）；
     *        Set selected transaction id.
     *
     * @param selectedTransactionId 选中交易 ID（Selected transaction id, nullable）。
     */
    public void setSelectedTransactionId(final String selectedTransactionId) {
        this.selectedTransactionId = normalizeNullableText(selectedTransactionId);
        fireChanged("selected_transaction_id");
    }

    /**
     * @brief 获取发起客户 ID 过滤条件（Get Initiator-Customer-ID Filter）；
     *        Get initiator-customer-id filter.
     *
     * @return 发起客户 ID（Initiator customer ID, nullable）。
     */
    public String initiatorCustomerIdOrNull() {
        return initiatorCustomerId;
    }

    /**
     * @brief 获取交易状态过滤条件（Get Transaction-Status Filter）；
     *        Get transaction-status filter.
     *
     * @return 交易状态（Transaction status, nullable）。
     */
    public String transactionStatusOrNull() {
        return transactionStatus;
    }

    /**
     * @brief 获取返回上限（Get Result Limit）；
     *        Get result limit.
     *
     * @return 返回上限（Result limit）。
     */
    public int limit() {
        return limit;
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
     * @brief 获取列表结果（Get List Result）；
     *        Get loaded list result.
     *
     * @return 列表结果（List result）。
     */
    public List<BusinessTransactionResult> transactions() {
        return transactions;
    }

    /**
     * @brief 获取当前选中交易 ID（Get Selected Transaction ID）；
     *        Get selected transaction id.
     *
     * @return 选中交易 ID（Selected transaction id, nullable）。
     */
    public String selectedTransactionIdOrNull() {
        return selectedTransactionId;
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
