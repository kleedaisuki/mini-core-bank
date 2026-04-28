package com.moesegfault.banking.presentation.web;

import com.moesegfault.banking.domain.shared.DomainException;
import com.moesegfault.banking.presentation.web.dto.ErrorResponseDto;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief Web 异常映射器（Web Exception Mapper），统一把异常转换为可追踪 JSON 错误响应；
 *        Web exception mapper that consistently converts exceptions into traceable JSON error responses.
 */
public final class WebExceptionMapper {

    /**
     * @brief 内部错误码（Internal Error Code）；
     *        Error code for generic internal failures.
     */
    public static final String ERROR_INTERNAL = "INTERNAL_SERVER_ERROR";

    /**
     * @brief 参数错误码（Invalid Argument Error Code）；
     *        Error code for invalid-argument requests.
     */
    public static final String ERROR_INVALID_ARGUMENT = "INVALID_ARGUMENT";

    /**
     * @brief 资源不存在错误码（Not Found Error Code）；
     *        Error code for resource-not-found requests.
     */
    public static final String ERROR_RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";

    /**
     * @brief 业务规则冲突错误码（Business Rule Violation Error Code）；
     *        Error code for business-rule violation.
     */
    public static final String ERROR_DOMAIN_RULE_VIOLATION = "DOMAIN_RULE_VIOLATION";

    /**
     * @brief 不支持操作错误码（Operation Not Supported Error Code）；
     *        Error code for unsupported operation.
     */
    public static final String ERROR_NOT_IMPLEMENTED = "NOT_IMPLEMENTED";

    /**
     * @brief JSON 编解码器（JSON Codec）；
     *        JSON codec for serializing error DTO.
     */
    private final WebJsonCodec webJsonCodec;

    /**
     * @brief 构造默认异常映射器（Construct Default Exception Mapper）；
     *        Construct exception mapper with default JSON codec.
     */
    public WebExceptionMapper() {
        this(new WebJsonCodec());
    }

    /**
     * @brief 构造异常映射器（Construct Exception Mapper）；
     *        Construct exception mapper with injected JSON codec.
     *
     * @param webJsonCodec JSON 编解码器（JSON codec）。
     */
    public WebExceptionMapper(final WebJsonCodec webJsonCodec) {
        this.webJsonCodec = Objects.requireNonNull(webJsonCodec, "webJsonCodec must not be null");
    }

    /**
     * @brief 映射异常为 JSON 响应（Map Exception to JSON Response）；
     *        Map one exception to JSON web response.
     *
     * @param throwable 异常对象（Exception object）。
     * @param context Web 请求上下文（Web request context）。
     * @return Web 响应（Web response）。
     */
    public WebResponse toResponse(final Throwable throwable, final WebContext context) {
        final Throwable normalizedThrowable = Objects.requireNonNull(throwable, "throwable must not be null");
        final WebContext normalizedContext = Objects.requireNonNull(context, "context must not be null");

        final ErrorDescriptor descriptor = mapError(normalizedThrowable);
        final String message = normalizeMessage(normalizedThrowable.getMessage(), descriptor.fallbackMessage);

        final ErrorResponseDto errorResponse = new ErrorResponseDto(
                descriptor.status,
                descriptor.errorCode,
                message,
                normalizedContext.requestId(),
                normalizedContext.traceId(),
                Instant.now().toString());

        return webJsonCodec.toJsonResponse(descriptor.status, errorResponse)
                .withHeader("X-Request-Id", normalizedContext.requestId())
                .withHeader("X-Trace-Id", normalizedContext.traceId());
    }

    /**
     * @brief 映射错误描述（Map Error Descriptor）；
     *        Map throwable to HTTP status, error code and fallback message.
     *
     * @param throwable 异常对象（Exception object）。
     * @return 错误描述对象（Error descriptor）。
     */
    private ErrorDescriptor mapError(final Throwable throwable) {
        if (throwable instanceof IllegalArgumentException) {
            return new ErrorDescriptor(400, ERROR_INVALID_ARGUMENT, "请求参数不合法");
        }
        if (throwable instanceof DomainException) {
            return new ErrorDescriptor(422, ERROR_DOMAIN_RULE_VIOLATION, "业务规则校验失败");
        }
        if (isNotFoundException(throwable)) {
            return new ErrorDescriptor(404, ERROR_RESOURCE_NOT_FOUND, "请求资源不存在");
        }
        if (throwable instanceof UnsupportedOperationException) {
            return new ErrorDescriptor(501, ERROR_NOT_IMPLEMENTED, "请求操作暂不支持");
        }
        return new ErrorDescriptor(500, ERROR_INTERNAL, "系统暂时不可用，请稍后重试");
    }

    /**
     * @brief 判断是否为 NotFound 异常（Determine Whether Exception is NotFound）；
     *        Determine whether exception follows `*NotFoundException` convention.
     *
     * @param throwable 异常对象（Exception object）。
     * @return 是否匹配 NotFound（Whether matched）。
     */
    private boolean isNotFoundException(final Throwable throwable) {
        return throwable.getClass().getSimpleName().endsWith("NotFoundException");
    }

    /**
     * @brief 规范化错误消息（Normalize Error Message）；
     *        Normalize error message and fallback to default text.
     *
     * @param rawMessage 原始消息（Raw message）。
     * @param fallbackMessage 回退消息（Fallback message）。
     * @return 规范化消息（Normalized message）。
     */
    private String normalizeMessage(final String rawMessage, final String fallbackMessage) {
        if (rawMessage == null || rawMessage.trim().isEmpty()) {
            return fallbackMessage;
        }
        return rawMessage.trim();
    }

    /**
     * @brief 错误描述记录（Error Descriptor Record）；
     *        Error descriptor record carrying status, code, and fallback message.
     *
     * @param status HTTP 状态码（HTTP status code）。
     * @param errorCode 错误码（Error code）。
     * @param fallbackMessage 回退消息（Fallback message）。
     */
    private record ErrorDescriptor(int status, String errorCode, String fallbackMessage) {
    }
}

