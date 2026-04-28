package com.moesegfault.banking.presentation.web.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.moesegfault.banking.application.business.query.FindBusinessTransactionHandler;
import com.moesegfault.banking.application.business.query.ListBusinessTransactionsHandler;
import com.moesegfault.banking.presentation.web.WebJsonCodec;
import com.moesegfault.banking.presentation.web.WebRouteHandler;
import com.moesegfault.banking.presentation.web.WebRuntime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @brief 业务流水路由注册测试（Business Transaction Routes Test），验证 REST 路由注册完整性；
 *        Tests for business-transaction route registration completeness.
 */
class BusinessTransactionRoutesTest {

    /**
     * @brief 验证业务流水路由会注册列表、按 ID 和按参考号三条 GET 路由；
     *        Verify routes register three GET endpoints for list, by-id, and by-reference.
     */
    @Test
    void shouldRegisterAllBusinessTransactionRoutes() {
        final ListBusinessTransactionsHandler listHandler = Mockito.mock(ListBusinessTransactionsHandler.class);
        final FindBusinessTransactionHandler findHandler = Mockito.mock(FindBusinessTransactionHandler.class);
        final BusinessTransactionController controller = new BusinessTransactionController(
                listHandler,
                findHandler,
                new WebJsonCodec());
        final BusinessTransactionRoutes routes = new BusinessTransactionRoutes(controller);
        final RecordingRuntime runtime = new RecordingRuntime();

        routes.registerRoutes(runtime);

        assertEquals(3, runtime.routes().size());
        assertEquals("GET", runtime.routes().get(0).method());
        assertEquals(BusinessTransactionRoutes.PATH_BASE, runtime.routes().get(0).pathPattern());
        assertNotNull(runtime.routes().get(0).handler());

        assertEquals("GET", runtime.routes().get(1).method());
        assertEquals(BusinessTransactionRoutes.PATH_BY_TRANSACTION_ID, runtime.routes().get(1).pathPattern());
        assertNotNull(runtime.routes().get(1).handler());

        assertEquals("GET", runtime.routes().get(2).method());
        assertEquals(BusinessTransactionRoutes.PATH_BY_REFERENCE_NO, runtime.routes().get(2).pathPattern());
        assertNotNull(runtime.routes().get(2).handler());
    }

    /**
     * @brief 记录型 Runtime（Recording Runtime）；
     *        Runtime test double that records added routes.
     */
    private static final class RecordingRuntime implements WebRuntime {

        /**
         * @brief 路由记录列表（Route Records）；
         *        Recorded routes.
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
         * @brief 获取路由快照（Get Route Snapshot）；
         *        Get immutable snapshot of recorded routes.
         *
         * @return 路由记录列表（Route-record list）。
         */
        private List<RouteRecord> routes() {
            return List.copyOf(routes);
        }
    }

    /**
     * @brief 路由记录（Route Record）；
     *        Route record used by runtime test double.
     *
     * @param method HTTP 方法（HTTP method）。
     * @param pathPattern 路径模式（Path pattern）。
     * @param handler 路由处理器（Route handler）。
     */
    private record RouteRecord(String method, String pathPattern, WebRouteHandler handler) {
    }
}

