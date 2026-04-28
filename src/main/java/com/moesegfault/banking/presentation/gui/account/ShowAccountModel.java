package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 账户详情页面模型（Show Account Model），维护查询条件、加载状态与账户详情结果；
 *        Show-account page model storing query inputs, loading state, and detail result.
 */
public final class ShowAccountModel extends AbstractGuiModel {

    /**
     * @brief 账户 ID 查询值（Account ID Query Value）;
     *        Account-id query value.
     */
    private String accountId = "";

    /**
     * @brief 账户号查询值（Account Number Query Value）;
     *        Account-number query value.
     */
    private String accountNo = "";

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
     * @brief 查询结果（Query Result）;
     *        Account result, nullable.
     */
    private AccountResult accountResult;

    /**
     * @brief 更新查询表单值（Update Query Form Values）;
     *        Update query form values.
     *
     * @param accountId 账户 ID（Account ID）。
     * @param accountNo 账户号（Account number）。
     */
    public void setQueryValues(final String accountId, final String accountNo) {
        this.accountId = normalizeNullable(accountId);
        this.accountNo = normalizeNullable(accountNo);
        fireChanged(AccountGuiSchema.ACCOUNT_ID, AccountGuiSchema.ACCOUNT_NO);
    }

    /**
     * @brief 获取查询表单快照（Get Query-form Snapshot）;
     *        Get immutable query-form snapshot.
     *
     * @return 查询表单值（Query-form values）。
     */
    public Map<String, String> queryValues() {
        return Map.of(
                AccountGuiSchema.ACCOUNT_ID, accountId,
                AccountGuiSchema.ACCOUNT_NO, accountNo
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
     *        Set user-facing error message and clear result.
     *
     * @param errorMessage 错误消息（Error message, nullable）。
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = normalizeNullable(errorMessage);
        this.accountResult = null;
        fireChanged("errorMessage", "accountResult");
    }

    /**
     * @brief 设置查询结果（Set Query Result）;
     *        Set query result and clear errors.
     *
     * @param accountResult 账户结果（Account result）。
     */
    public void setAccountResult(final AccountResult accountResult) {
        this.accountResult = Objects.requireNonNull(accountResult, "accountResult must not be null");
        this.errorMessage = null;
        fireChanged("accountResult", "errorMessage");
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
     * @brief 获取查询结果（Get Query Result）;
     *        Get account query result.
     *
     * @return 账户结果（Account result, nullable）。
     */
    public AccountResult accountResult() {
        return accountResult;
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
