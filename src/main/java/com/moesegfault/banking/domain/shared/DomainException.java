package com.moesegfault.banking.domain.shared;

/**
 * @brief 领域异常基类（Domain Exception Base Class），表示业务语义层面的错误；
 * Domain exception base class representing business-semantic errors.
 */
public class DomainException extends RuntimeException {

    /**
     * @brief 构造领域异常（Construct Domain Exception）；
     * Construct a domain exception.
     *
     * @param message 异常消息（Exception message）。
     */
    public DomainException(final String message) {
        super(message);
    }

    /**
     * @brief 构造领域异常并携带根因（Construct Domain Exception with Cause）；
     * Construct a domain exception with root cause.
     *
     * @param message 异常消息（Exception message）。
     * @param cause 根因异常（Root cause）。
     */
    public DomainException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
