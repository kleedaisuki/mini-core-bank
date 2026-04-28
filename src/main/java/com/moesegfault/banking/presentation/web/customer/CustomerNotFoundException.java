package com.moesegfault.banking.presentation.web.customer;

/**
 * @brief 客户不存在异常（Customer Not Found Exception），用于 Web 层 404 映射；
 *        Customer-not-found exception used for web-layer HTTP 404 mapping.
 */
public final class CustomerNotFoundException extends RuntimeException {

    /**
     * @brief 构造客户不存在异常（Construct Customer Not Found Exception）；
     *        Construct customer-not-found exception.
     *
     * @param message 异常消息（Exception message）。
     */
    public CustomerNotFoundException(final String message) {
        super(message);
    }
}
