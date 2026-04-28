package com.moesegfault.banking.application.account;

/**
 * @brief 账户应用层异常基类（Account Application Exception Base Class）；
 *        Base runtime exception for account application layer.
 */
public class AccountApplicationException extends RuntimeException {

    /**
     * @brief 构造账户应用层异常（Construct Account Application Exception）；
     *        Construct account application exception.
     *
     * @param message 异常消息（Exception message）。
     */
    public AccountApplicationException(final String message) {
        super(message);
    }
}
