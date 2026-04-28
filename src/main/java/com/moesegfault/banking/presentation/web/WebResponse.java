package com.moesegfault.banking.presentation.web;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @brief Web 响应对象（Web Response Object），封装状态码、类型、响应体和响应头；
 *        Web response object encapsulating status code, content type, body, and headers.
 */
public final class WebResponse {

    /**
     * @brief HTTP 状态码（HTTP Status Code）；
     *        HTTP response status code.
     */
    private final int statusCode;

    /**
     * @brief Content-Type（Content Type）；
     *        Response content type.
     */
    private final String contentType;

    /**
     * @brief 响应体字节（Response Body Bytes）；
     *        Response body bytes.
     */
    private final byte[] body;

    /**
     * @brief 响应头集合（Response Headers）；
     *        Response headers map.
     */
    private final Map<String, List<String>> headers;

    /**
     * @brief 构造响应对象（Construct Web Response）；
     *        Construct web response with full fields.
     *
     * @param statusCode 状态码（Status code）。
     * @param contentType 内容类型（Content type）。
     * @param body 响应体字节（Body bytes）。
     * @param headers 响应头（Headers）。
     */
    private WebResponse(final int statusCode,
                        final String contentType,
                        final byte[] body,
                        final Map<String, List<String>> headers) {
        this.statusCode = statusCode;
        this.contentType = Objects.requireNonNull(contentType, "contentType must not be null");
        this.body = Objects.requireNonNull(body, "body must not be null").clone();
        this.headers = copyHeaders(Objects.requireNonNull(headers, "headers must not be null"));
    }

    /**
     * @brief 获取状态码（Get Status Code）；
     *        Get HTTP status code.
     *
     * @return 状态码（Status code）。
     */
    public int statusCode() {
        return statusCode;
    }

    /**
     * @brief 获取 Content-Type（Get Content Type）；
     *        Get response content type.
     *
     * @return 内容类型（Content type）。
     */
    public String contentType() {
        return contentType;
    }

    /**
     * @brief 获取响应体字节（Get Body Bytes）；
     *        Get response body bytes.
     *
     * @return 响应体字节（Body bytes）。
     */
    public byte[] body() {
        return body.clone();
    }

    /**
     * @brief 获取响应头（Get Headers）；
     *        Get immutable response headers.
     *
     * @return 响应头映射（Headers map）。
     */
    public Map<String, List<String>> headers() {
        return headers;
    }

    /**
     * @brief 添加响应头并返回新对象（Add Header and Return New Response）；
     *        Add one header and return a new response instance.
     *
     * @param name Header 名称（Header name）。
     * @param value Header 值（Header value）。
     * @return 新响应对象（New response instance）。
     */
    public WebResponse withHeader(final String name, final String value) {
        final String resolvedName = Objects.requireNonNull(name, "name must not be null");
        final String resolvedValue = Objects.requireNonNull(value, "value must not be null");

        final Map<String, List<String>> mutableHeaders = new LinkedHashMap<>();
        headers.forEach((headerName, headerValues) -> mutableHeaders.put(headerName, new ArrayList<>(headerValues)));
        mutableHeaders.computeIfAbsent(resolvedName, ignored -> new ArrayList<>()).add(resolvedValue);

        return new WebResponse(statusCode, contentType, body, mutableHeaders);
    }

    /**
     * @brief 创建完整响应（Create Full Response）；
     *        Create full response with status, content type and body bytes.
     *
     * @param statusCode 状态码（Status code）。
     * @param contentType 内容类型（Content type）。
     * @param body 响应体字节（Body bytes）。
     * @return Web 响应（Web response）。
     */
    public static WebResponse of(final int statusCode, final String contentType, final byte[] body) {
        validateStatusCode(statusCode);
        return new WebResponse(statusCode, contentType, body, Map.of());
    }

    /**
     * @brief 创建纯文本响应（Create Text Response）；
     *        Create UTF-8 plain text response.
     *
     * @param statusCode 状态码（Status code）。
     * @param bodyText 响应文本（Response text）。
     * @return Web 响应（Web response）。
     */
    public static WebResponse text(final int statusCode, final String bodyText) {
        validateStatusCode(statusCode);
        final String resolvedBody = Objects.requireNonNull(bodyText, "bodyText must not be null");
        return of(statusCode, "text/plain; charset=UTF-8", resolvedBody.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @brief 创建 JSON 响应（Create JSON Response）；
     *        Create UTF-8 JSON response.
     *
     * @param statusCode 状态码（Status code）。
     * @param jsonText JSON 文本（JSON text）。
     * @return Web 响应（Web response）。
     */
    public static WebResponse json(final int statusCode, final String jsonText) {
        validateStatusCode(statusCode);
        final String resolvedJson = Objects.requireNonNull(jsonText, "jsonText must not be null");
        return of(statusCode, "application/json; charset=UTF-8", resolvedJson.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @brief 创建空响应（Create Empty Response）；
     *        Create empty-body response.
     *
     * @param statusCode 状态码（Status code）。
     * @return Web 响应（Web response）。
     */
    public static WebResponse empty(final int statusCode) {
        validateStatusCode(statusCode);
        return of(statusCode, "text/plain; charset=UTF-8", new byte[0]);
    }

    /**
     * @brief 校验状态码（Validate Status Code）；
     *        Validate HTTP status code range.
     *
     * @param statusCode 状态码（Status code）。
     */
    private static void validateStatusCode(final int statusCode) {
        if (statusCode < 100 || statusCode > 599) {
            throw new IllegalArgumentException("statusCode must be between 100 and 599");
        }
    }

    /**
     * @brief 复制并冻结响应头（Copy and Freeze Headers）；
     *        Copy headers into immutable map/list structure.
     *
     * @param source 源响应头（Source headers）。
     * @return 不可变响应头（Immutable headers）。
     */
    private static Map<String, List<String>> copyHeaders(final Map<String, List<String>> source) {
        final Map<String, List<String>> copied = new LinkedHashMap<>();
        source.forEach((name, values) -> copied.put(
                Objects.requireNonNull(name, "header name must not be null"),
                List.copyOf(Objects.requireNonNull(values, "header values must not be null"))
        ));
        return Collections.unmodifiableMap(copied);
    }
}
