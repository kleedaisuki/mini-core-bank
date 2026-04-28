package com.moesegfault.banking.presentation.web;

import java.util.Objects;
import java.util.Properties;

/**
 * @brief Web 配置对象（Web Configuration），统一 REST 运行参数与请求追踪头配置；
 *        Web configuration object that centralizes REST runtime settings and tracing-header names.
 */
public final class WebConfig {

    /**
     * @brief Web Host 配置键（Web Host Property Key）；
     *        Property key for web listening host.
     */
    public static final String KEY_WEB_HOST = "bank.web.host";

    /**
     * @brief Web 端口配置键（Web Port Property Key）；
     *        Property key for web listening port.
     */
    public static final String KEY_WEB_PORT = "bank.web.port";

    /**
     * @brief Web 工作线程配置键（Web Worker Threads Property Key）；
     *        Property key for worker thread count.
     */
    public static final String KEY_WEB_WORKER_THREADS = "bank.web.worker-threads";

    /**
     * @brief Request-Id 头配置键（Request-Id Header Property Key）；
     *        Property key for request-id header name.
     */
    public static final String KEY_WEB_REQUEST_ID_HEADER = "bank.web.request-id-header";

    /**
     * @brief Trace-Id 头配置键（Trace-Id Header Property Key）；
     *        Property key for trace-id header name.
     */
    public static final String KEY_WEB_TRACE_ID_HEADER = "bank.web.trace-id-header";

    /**
     * @brief 默认监听主机（Default Host）；
     *        Default web listening host.
     */
    public static final String DEFAULT_HOST = "127.0.0.1";

    /**
     * @brief 默认监听端口（Default Port）；
     *        Default web listening port.
     */
    public static final int DEFAULT_PORT = 8080;

    /**
     * @brief 默认 Request-Id 头名（Default Request-Id Header Name）；
     *        Default request-id header name.
     */
    public static final String DEFAULT_REQUEST_ID_HEADER = "X-Request-Id";

    /**
     * @brief 默认 Trace-Id 头名（Default Trace-Id Header Name）；
     *        Default trace-id header name.
     */
    public static final String DEFAULT_TRACE_ID_HEADER = "X-Trace-Id";

    /**
     * @brief 监听主机（Listening Host）；
     *        Listening host.
     */
    private final String host;

    /**
     * @brief 监听端口（Listening Port）；
     *        Listening port.
     */
    private final int port;

    /**
     * @brief 工作线程数（Worker Thread Count）；
     *        Worker thread count.
     */
    private final int workerThreads;

    /**
     * @brief Request-Id 头名（Request-Id Header Name）；
     *        Request-id header name.
     */
    private final String requestIdHeaderName;

    /**
     * @brief Trace-Id 头名（Trace-Id Header Name）；
     *        Trace-id header name.
     */
    private final String traceIdHeaderName;

    /**
     * @brief 构造 Web 配置（Construct Web Configuration）；
     *        Construct web configuration with default tracing header names.
     *
     * @param host 监听主机（Listening host）。
     * @param port 监听端口（Listening port）。
     * @param workerThreads 工作线程数（Worker thread count）。
     */
    public WebConfig(final String host, final int port, final int workerThreads) {
        this(host, port, workerThreads, DEFAULT_REQUEST_ID_HEADER, DEFAULT_TRACE_ID_HEADER);
    }

    /**
     * @brief 构造 Web 配置（Construct Web Configuration）；
     *        Construct web configuration with full fields.
     *
     * @param host 监听主机（Listening host）。
     * @param port 监听端口（Listening port）。
     * @param workerThreads 工作线程数（Worker thread count）。
     * @param requestIdHeaderName Request-Id 头名（Request-id header name）。
     * @param traceIdHeaderName Trace-Id 头名（Trace-id header name）。
     */
    public WebConfig(final String host,
                     final int port,
                     final int workerThreads,
                     final String requestIdHeaderName,
                     final String traceIdHeaderName) {
        this.host = normalizeHost(host);
        this.port = validatePort(port);
        this.workerThreads = validateWorkerThreads(workerThreads);
        this.requestIdHeaderName = normalizeHeaderName(requestIdHeaderName, "requestIdHeaderName");
        this.traceIdHeaderName = normalizeHeaderName(traceIdHeaderName, "traceIdHeaderName");
    }

    /**
     * @brief 构建默认配置（Build Default Configuration）；
     *        Build web configuration with default settings.
     *
     * @return 默认 Web 配置（Default web configuration）。
     */
    public static WebConfig defaults() {
        return new WebConfig(DEFAULT_HOST, DEFAULT_PORT, defaultWorkerThreads());
    }

    /**
     * @brief 从 Properties 读取配置（Load Configuration from Properties）；
     *        Load web configuration from properties with fallback defaults.
     *
     * @param properties 配置集合（Properties set）。
     * @return Web 配置对象（Web configuration object）。
     */
    public static WebConfig fromProperties(final Properties properties) {
        final Properties normalizedProperties = Objects.requireNonNull(properties, "properties must not be null");
        final String host = normalizedProperties.getProperty(KEY_WEB_HOST, DEFAULT_HOST);
        final int port = parseIntProperty(normalizedProperties, KEY_WEB_PORT, DEFAULT_PORT);
        final int workerThreads = parseIntProperty(normalizedProperties, KEY_WEB_WORKER_THREADS, defaultWorkerThreads());
        final String requestIdHeader = normalizedProperties.getProperty(KEY_WEB_REQUEST_ID_HEADER, DEFAULT_REQUEST_ID_HEADER);
        final String traceIdHeader = normalizedProperties.getProperty(KEY_WEB_TRACE_ID_HEADER, DEFAULT_TRACE_ID_HEADER);
        return new WebConfig(host, port, workerThreads, requestIdHeader, traceIdHeader);
    }

    /**
     * @brief 获取监听主机（Get Listening Host）；
     *        Get listening host.
     *
     * @return 监听主机（Listening host）。
     */
    public String host() {
        return host;
    }

    /**
     * @brief 获取监听端口（Get Listening Port）；
     *        Get listening port.
     *
     * @return 监听端口（Listening port）。
     */
    public int port() {
        return port;
    }

    /**
     * @brief 获取工作线程数（Get Worker Thread Count）；
     *        Get worker thread count.
     *
     * @return 工作线程数（Worker thread count）。
     */
    public int workerThreads() {
        return workerThreads;
    }

    /**
     * @brief 获取 Request-Id 头名（Get Request-Id Header Name）；
     *        Get request-id header name.
     *
     * @return Request-Id 头名（Request-id header name）。
     */
    public String requestIdHeaderName() {
        return requestIdHeaderName;
    }

    /**
     * @brief 获取 Trace-Id 头名（Get Trace-Id Header Name）；
     *        Get trace-id header name.
     *
     * @return Trace-Id 头名（Trace-id header name）。
     */
    public String traceIdHeaderName() {
        return traceIdHeaderName;
    }

    /**
     * @brief 获取默认线程数（Get Default Worker Threads）；
     *        Get default worker thread count.
     *
     * @return 默认线程数（Default thread count）。
     */
    private static int defaultWorkerThreads() {
        return Math.max(2, Runtime.getRuntime().availableProcessors());
    }

    /**
     * @brief 解析整数配置（Parse Integer Property）；
     *        Parse integer property with fallback.
     *
     * @param properties 配置集合（Properties set）。
     * @param key 配置键（Property key）。
     * @param defaultValue 默认值（Default value）。
     * @return 解析后的整数（Parsed integer value）。
     */
    private static int parseIntProperty(final Properties properties, final String key, final int defaultValue) {
        final String rawValue = properties.getProperty(key);
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(rawValue.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer property: " + key + " = " + rawValue, exception);
        }
    }

    /**
     * @brief 规范化 Host（Normalize Host）；
     *        Normalize host text and reject blank value.
     *
     * @param host 原始 Host（Raw host）。
     * @return 规范化 Host（Normalized host）。
     */
    private static String normalizeHost(final String host) {
        final String normalized = Objects.requireNonNull(host, "host must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("host must not be blank");
        }
        return normalized;
    }

    /**
     * @brief 校验端口范围（Validate Port Range）；
     *        Validate port is in legal range.
     *
     * @param port 端口值（Port value）。
     * @return 合法端口（Validated port）。
     */
    private static int validatePort(final int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("port must be between 1 and 65535");
        }
        return port;
    }

    /**
     * @brief 校验线程数（Validate Worker Thread Count）；
     *        Validate worker-thread count is positive.
     *
     * @param workerThreads 线程数（Worker thread count）。
     * @return 合法线程数（Validated worker-thread count）。
     */
    private static int validateWorkerThreads(final int workerThreads) {
        if (workerThreads <= 0) {
            throw new IllegalArgumentException("workerThreads must be greater than 0");
        }
        return workerThreads;
    }

    /**
     * @brief 规范化 Header 名（Normalize Header Name）；
     *        Normalize header-name text and reject blank value.
     *
     * @param headerName Header 名（Header name）。
     * @param argumentName 参数名（Argument name）。
     * @return 规范化 Header 名（Normalized header name）。
     */
    private static String normalizeHeaderName(final String headerName, final String argumentName) {
        final String normalized = Objects.requireNonNull(headerName, argumentName + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(argumentName + " must not be blank");
        }
        return normalized;
    }
}

