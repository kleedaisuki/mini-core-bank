package com.moesegfault.banking.application.account;

/**
 * @brief 账户不存在异常（Account Not Found Exception）；
 *        Exception thrown when target account does not exist.
 */
public final class AccountNotFoundException extends AccountApplicationException {

    /**
     * @brief 构造账户不存在异常（Construct Account Not Found Exception）；
     *        Construct account-not-found exception.
     *
     * @param message 异常消息（Exception message）。
     */
    public AccountNotFoundException(final String message) {
        super(message);
    }
}
