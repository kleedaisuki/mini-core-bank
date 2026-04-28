package com.moesegfault.banking.presentation.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.domain.shared.DomainException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * @brief Web 应用测试（Web Application Test），验证统一异常映射与追踪头注入行为；
 *        Web application tests verifying unified exception mapping and tracing-header injection.
 */
class WebApplicationTest {

    /**
     * @brief 验证路由异常会被统一映射为 JSON 错误响应；
     *        Verify route exception is converted into unified JSON error response.
     *
     * @throws Exception 请求处理异常（Request handling exception）。
     */
    @Test
    void shouldMapRouteExceptionToJsonErrorResponse() throws Exception {
        final DispatchRuntime runtime = new DispatchRuntime();
        final WebConfig config = new WebConfig("127.0.0.1", 8080, 2);
        final WebApplication application = new WebApplication(
                runtime,
                config,
                new WebExceptionMapper(),
                routeRuntime -> routeRuntime.addRoute("GET", "/boom", request -> {
                    throw new DomainException("账户已冻结");
                }));
        application.start();

        final FakeRequest request = new FakeRequest(
                "GET",
                "/boom",
                Map.of("X-Request-Id", "req-42", "X-Trace-Id", "trace-88"),
                Map.of(),
                Map.of(),
                "");
        final WebResponse response = runtime.dispatch("GET", "/boom", request);

        assertEquals(422, response.statusCode());
        assertTrue(new String(response.body(), StandardCharsets.UTF_8).contains("\"errorCode\":\"DOMAIN_RULE_VIOLATION\""));
        assertEquals("req-42", response.headers().get("X-Request-Id").get(0));
        assertEquals("trace-88", response.headers().get("X-Trace-Id").get(0));
    }

    /**
     * @brief 验证成功响应也会注入追踪头；
     *        Verify successful response also includes tracing headers.
     *
     * @throws Exception 请求处理异常（Request handling exception）。
     */
    @Test
    void shouldAppendTraceHeadersForSuccessfulResponse() throws Exception {
        final DispatchRuntime runtime = new DispatchRuntime();
        final WebConfig config = new WebConfig("127.0.0.1", 8080, 2);
        final WebApplication application = new WebApplication(
                runtime,
                config,
                new WebExceptionMapper(),
                routeRuntime -> routeRuntime.addRoute("GET", "/health", request -> WebResponse.json(200, "{\"status\":\"ok\"}")));
        application.start();

        final FakeRequest request = new FakeRequest(
                "GET",
                "/health",
                Map.of("X-Request-Id", "req-100", "X-Trace-Id", "trace-100"),
                Map.of(),
                Map.of(),
                "");
        final WebResponse response = runtime.dispatch("GET", "/health", request);

        assertEquals(200, response.statusCode());
        assertEquals("req-100", response.headers().get("X-Request-Id").get(0));
        assertEquals("trace-100", response.headers().get("X-Trace-Id").get(0));
    }

    /**
     * @brief 可分发测试 Runtime（Dispatch-capable Test Runtime）；
     *        Dispatch-capable runtime used by unit tests.
     */
    private static final class DispatchRuntime implements WebRuntime {

        /**
         * @brief 路由映射（Route Mapping）；
         *        Mapping from route key to handler.
         */
        private final Map<String, WebRouteHandler> routeMapping = new HashMap<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void addRoute(final String method, final String pathPattern, final WebRouteHandler handler) {
            routeMapping.put(routeKey(method, pathPattern), handler);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void start() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void stop() {
        }

        /**
         * @brief 执行路由分发（Dispatch Route）；
         *        Dispatch a request to one registered handler.
         *
         * @param method HTTP 方法（HTTP method）。
         * @param path 路径（Path）。
         * @param request Web 请求（Web request）。
         * @return Web 响应（Web response）。
         * @throws Exception 处理异常（Handling exception）。
         */
        WebResponse dispatch(final String method, final String path, final WebRequest request) throws Exception {
            final WebRouteHandler handler = routeMapping.get(routeKey(method, path));
            if (handler == null) {
                throw new IllegalStateException("Missing route: " + method + " " + path);
            }
            return handler.handle(request);
        }

        /**
         * @brief 构建路由键（Build Route Key）；
         *        Build canonical route key by method and path.
         *
         * @param method HTTP 方法（HTTP method）。
         * @param path 路径（Path）。
         * @return 路由键（Route key）。
         */
        private String routeKey(final String method, final String path) {
            return method + " " + path;
        }
    }

    /**
     * @brief 假请求对象（Fake Web Request）；
     *        Fake request object for unit tests.
     */
    private static final class FakeRequest implements WebRequest {

        /**
         * @brief HTTP 方法（HTTP Method）；
         *        Request HTTP method.
         */
        private final String method;

        /**
         * @brief 请求路径（Request Path）；
         *        Request path.
         */
        private final String path;

        /**
         * @brief 请求头映射（Header Mapping）；
         *        Request-header map.
         */
        private final Map<String, String> headers;

        /**
         * @brief 查询参数映射（Query Parameter Mapping）；
         *        Query-parameter map.
         */
        private final Map<String, String> queryParams;

        /**
         * @brief 路径参数映射（Path Parameter Mapping）；
         *        Path-parameter map.
         */
        private final Map<String, String> pathParams;

        /**
         * @brief 请求体文本（Body Text）；
         *        Request body text.
         */
        private final String bodyText;

        /**
         * @brief 构造假请求（Construct Fake Request）；
         *        Construct fake request with full fields.
         *
         * @param method HTTP 方法（HTTP method）。
         * @param path 请求路径（Request path）。
         * @param headers 请求头映射（Header map）。
         * @param queryParams 查询参数映射（Query map）。
         * @param pathParams 路径参数映射（Path map）。
         * @param bodyText 请求体文本（Body text）。
         */
        private FakeRequest(final String method,
                            final String path,
                            final Map<String, String> headers,
                            final Map<String, String> queryParams,
                            final Map<String, String> pathParams,
                            final String bodyText) {
            this.method = method;
            this.path = path;
            this.headers = Map.copyOf(headers);
            this.queryParams = Map.copyOf(queryParams);
            this.pathParams = Map.copyOf(pathParams);
            this.bodyText = bodyText;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String method() {
            return method;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String path() {
            return path;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<String> pathParam(final String name) {
            return Optional.ofNullable(pathParams.get(name));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<String> queryParam(final String name) {
            return Optional.ofNullable(queryParams.get(name));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<String> header(final String name) {
            return Optional.ofNullable(headers.get(name));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<String> headers(final String name) {
            return header(name).stream().toList();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public byte[] bodyBytes() {
            return bodyText.getBytes(StandardCharsets.UTF_8);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String bodyText() {
            return bodyText;
        }
    }
}

