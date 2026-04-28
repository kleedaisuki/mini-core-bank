package com.moesegfault.banking.presentation.gui.ledger;

import com.moesegfault.banking.application.ledger.result.LedgerEntryResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import java.util.List;

/**
 * @brief 分录查询页面模型（Show Entries Model），保存查询输入、分录列表与反馈消息；
 *        Ledger-entries query page model storing query inputs, entries and feedback messages.
 */
public final class ShowEntriesModel extends AbstractGuiModel {

    /**
     * @brief 账户编号输入（Account Identifier Input）；
     *        Account-id input text.
     */
    private String accountIdInput = "";

    /**
     * @brief 条数上限输入（Limit Input）；
     *        Limit input text.
     */
    private String limitInput = "";

    /**
     * @brief 加载状态（Loading Flag）；
     *        Loading flag while querying application layer.
     */
    private boolean loading;

    /**
     * @brief 错误消息（Error Message）；
     *        User-facing error message.
     */
    private String errorMessage = "";

    /**
     * @brief 提示消息（Hint Message）；
     *        User-facing hint/info message.
     */
    private String hintMessage = "请输入 account_id 与 limit 后点击 Submit";

    /**
     * @brief 分录结果列表（Ledger-entry Results）；
     *        Ledger-entry result list.
     */
    private List<LedgerEntryResult> entries = List.of();

    /**
     * @brief 获取账户编号输入（Get Account Identifier Input）；
     *        Get account-id input text.
     *
     * @return 账户编号输入（Account-id input）。
     */
    public String accountIdInput() {
        return accountIdInput;
    }

    /**
     * @brief 获取条数上限输入（Get Limit Input）；
     *        Get limit input text.
     *
     * @return 条数上限输入（Limit input）。
     */
    public String limitInput() {
        return limitInput;
    }

    /**
     * @brief 获取加载状态（Get Loading Flag）；
     *        Get loading flag.
     *
     * @return 是否加载中（Whether currently loading）。
     */
    public boolean loading() {
        return loading;
    }

    /**
     * @brief 获取错误消息（Get Error Message）；
     *        Get user-facing error message.
     *
     * @return 错误消息（Error message）。
     */
    public String errorMessage() {
        return errorMessage;
    }

    /**
     * @brief 获取提示消息（Get Hint Message）；
     *        Get user-facing hint/info message.
     *
     * @return 提示消息（Hint message）。
     */
    public String hintMessage() {
        return hintMessage;
    }

    /**
     * @brief 获取分录结果列表（Get Ledger-entry Results）；
     *        Get ledger-entry result list.
     *
     * @return 分录列表（Ledger-entry list）。
     */
    public List<LedgerEntryResult> entries() {
        return entries;
    }

    /**
     * @brief 更新查询输入（Update Query Inputs）；
     *        Update query input fields with normalized text.
     *
     * @param accountIdInput 账户编号输入（Account-id input）。
     * @param limitInput 条数上限输入（Limit input）。
     */
    public void updateQueryInputs(final String accountIdInput, final String limitInput) {
        this.accountIdInput = normalizeNullable(accountIdInput);
        this.limitInput = normalizeNullable(limitInput);
        fireChanged("account_id", "limit");
    }

    /**
     * @brief 设置加载状态（Set Loading Flag）；
     *        Set loading flag.
     *
     * @param loading 是否加载中（Whether loading）。
     */
    public void setLoading(final boolean loading) {
        this.loading = loading;
        fireChanged("loading");
    }

    /**
     * @brief 展示错误消息（Show Error Message）；
     *        Show user-facing error message and clear entries/hints.
     *
     * @param message 错误消息（Error message）。
     */
    public void showError(final String message) {
        this.errorMessage = normalizeNullable(message);
        this.entries = List.of();
        this.hintMessage = "";
        fireChanged("error_message", "entries", "hint_message");
    }

    /**
     * @brief 展示提示消息（Show Hint Message）；
     *        Show user-facing hint/info message and clear error.
     *
     * @param message 提示消息（Hint message）。
     */
    public void showHint(final String message) {
        this.errorMessage = "";
        this.hintMessage = normalizeNullable(message);
        fireChanged("error_message", "hint_message");
    }

    /**
     * @brief 展示分录列表（Show Ledger-entry Results）；
     *        Show ledger-entry list and clear error/hint messages.
     *
     * @param entries 分录列表（Ledger-entry list）。
     */
    public void showEntries(final List<LedgerEntryResult> entries) {
        this.entries = List.copyOf(entries);
        this.errorMessage = "";
        this.hintMessage = "";
        fireChanged("entries", "error_message", "hint_message");
    }

    /**
     * @brief 规范化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text by trimming and replacing null with empty string.
     *
     * @param text 原始文本（Raw text）。
     * @return 规范化文本（Normalized text）。
     */
    private static String normalizeNullable(final String text) {
        if (text == null) {
            return "";
        }
        return text.trim();
    }
}
