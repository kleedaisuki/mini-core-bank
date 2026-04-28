package com.moesegfault.banking.presentation.web.dto;

import java.util.Objects;

/**
 * @brief Web 错误响应 DTO（Web Error Response DTO），统一错误码、消息与追踪字段；
 *        Web error-response DTO unifying error code, message, and tracing fields.
 */
public record ErrorResponseDto(
        int status,
        String errorCode,
        String message,
        String requestId,
        String traceId,
        String timestamp
) {

    /**
     * @brief 规范化并校验错误响应 DTO（Normalize and Validate ErrorResponse DTO）；
     *        Normalize and validate error-response DTO fields.
     */
    public ErrorResponseDto {
        if (status < 400 || status > 599) {
            throw new IllegalArgumentException("status must be between 400 and 599");
        }
        errorCode = normalizeRequiredText(errorCode, "errorCode");
        message = normalizeRequiredText(message, "message");
        requestId = normalizeRequiredText(requestId, "requestId");
        traceId = normalizeRequiredText(traceId, "traceId");
        timestamp = normalizeRequiredText(timestamp, "timestamp");
    }

    /**
     * @brief 规范化必填文本（Normalize Required Text）；
     *        Normalize required text and reject blank value.
     *
     * @param value 原始值（Raw value）。
     * @param argumentName 参数名（Argument name）。
     * @return 规范化值（Normalized value）。
     */
    private static String normalizeRequiredText(final String value, final String argumentName) {
        final String normalized = Objects.requireNonNull(value, argumentName + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(argumentName + " must not be blank");
        }
        return normalized;
    }
}

