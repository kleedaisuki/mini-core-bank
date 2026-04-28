package com.moesegfault.banking.presentation.web;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * @brief Web 请求上下文（Web Request Context），保存 requestId、traceId、主体标识与 locale；
 *        Web request context carrying requestId, traceId, principal identity, and locale.
 */
public final class WebContext {

    /**
     * @brief Accept-Language 头名（Accept-Language Header Name）；
     *        HTTP header name for locale negotiation.
     */
    public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";

    /**
     * @brief 主体标识头名（Principal Header Name）；
     *        HTTP header name for caller principal identifier.
     */
    public static final String HEADER_PRINCIPAL_ID = "X-Principal-Id";

    /**
     * @brief 请求 ID（Request Identifier）；
     *        Request identifier.
     */
    private final String requestId;

    /**
     * @brief 追踪 ID（Trace Identifier）；
     *        Trace identifier.
     */
    private final String traceId;

    /**
     * @brief 主体标识（Principal Identifier）；
     *        Optional caller principal identifier.
     */
    private final Optional<String> principalId;

    /**
     * @brief 区域设置（Locale）；
     *        Request locale.
     */
    private final Locale locale;

    /**
     * @brief 请求时间（Requested At Instant）；
     *        Instant at which request context was created.
     */
    private final Instant requestedAt;

    /**
     * @brief 构造请求上下文（Construct Request Context）；
     *        Construct request context with full fields.
     *
     * @param requestId 请求 ID（Request identifier）。
     * @param traceId 追踪 ID（Trace identifier）。
     * @param principalId 主体标识（Principal identifier）。
     * @param locale 区域设置（Locale）。
     * @param requestedAt 请求时间（Requested-at instant）。
     */
    public WebContext(final String requestId,
                      final String traceId,
                      final Optional<String> principalId,
                      final Locale locale,
                      final Instant requestedAt) {
        this.requestId = normalizeRequiredText(requestId, "requestId");
        this.traceId = normalizeRequiredText(traceId, "traceId");
        this.principalId = normalizeOptionalText(Objects.requireNonNull(principalId, "principalId must not be null"));
        this.locale = Objects.requireNonNull(locale, "locale must not be null");
        this.requestedAt = Objects.requireNonNull(requestedAt, "requestedAt must not be null");
    }

    /**
     * @brief 基于请求创建上下文（Create Context from Request）；
     *        Create request context from web request and web configuration.
     *
     * @param request Web 请求（Web request）。
     * @param webConfig Web 配置（Web configuration）。
     * @return Web 请求上下文（Web request context）。
     */
    public static WebContext fromRequest(final WebRequest request, final WebConfig webConfig) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final WebConfig normalizedConfig = Objects.requireNonNull(webConfig, "webConfig must not be null");

        final String requestId = normalizedRequest.header(normalizedConfig.requestIdHeaderName())
                .map(WebContext::normalizeOptionalInput)
                .filter(value -> !value.isEmpty())
                .orElseGet(() -> UUID.randomUUID().toString());
        final String traceId = normalizedRequest.header(normalizedConfig.traceIdHeaderName())
                .map(WebContext::normalizeOptionalInput)
                .filter(value -> !value.isEmpty())
                .orElse(requestId);
        final Optional<String> principalId = normalizedRequest.header(HEADER_PRINCIPAL_ID)
                .map(WebContext::normalizeOptionalInput)
                .filter(value -> !value.isEmpty());
        final Locale locale = resolveLocale(normalizedRequest);

        return new WebContext(requestId, traceId, principalId, locale, Instant.now());
    }

    /**
     * @brief 获取请求 ID（Get Request Identifier）；
     *        Get request identifier.
     *
     * @return 请求 ID（Request identifier）。
     */
    public String requestId() {
        return requestId;
    }

    /**
     * @brief 获取追踪 ID（Get Trace Identifier）；
     *        Get trace identifier.
     *
     * @return 追踪 ID（Trace identifier）。
     */
    public String traceId() {
        return traceId;
    }

    /**
     * @brief 获取主体标识（Get Principal Identifier）；
     *        Get optional principal identifier.
     *
     * @return 主体标识（Principal identifier）。
     */
    public Optional<String> principalId() {
        return principalId;
    }

    /**
     * @brief 获取区域设置（Get Locale）；
     *        Get request locale.
     *
     * @return 区域设置（Locale）。
     */
    public Locale locale() {
        return locale;
    }

    /**
     * @brief 获取请求时间（Get Requested-at Instant）；
     *        Get requested-at instant.
     *
     * @return 请求时间（Requested-at instant）。
     */
    public Instant requestedAt() {
        return requestedAt;
    }

    /**
     * @brief 解析 Locale（Resolve Locale）；
     *        Resolve locale from `Accept-Language` header.
     *
     * @param request Web 请求（Web request）。
     * @return 解析后的 Locale（Resolved locale）。
     */
    private static Locale resolveLocale(final WebRequest request) {
        final Optional<String> acceptLanguage = request.header(HEADER_ACCEPT_LANGUAGE)
                .map(WebContext::normalizeOptionalInput)
                .filter(value -> !value.isEmpty());
        if (acceptLanguage.isEmpty()) {
            return Locale.getDefault();
        }

        final String primaryLanguage = acceptLanguage.get().split(",", 2)[0].trim();
        if (primaryLanguage.isEmpty()) {
            return Locale.getDefault();
        }

        final String normalizedTag = primaryLanguage.split(";", 2)[0].trim();
        if (normalizedTag.isEmpty()) {
            return Locale.getDefault();
        }

        final Locale candidate = Locale.forLanguageTag(normalizedTag);
        if (candidate.getLanguage().isEmpty()) {
            return Locale.getDefault();
        }
        return candidate;
    }

    /**
     * @brief 规范化必填文本（Normalize Required Text）；
     *        Normalize required text value and reject blank.
     *
     * @param value 原始值（Raw value）。
     * @param argumentName 参数名（Argument name）。
     * @return 规范化文本（Normalized text）。
     */
    private static String normalizeRequiredText(final String value, final String argumentName) {
        final String normalized = Objects.requireNonNull(value, argumentName + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(argumentName + " must not be blank");
        }
        return normalized;
    }

    /**
     * @brief 规范化可选文本（Normalize Optional Text）；
     *        Normalize optional text and collapse blank to empty optional.
     *
     * @param value 可选文本（Optional text）。
     * @return 规范化后的可选文本（Normalized optional text）。
     */
    private static Optional<String> normalizeOptionalText(final Optional<String> value) {
        return value.map(WebContext::normalizeOptionalInput).filter(candidate -> !candidate.isEmpty());
    }

    /**
     * @brief 规范化输入文本（Normalize Input Text）；
     *        Normalize one input text by trimming null-safe.
     *
     * @param value 原始值（Raw value）。
     * @return 规范化文本（Normalized text）。
     */
    private static String normalizeOptionalInput(final String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
}

