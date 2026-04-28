package com.moesegfault.banking.infrastructure.web.jdk;

import com.moesegfault.banking.presentation.web.WebRequest;
import com.moesegfault.banking.presentation.web.WebResponse;
import com.moesegfault.banking.presentation.web.WebRouteHandler;
import com.moesegfault.banking.presentation.web.WebRuntime;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @brief JDK HttpServer Web 运行时（JDK HttpServer Web Runtime），负责路由分发与服务生命周期；
 *        JDK HttpServer-based web runtime responsible for route dispatching and service lifecycle.
 */
public final class JdkHttpWebRuntime implements WebRuntime {

    /**
     * @brief HTTP 服务器（HTTP Server）；
     *        Underlying JDK HttpServer.
     */
    private final HttpServer httpServer;

    /**
     * @brief 响应写入器（Response Writer）；
     *        Response writer for HttpExchange.
     */
    private final JdkWebResponseWriter responseWriter;

    /**
     * @brief 路由列表（Registered Routes）；
     *        Registered route definitions.
     */
    private final List<Route> routes;

    /**
     * @brief 运行状态（Started Flag）；
     *        Runtime started flag.
     */
    private final AtomicBoolean started;

    /**
     * @brief 自有线程池（Owned Executor）；
     *        Owned executor that should be shutdown on stop.
     */
    private final ExecutorService ownedExecutor;

    /**
     * @brief 构造 JDK Web 运行时（Construct JDK Web Runtime）；
     *        Construct runtime by host/port/thread settings.
     *
     * @param host 监听主机（Host）。
     * @param port 监听端口（Port）。
     * @param workerThreads 工作线程数（Worker threads）。
     */
    public JdkHttpWebRuntime(final String host, final int port, final int workerThreads) {
        try {
            final HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
            final ExecutorService executorService = Executors.newFixedThreadPool(Math.max(1, workerThreads));
            server.setExecutor(executorService);
            this.httpServer = server;
            this.responseWriter = new JdkWebResponseWriter();
            this.routes = new CopyOnWriteArrayList<>();
            this.started = new AtomicBoolean(false);
            this.ownedExecutor = executorService;
            this.httpServer.createContext("/", this::handleExchange);
        } catch (IOException exception) {
            throw new JdkHttpRuntimeException("Failed to initialize JDK HttpServer runtime.", exception);
        }
    }

    /**
     * @brief 构造 JDK Web 运行时（Construct JDK Web Runtime）；
     *        Construct runtime with default worker size.
     *
     * @param host 监听主机（Host）。
     * @param port 监听端口（Port）。
     */
    public JdkHttpWebRuntime(final String host, final int port) {
        this(host, port, Math.max(2, Runtime.getRuntime().availableProcessors()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRoute(final String method, final String pathPattern, final WebRouteHandler handler) {
        final String resolvedMethod = Objects.requireNonNull(method, "method must not be null").trim();
        if (resolvedMethod.isEmpty()) {
            throw new IllegalArgumentException("method must not be blank");
        }

        final JdkPathPattern resolvedPathPattern = new JdkPathPattern(pathPattern);
        final WebRouteHandler resolvedHandler = Objects.requireNonNull(handler, "handler must not be null");

        routes.add(new Route(resolvedMethod.toUpperCase(Locale.ROOT), resolvedPathPattern, resolvedHandler));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        if (!started.compareAndSet(false, true)) {
            return;
        }
        httpServer.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (!started.compareAndSet(true, false)) {
            return;
        }
        httpServer.stop(0);
        if (ownedExecutor != null) {
            ownedExecutor.shutdownNow();
        }
    }

    /**
     * @brief 获取实际端口（Get Bound Port）；
     *        Get actual bound listening port.
     *
     * @return 实际端口（Actual port）。
     */
    public int actualPort() {
        return httpServer.getAddress().getPort();
    }

    /**
     * @brief 处理单个交换对象（Handle One HttpExchange）；
     *        Handle one incoming HttpExchange.
     *
     * @param exchange HttpExchange（HttpExchange）。
     */
    private void handleExchange(final HttpExchange exchange) {
        try {
            final String requestMethod = exchange.getRequestMethod().toUpperCase(Locale.ROOT);
            final String requestPath = exchange.getRequestURI().getPath();

            for (Route route : routes) {
                if (!route.method.equals(requestMethod)) {
                    continue;
                }

                final Optional<Map<String, String>> matchedParams = route.pathPattern.match(requestPath);
                if (matchedParams.isEmpty()) {
                    continue;
                }

                handleMatchedRoute(exchange, route.handler, matchedParams.get());
                return;
            }

            responseWriter.write(exchange, WebResponse.text(404, "Not Found"));
        } catch (Exception exception) {
            writeInternalError(exchange, exception);
        } finally {
            exchange.close();
        }
    }

    /**
     * @brief 处理匹配路由（Handle Matched Route）；
     *        Handle one matched route and write response.
     *
     * @param exchange HttpExchange（HttpExchange）。
     * @param handler 路由处理器（Route handler）。
     * @param pathParams 路径参数（Path parameters）。
     * @throws Exception 处理异常（Handling exception）。
     */
    private void handleMatchedRoute(final HttpExchange exchange,
                                    final WebRouteHandler handler,
                                    final Map<String, String> pathParams) throws Exception {
        final WebRequest webRequest = new JdkWebRequest(exchange, pathParams);
        final WebResponse webResponse = handler.handle(webRequest);

        if (webResponse == null) {
            throw new JdkHttpRuntimeException("Route handler must not return null response.");
        }

        responseWriter.write(exchange, webResponse);
    }

    /**
     * @brief 写入 500 错误响应（Write 500 Error Response）；
     *        Write HTTP 500 response for unhandled exception.
     *
     * @param exchange HttpExchange（HttpExchange）。
     * @param rootCause 根因异常（Root cause）。
     */
    private void writeInternalError(final HttpExchange exchange, final Exception rootCause) {
        try {
            responseWriter.write(exchange, WebResponse.text(500, "Internal Server Error"));
        } catch (IOException ioException) {
            throw new JdkHttpRuntimeException("Failed to write internal error response.", ioException);
        }
    }

    /**
     * @brief 路由定义（Route Definition）；
     *        Immutable route definition.
     */
    private static final class Route {

        /**
         * @brief HTTP 方法（HTTP Method）；
         *        Route HTTP method.
         */
        private final String method;

        /**
         * @brief 路径模式（Path Pattern）；
         *        Route path pattern.
         */
        private final JdkPathPattern pathPattern;

        /**
         * @brief 处理器（Handler）；
         *        Route handler.
         */
        private final WebRouteHandler handler;

        /**
         * @brief 构造路由定义（Construct Route Definition）；
         *        Construct immutable route definition.
         *
         * @param method HTTP 方法（HTTP method）。
         * @param pathPattern 路径模式（Path pattern）。
         * @param handler 路由处理器（Route handler）。
         */
        private Route(final String method, final JdkPathPattern pathPattern, final WebRouteHandler handler) {
            this.method = method;
            this.pathPattern = pathPattern;
            this.handler = handler;
        }
    }
}
