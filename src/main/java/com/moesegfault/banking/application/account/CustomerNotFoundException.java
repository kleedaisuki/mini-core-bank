package com.moesegfault.banking.application.account;

/**
 * @brief 客户不存在异常（Customer Not Found Exception）；
 *        Exception thrown when target customer does not exist.
 */
public final class CustomerNotFoundException extends AccountApplicationException {

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
