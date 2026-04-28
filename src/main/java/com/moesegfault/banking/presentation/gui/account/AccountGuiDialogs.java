package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.presentation.gui.view.ErrorDialogView;
import com.moesegfault.banking.presentation.gui.view.SuccessDialogView;

/**
 * @brief Account GUI 对话框默认实现（Account GUI Dialog Defaults），提供空操作弹窗实现；
 *        Account GUI dialog defaults providing no-op dialog implementations.
 */
final class AccountGuiDialogs {

    /**
     * @brief 空操作错误弹窗（No-op Error Dialog）;
     *        No-op error-dialog instance.
     */
    static final ErrorDialogView NOOP_ERROR_DIALOG = (title, message) -> {
    };

    /**
     * @brief 空操作成功弹窗（No-op Success Dialog）;
     *        No-op success-dialog instance.
     */
    static final SuccessDialogView NOOP_SUCCESS_DIALOG = (title, message) -> {
    };

    /**
     * @brief 私有构造（Private Constructor）;
     *        Private constructor for utility class.
     */
    private AccountGuiDialogs() {
    }
}
