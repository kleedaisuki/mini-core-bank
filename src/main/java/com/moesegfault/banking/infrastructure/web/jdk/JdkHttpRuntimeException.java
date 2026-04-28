package com.moesegfault.banking.infrastructure.web.jdk;

/**
 * @brief JDK HTTP Runtime 异常（JDK HTTP Runtime Exception），表示运行时配置或执行错误；
 *        Runtime exception for JDK HttpServer runtime configuration or execution failures.
 */
public final class JdkHttpRuntimeException extends RuntimeException {

    /**
     * @brief 使用消息构造异常（Construct Exception with Message）；
     *        Construct exception with message.
     *
     * @param message 异常消息（Exception message）。
     */
    public JdkHttpRuntimeException(final String message) {
        super(message);
    }

    /**
     * @brief 使用消息与原因构造异常（Construct Exception with Message and Cause）；
     *        Construct exception with message and cause.
     *
     * @param message 异常消息（Exception message）。
     * @param cause 根因异常（Cause exception）。
     */
    public JdkHttpRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
