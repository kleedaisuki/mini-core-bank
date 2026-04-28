package com.moesegfault.banking.domain.shared;

/**
 * @brief 业务规则违反异常（Business Rule Violation），用于表达领域不变量被破坏；
 *        Exception indicating a domain invariant or business rule has been
 *        violated.
 */
public final class BusinessRuleViolation extends DomainException {

    /**
     * @brief 构造业务规则违反异常（Construct Business Rule Violation）；
     *        Construct a business rule violation exception.
     *
     * @param message 异常消息（Exception message）。
     */
    public BusinessRuleViolation(final String message) {
        super(message);
    }

    /**
     * @brief 构造业务规则违反异常并携带根因（Construct Violation with Cause）；
     *        Construct a violation exception with root cause.
     *
     * @param message 异常消息（Exception message）。
     * @param cause   根因异常（Root cause）。
     */
    public BusinessRuleViolation(final String message, final Throwable cause) {
        super(message, cause);
    }
}
