package com.moesegfault.banking.infrastructure.web.jdk;

import com.moesegfault.banking.presentation.web.WebRequest;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief JDK Web 请求适配器（JDK Web Request Adapter），将 HttpExchange 适配为 WebRequest；
 *        JDK Web request adapter that maps HttpExchange into WebRequest abstraction.
 */
public final class JdkWebRequest implements WebRequest {

    /**
     * @brief JDK HttpExchange（JDK HttpExchange）；
     *        Underlying JDK HttpExchange.
     */
    private final HttpExchange exchange;

    /**
     * @brief 路径参数映射（Path Parameters）；
     *        Extracted path parameter map.
     */
    private final Map<String, String> pathParams;

    /**
     * @brief 查询参数映射（Query Parameters）；
     *        Parsed query parameter map.
     */
    private final Map<String, String> queryParams;

    /**
     * @brief 缓存请求体（Cached Body）；
     *        Cached request body bytes.
     */
    private volatile byte[] cachedBody;

    /**
     * @brief 构造请求适配器（Construct Request Adapter）；
     *        Construct request adapter with path parameters.
     *
     * @param exchange HttpExchange（HttpExchange）。
     * @param pathParams 路径参数（Path parameters）。
     */
    public JdkWebRequest(final HttpExchange exchange, final Map<String, String> pathParams) {
        this.exchange = Objects.requireNonNull(exchange, "exchange must not be null");
        this.pathParams = Map.copyOf(Objects.requireNonNull(pathParams, "pathParams must not be null"));
        this.queryParams = parseQueryParams(exchange.getRequestURI().getRawQuery());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String method() {
        return exchange.getRequestMethod();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String path() {
        return exchange.getRequestURI().getPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> pathParam(final String name) {
        final String resolvedName = Objects.requireNonNull(name, "name must not be null");
        return Optional.ofNullable(pathParams.get(resolvedName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> queryParam(final String name) {
        final String resolvedName = Objects.requireNonNull(name, "name must not be null");
        return Optional.ofNullable(queryParams.get(resolvedName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> header(final String name) {
        final String resolvedName = Objects.requireNonNull(name, "name must not be null");
        return Optional.ofNullable(exchange.getRequestHeaders().getFirst(resolvedName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> headers(final String name) {
        final String resolvedName = Objects.requireNonNull(name, "name must not be null");
        final List<String> values = exchange.getRequestHeaders().get(resolvedName);
        if (values == null) {
            return List.of();
        }
        return List.copyOf(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] bodyBytes() {
        if (cachedBody != null) {
            return cachedBody.clone();
        }

        synchronized (this) {
            if (cachedBody == null) {
                try {
                    cachedBody = exchange.getRequestBody().readAllBytes();
                } catch (IOException exception) {
                    throw new JdkHttpRuntimeException("Failed to read request body.", exception);
                }
            }
            return cachedBody.clone();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String bodyText() {
        return new String(bodyBytes(), StandardCharsets.UTF_8);
    }

    /**
     * @brief 解析查询参数（Parse Query Parameters）；
     *        Parse URI raw query string into key-value map.
     *
     * @param rawQuery 原始查询串（Raw query）。
     * @return 查询参数映射（Query map）。
     */
    private static Map<String, String> parseQueryParams(final String rawQuery) {
        if (rawQuery == null || rawQuery.isBlank()) {
            return Map.of();
        }

        final Map<String, String> parsed = new LinkedHashMap<>();
        for (String pair : rawQuery.split("&")) {
            if (pair.isBlank()) {
                continue;
            }

            final String[] keyValue = pair.split("=", 2);
            final String key = decodeQueryComponent(keyValue[0]);
            final String value = keyValue.length == 2 ? decodeQueryComponent(keyValue[1]) : "";
            parsed.putIfAbsent(key, value);
        }
        return Map.copyOf(parsed);
    }

    /**
     * @brief 解码查询参数组件（Decode Query Component）；
     *        Decode one query component with UTF-8.
     *
     * @param rawValue 原始值（Raw value）。
     * @return 解码值（Decoded value）。
     */
    private static String decodeQueryComponent(final String rawValue) {
        return URLDecoder.decode(rawValue, StandardCharsets.UTF_8);
    }
}
