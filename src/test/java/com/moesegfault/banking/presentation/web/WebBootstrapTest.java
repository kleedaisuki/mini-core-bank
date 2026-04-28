package com.moesegfault.banking.presentation.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * @brief Web 装配器测试（Web Bootstrap Test），验证装配链路、启动行为和路由注册幂等性；
 *        Web bootstrap tests verifying bootstrap chain, launch behavior, and route-registration idempotency.
 */
class WebBootstrapTest {

    /**
     * @brief 验证 BankingWeb.launch 会启动应用并注册路由；
     *        Verify BankingWeb.launch starts application and registers routes.
     */
    @Test
    void shouldLaunchApplicationAndRegisterRoutes() {
        final RecordingRuntime runtime = new RecordingRuntime();
        final AtomicInteger registerCounter = new AtomicInteger();
        final RouteRegistrar registrar = routeRuntime -> {
            registerCounter.incrementAndGet();
            routeRuntime.addRoute("GET", "/health", request -> WebResponse.text(200, "ok"));
        };

        final WebBootstrap bootstrap = new WebBootstrap(
                config -> runtime,
                WebConfig.defaults(),
                new WebExceptionMapper(),
                registrar);
        final BankingWeb bankingWeb = new BankingWeb(bootstrap);

        final WebApplication application = bankingWeb.launch();

        assertNotNull(application);
        assertEquals(1, runtime.startCount());
        assertEquals(1, registerCounter.get());
        assertEquals(1, runtime.routes().size());
    }

    /**
     * @brief 验证重复启动不会重复注册路由；
     *        Verify repeated start does not duplicate route registration.
     */
    @Test
    void shouldRegisterRoutesOnlyOnceAcrossRepeatedStart() {
        final RecordingRuntime runtime = new RecordingRuntime();
        final AtomicInteger registerCounter = new AtomicInteger();
        final RouteRegistrar registrar = routeRuntime -> {
            registerCounter.incrementAndGet();
            routeRuntime.addRoute("GET", "/v1/ping", request -> WebResponse.text(200, "pong"));
        };

        final WebApplication application = new WebApplication(
                runtime,
                WebConfig.defaults(),
                new WebExceptionMapper(),
                registrar);

        application.start();
        application.stop();
        application.start();

        assertEquals(1, registerCounter.get());
        assertEquals(1, runtime.routes().size());
        assertEquals(2, runtime.startCount());
    }

    /**
     * @brief 验证 Runtime 工厂返回 null 时会失败；
     *        Verify bootstrap fails when runtime factory returns null.
     */
    @Test
    void shouldRejectNullRuntimeFromFactory() {
        final WebBootstrap bootstrap = new WebBootstrap(
                config -> null,
                WebConfig.defaults(),
                new WebExceptionMapper());

        assertThrows(NullPointerException.class, bootstrap::bootstrap);
    }

    /**
     * @brief 记录型 Runtime（Recording Runtime）；
     *        Recording runtime for bootstrap tests.
     */
    private static final class RecordingRuntime implements WebRuntime {

        /**
         * @brief 已注册路由（Registered Routes）；
         *        Registered route list.
         */
        private final List<RouteEntry> routes = new ArrayList<>();

        /**
         * @brief 启动计数（Start Count）；
         *        Start invocation count.
         */
        private final AtomicInteger startCounter = new AtomicInteger();

        /**
         * {@inheritDoc}
         */
        @Override
        public void addRoute(final String method, final String pathPattern, final WebRouteHandler handler) {
            routes.add(new RouteEntry(method, pathPattern, handler));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void start() {
            startCounter.incrementAndGet();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void stop() {
        }

        /**
         * @brief 获取路由快照（Get Route Snapshot）；
         *        Get immutable route snapshot.
         *
         * @return 路由列表（Route list）。
         */
        List<RouteEntry> routes() {
            return List.copyOf(routes);
        }

        /**
         * @brief 获取启动次数（Get Start Count）；
         *        Get runtime start count.
         *
         * @return 启动次数（Start count）。
         */
        int startCount() {
            return startCounter.get();
        }
    }

    /**
     * @brief 路由条目（Route Entry）；
     *        Route entry record.
     *
     * @param method HTTP 方法（HTTP method）。
     * @param path 路径模式（Path pattern）。
     * @param handler 路由处理器（Route handler）。
     */
    private record RouteEntry(String method, String path, WebRouteHandler handler) {
    }

    /**
     * @brief 假请求对象（Fake Request）；
     *        Fake web request for invoking handlers in bootstrap tests.
     */
    private static final class FakeWebRequest implements WebRequest {

        /**
         * @brief 请求头集合（Request Headers）；
         *        Request header map.
         */
        private final Map<String, String> headers;

        /**
         * @brief 构造假请求（Construct Fake Request）；
         *        Construct fake web request.
         *
         * @param headers 请求头集合（Request headers）。
         */
        private FakeWebRequest(final Map<String, String> headers) {
            this.headers = headers;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String method() {
            return "GET";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String path() {
            return "/health";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<String> pathParam(final String name) {
            return Optional.empty();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<String> queryParam(final String name) {
            return Optional.empty();
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
            return new byte[0];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String bodyText() {
            return "";
        }
    }
}

