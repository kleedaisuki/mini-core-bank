package com.moesegfault.banking.application.card;

/**
 * @brief 卡应用层异常（Card Application Exception），用于表达应用编排阶段的失败；
 *        Card application exception used to represent failures in application orchestration.
 */
public final class CardApplicationException extends RuntimeException {

    /**
     * @brief 使用错误消息构造异常（Construct Exception with Message）；
     *        Construct exception with error message.
     *
     * @param message 错误消息（Error message）。
     */
    public CardApplicationException(final String message) {
        super(message);
    }

    /**
     * @brief 使用错误消息和原因构造异常（Construct Exception with Message and Cause）；
     *        Construct exception with both error message and root cause.
     *
     * @param message 错误消息（Error message）。
     * @param cause   根因异常（Root cause）。
     */
    public CardApplicationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
