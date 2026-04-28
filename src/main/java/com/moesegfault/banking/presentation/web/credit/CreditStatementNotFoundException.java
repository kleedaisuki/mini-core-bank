package com.moesegfault.banking.presentation.web.credit;

/**
 * @brief 信用账单未找到异常（Credit Statement Not Found Exception），用于触发 Web 404 映射；
 *        Credit-statement not-found exception used for triggering HTTP 404 mapping.
 */
public final class CreditStatementNotFoundException extends RuntimeException {

    /**
     * @brief 构造未找到异常（Construct Not-found Exception）；
     *        Construct not-found exception.
     *
     * @param message 错误消息（Error message）。
     */
    public CreditStatementNotFoundException(final String message) {
        super(message);
    }
}
