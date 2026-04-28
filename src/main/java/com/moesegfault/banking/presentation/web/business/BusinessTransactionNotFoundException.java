package com.moesegfault.banking.presentation.web.business;

/**
 * @brief 业务流水未找到异常（Business Transaction Not Found Exception），用于触发统一 404 错误映射；
 *        Exception for missing business transaction, used to trigger unified HTTP 404 mapping.
 */
public final class BusinessTransactionNotFoundException extends RuntimeException {

    /**
     * @brief 构造未找到异常（Construct Not Found Exception）；
     *        Construct not-found exception with message.
     *
     * @param message 异常消息（Exception message）。
     */
    public BusinessTransactionNotFoundException(final String message) {
        super(message);
    }
}

