package com.moesegfault.banking.presentation.gui;

import com.moesegfault.banking.domain.shared.DomainException;
import java.util.Objects;

/**
 * @brief GUI 异常处理器（GUI Exception Handler），将异常映射为用户可读消息；
 *        GUI exception handler mapping exceptions to user-readable messages.
 */
public final class GuiExceptionHandler {

    /**
     * @brief 异常转用户消息（Map Exception To User Message）；
     *        Map one exception to user-readable message.
     *
     * @param throwable 异常对象（Exception object）。
     * @return 用户消息（User-facing message）。
     */
    public String toUserMessage(final Throwable throwable) {
        final Throwable normalizedThrowable = Objects.requireNonNull(throwable, "throwable must not be null");
        final String message = normalizedThrowable.getMessage();
        if (normalizedThrowable instanceof DomainException) {
            return nonBlankOrFallback(message, "业务规则校验失败");
        }
        if (normalizedThrowable instanceof IllegalArgumentException) {
            return "输入参数不合法: " + nonBlankOrFallback(message, "请检查输入值");
        }
        return "系统暂时不可用，请稍后重试";
    }

    /**
     * @brief 规范化消息文本（Normalize Message Text）；
     *        Normalize message text with fallback value.
     *
     * @param message 原始消息（Raw message）。
     * @param fallback 回退消息（Fallback message）。
     * @return 非空消息（Non-blank message）。
     */
    private static String nonBlankOrFallback(final String message, final String fallback) {
        if (message == null || message.trim().isEmpty()) {
            return fallback;
        }
        return message.trim();
    }
}
