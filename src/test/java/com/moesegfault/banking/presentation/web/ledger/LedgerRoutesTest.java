package com.moesegfault.banking.presentation.web.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moesegfault.banking.presentation.web.WebRequest;
import com.moesegfault.banking.presentation.web.WebResponse;
import com.moesegfault.banking.presentation.web.WebRouteHandler;
import com.moesegfault.banking.presentation.web.WebRuntime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @brief Ledger 路由注册测试（Ledger Routes Test），验证路由注册与处理器委托行为；
 *        Ledger-route tests verifying route registration and handler delegation.
 */
class LedgerRoutesTest {

    /**
     * @brief 验证会注册两个 GET 路由；
     *        Verify two GET ledger routes are registered.
     */
    @Test
    void shouldRegisterTwoLedgerGetRoutes() {
        final LedgerController controller = Mockito.mock(LedgerController.class);
        final LedgerRoutes routes = new LedgerRoutes(controller);
        final RecordingRuntime runtime = new RecordingRuntime();

        routes.registerRoutes(runtime);

        assertEquals(2, runtime.routes().size());
        assertTrue(runtime.contains("GET", LedgerRoutes.ROUTE_FIND_BALANCE));
        assertTrue(runtime.contains("GET", LedgerRoutes.ROUTE_LIST_ENTRIES));
    }

    /**
     * @brief 验证路由处理器委托到控制器；
     *        Verify registered route handlers delegate to controller methods.
     *
     * @throws Exception 分发异常（Dispatch exception）。
     */
    @Test
    void shouldDelegateToControllerHandlers() throws Exception {
        final LedgerController controller = Mockito.mock(LedgerController.class);
        when(controller.findBalance(Mockito.any())).thenReturn(WebResponse.text(200, "balance"));
        when(controller.listLedgerEntries(Mockito.any())).thenReturn(WebResponse.text(200, "entries"));

        final LedgerRoutes routes = new LedgerRoutes(controller);
        final RecordingRuntime runtime = new RecordingRuntime();
        routes.registerRoutes(runtime);

        runtime.dispatch("GET", LedgerRoutes.ROUTE_FIND_BALANCE, new FakeRequest());
        runtime.dispatch("GET", LedgerRoutes.ROUTE_LIST_ENTRIES, new FakeRequest());

        verify(controller).findBalance(Mockito.any());
        verify(controller).listLedgerEntries(Mockito.any());
    }

    /**
     * @brief 记录型运行时（Recording Runtime）；
     *        Recording runtime for route-registration assertions.
     */
    private static final class RecordingRuntime implements WebRuntime {

        /**
         * @brief 路由记录列表（Route Record List）；
         *        Route record list.
         */
        private final List<RouteRecord> routes = new ArrayList<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void addRoute(final String method, final String pathPattern, final WebRouteHandler handler) {
            routes.add(new RouteRecord(method, pathPattern, handler));
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
         * @brief 获取路由记录快照（Get Route Record Snapshot）；
         *        Get immutable route record snapshot.
         *
         * @return 路由记录列表（Route record list）。
         */
        private List<RouteRecord> routes() {
            return List.copyOf(routes);
        }

        /**
         * @brief 判断是否包含指定路由（Check Route Presence）；
         *        Check whether one route exists in records.
         *
         * @param method HTTP 方法（HTTP method）。
         * @param pathPattern 路径模式（Path pattern）。
         * @return 存在返回 true（true when present）。
         */
        private boolean contains(final String method, final String pathPattern) {
            return routes.stream().anyMatch(route -> route.method.equals(method) && route.pathPattern.equals(pathPattern));
        }

        /**
         * @brief 分发路由处理器（Dispatch Route Handler）；
         *        Dispatch one registered route handler.
         *
         * @param method HTTP 方法（HTTP method）。
         * @param pathPattern 路径模式（Path pattern）。
         * @param request Web 请求（Web request）。
         * @return Web 响应（Web response）。
         * @throws Exception 分发异常（Dispatch exception）。
         */
        private WebResponse dispatch(final String method, final String pathPattern, final WebRequest request) throws Exception {
            for (RouteRecord route : routes) {
                if (route.method.equals(method) && route.pathPattern.equals(pathPattern)) {
                    return route.handler.handle(request);
                }
            }
            throw new IllegalStateException("Missing route: " + method + " " + pathPattern);
        }
    }

    /**
     * @brief 路由记录（Route Record）；
     *        Route registration record.
     *
     * @param method HTTP 方法（HTTP method）。
     * @param pathPattern 路径模式（Path pattern）。
     * @param handler 路由处理器（Route handler）。
     */
    private record RouteRecord(String method, String pathPattern, WebRouteHandler handler) {
    }

    /**
     * @brief 路由测试假请求（Fake Request for Route Tests）；
     *        Fake request implementation for route tests.
     */
    private static final class FakeRequest implements WebRequest {

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
            return "/";
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
            return Optional.empty();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<String> headers(final String name) {
            return List.of();
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
