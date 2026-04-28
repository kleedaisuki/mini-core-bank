package com.moesegfault.banking.presentation.gui.ledger;

import com.moesegfault.banking.application.ledger.result.BalanceResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 余额查询页面模型（Show Balance Model），保存查询输入、结果与反馈消息；
 *        Balance-query page model storing query inputs, result snapshot and feedback messages.
 */
public final class ShowBalanceModel extends AbstractGuiModel {

    /**
     * @brief 账户编号输入（Account Identifier Input）；
     *        Account-id input text.
     */
    private String accountIdInput = "";

    /**
     * @brief 币种代码输入（Currency Code Input）；
     *        Currency-code input text.
     */
    private String currencyCodeInput = "";

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
    private String hintMessage = "请输入 account_id 与 currency_code 后点击 Submit";

    /**
     * @brief 余额查询结果（Balance Query Result）；
     *        Optional balance query result from application layer.
     */
    private BalanceResult balanceResult;

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
     * @brief 获取币种代码输入（Get Currency Code Input）；
     *        Get currency-code input text.
     *
     * @return 币种代码输入（Currency-code input）。
     */
    public String currencyCodeInput() {
        return currencyCodeInput;
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
     * @brief 获取余额结果（Get Balance Result）；
     *        Get optional balance result.
     *
     * @return 余额结果可选值（Optional balance result）。
     */
    public Optional<BalanceResult> balanceResult() {
        return Optional.ofNullable(balanceResult);
    }

    /**
     * @brief 更新查询输入（Update Query Inputs）；
     *        Update query input fields with normalized text.
     *
     * @param accountIdInput 账户编号输入（Account-id input）。
     * @param currencyCodeInput 币种代码输入（Currency-code input）。
     */
    public void updateQueryInputs(final String accountIdInput, final String currencyCodeInput) {
        this.accountIdInput = normalizeNullable(accountIdInput);
        this.currencyCodeInput = normalizeNullable(currencyCodeInput);
        fireChanged("account_id", "currency_code");
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
     *        Show user-facing error message and clear result.
     *
     * @param message 错误消息（Error message）。
     */
    public void showError(final String message) {
        this.errorMessage = normalizeNullable(message);
        this.balanceResult = null;
        this.hintMessage = "";
        fireChanged("error_message", "balance_result", "hint_message");
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
     * @brief 展示余额结果（Show Balance Result）；
     *        Show balance result and clear error/hint messages.
     *
     * @param result 余额结果（Balance result）。
     */
    public void showBalance(final BalanceResult result) {
        this.balanceResult = Objects.requireNonNull(result, "result must not be null");
        this.errorMessage = "";
        this.hintMessage = "";
        fireChanged("balance_result", "error_message", "hint_message");
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
