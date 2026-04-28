package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @brief 账户列表页面模型（List Accounts Model），维护筛选条件、结果列表和选择状态；
 *        List-accounts page model storing filters, result list, and selection state.
 */
public final class ListAccountsModel extends AbstractGuiModel {

    /**
     * @brief 客户 ID 筛选值（Customer ID Filter Value）;
     *        Customer-id filter value.
     */
    private String customerId = "";

    /**
     * @brief 包含已关闭账户标志（Include Closed Accounts Flag）;
     *        Include-closed-accounts filter flag.
     */
    private boolean includeClosedAccounts;

    /**
     * @brief 加载中标志（Loading Flag）;
     *        Whether page is currently loading.
     */
    private boolean loading;

    /**
     * @brief 页面错误消息（Page Error Message）;
     *        User-facing error message, nullable.
     */
    private String errorMessage;

    /**
     * @brief 账户结果列表（Account Result List）;
     *        Account result list.
     */
    private List<AccountResult> accountResults = List.of();

    /**
     * @brief 当前选中行下标（Selected Row Index）;
     *        Selected row index, nullable.
     */
    private Integer selectedRowIndex;

    /**
     * @brief 更新筛选值（Update Filter Values）;
     *        Update customer filter and include-closed flag.
     *
     * @param customerId 客户 ID（Customer ID）。
     * @param includeClosedAccounts 包含已关闭账户（Include closed accounts）。
     */
    public void setFilterValues(final String customerId, final boolean includeClosedAccounts) {
        this.customerId = normalizeNullable(customerId);
        this.includeClosedAccounts = includeClosedAccounts;
        fireChanged(AccountGuiSchema.CUSTOMER_ID, AccountGuiSchema.INCLUDE_CLOSED_ACCOUNTS);
    }

    /**
     * @brief 获取筛选值快照（Get Filter Snapshot）;
     *        Get immutable filter-value snapshot.
     *
     * @return 筛选值（Filter values）。
     */
    public Map<String, String> filterValues() {
        return Map.of(
                AccountGuiSchema.CUSTOMER_ID, customerId,
                AccountGuiSchema.INCLUDE_CLOSED_ACCOUNTS, Boolean.toString(includeClosedAccounts)
        );
    }

    /**
     * @brief 设置加载状态（Set Loading State）;
     *        Set loading flag.
     *
     * @param loading 加载中标志（Loading flag）。
     */
    public void setLoading(final boolean loading) {
        this.loading = loading;
        fireChanged("loading");
    }

    /**
     * @brief 设置错误消息（Set Error Message）;
     *        Set error message and clear result list.
     *
     * @param errorMessage 错误消息（Error message, nullable）。
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = normalizeNullable(errorMessage);
        this.accountResults = List.of();
        this.selectedRowIndex = null;
        fireChanged("errorMessage", "accountResults", "selectedRowIndex");
    }

    /**
     * @brief 设置账户列表（Set Account-result List）;
     *        Set account results and clear errors.
     *
     * @param accountResults 账户结果列表（Account-result list）。
     */
    public void setAccountResults(final List<AccountResult> accountResults) {
        this.accountResults = List.copyOf(accountResults);
        this.errorMessage = null;
        this.selectedRowIndex = null;
        fireChanged("accountResults", "errorMessage", "selectedRowIndex");
    }

    /**
     * @brief 设置选中行下标（Set Selected Row Index）;
     *        Set selected row index.
     *
     * @param selectedRowIndex 选中行下标（Selected row index, nullable）。
     */
    public void setSelectedRowIndex(final Integer selectedRowIndex) {
        if (selectedRowIndex != null && (selectedRowIndex < 0 || selectedRowIndex >= accountResults.size())) {
            throw new IllegalArgumentException("selectedRowIndex out of range");
        }
        this.selectedRowIndex = selectedRowIndex;
        fireChanged("selectedRowIndex");
    }

    /**
     * @brief 获取加载状态（Get Loading State）;
     *        Get loading flag.
     *
     * @return 加载中标志（Loading flag）。
     */
    public boolean loading() {
        return loading;
    }

    /**
     * @brief 获取错误消息（Get Error Message）;
     *        Get user-facing error message.
     *
     * @return 错误消息（Error message, nullable）。
     */
    public String errorMessage() {
        return errorMessage;
    }

    /**
     * @brief 获取账户结果列表（Get Account-result List）;
     *        Get account result list snapshot.
     *
     * @return 账户结果列表（Account-result list）。
     */
    public List<AccountResult> accountResults() {
        return new ArrayList<>(accountResults);
    }

    /**
     * @brief 获取选中行下标（Get Selected Row Index）;
     *        Get selected row index.
     *
     * @return 选中行下标（Selected row index, nullable）。
     */
    public Integer selectedRowIndex() {
        return selectedRowIndex;
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
