package com.moesegfault.banking.presentation.web.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moesegfault.banking.application.account.command.FreezeAccountCommand;
import com.moesegfault.banking.application.account.command.FreezeAccountHandler;
import com.moesegfault.banking.application.account.command.OpenFxAccountHandler;
import com.moesegfault.banking.application.account.command.OpenInvestmentAccountHandler;
import com.moesegfault.banking.application.account.command.OpenSavingsAccountCommand;
import com.moesegfault.banking.application.account.command.OpenSavingsAccountHandler;
import com.moesegfault.banking.application.account.query.FindAccountHandler;
import com.moesegfault.banking.application.account.query.ListCustomerAccountsHandler;
import com.moesegfault.banking.application.account.query.ListCustomerAccountsQuery;
import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.application.account.result.OpenAccountResult;
import com.moesegfault.banking.presentation.web.WebJsonCodec;
import com.moesegfault.banking.presentation.web.WebRequest;
import com.moesegfault.banking.presentation.web.WebResponse;
import com.moesegfault.banking.presentation.web.WebRouteHandler;
import com.moesegfault.banking.presentation.web.WebRuntime;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @brief Account Web 展示层测试（Account Web Presentation Test），验证路由注册、请求映射与响应 schema；
 *        Account web-presentation tests verifying route registration, request mapping, and response schema.
 */
class AccountWebPresentationTest {

    /**
     * @brief 验证 AccountRoutes 会注册完整 account REST 路由；
     *        Verify AccountRoutes registers full account REST routes.
     */
    @Test
    void shouldRegisterAllAccountRoutes() {
        final AccountController controller = newController(
                Mockito.mock(OpenSavingsAccountHandler.class),
                Mockito.mock(OpenFxAccountHandler.class),
                Mockito.mock(OpenInvestmentAccountHandler.class),
                Mockito.mock(FindAccountHandler.class),
                Mockito.mock(ListCustomerAccountsHandler.class),
                Mockito.mock(FreezeAccountHandler.class));
        final AccountRoutes routes = new AccountRoutes(controller);
        final RecordingRuntime runtime = new RecordingRuntime();

        routes.registerRoutes(runtime);

        assertEquals(7, runtime.routes().size());
        assertTrue(runtime.contains("POST", AccountWebSchema.PATH_OPEN_SAVINGS));
        assertTrue(runtime.contains("POST", AccountWebSchema.PATH_OPEN_FX));
        assertTrue(runtime.contains("POST", AccountWebSchema.PATH_OPEN_INVESTMENT));
        assertTrue(runtime.contains("GET", AccountWebSchema.PATH_ACCOUNT_DETAIL));
        assertTrue(runtime.contains("GET", AccountWebSchema.PATH_ACCOUNT_BY_NO));
        assertTrue(runtime.contains("GET", AccountWebSchema.PATH_ACCOUNTS));
        assertTrue(runtime.contains("PATCH", AccountWebSchema.PATH_ACCOUNT_DETAIL));
    }

    /**
     * @brief 验证储蓄开户请求体会映射为应用命令并返回 201；
     *        Verify open-savings JSON body maps to application command and returns HTTP 201.
     */
    @Test
    void shouldOpenSavingsAccountFromJsonBody() {
        final OpenSavingsAccountHandler openSavingsAccountHandler = Mockito.mock(OpenSavingsAccountHandler.class);
        final AccountController controller = newController(
                openSavingsAccountHandler,
                Mockito.mock(OpenFxAccountHandler.class),
                Mockito.mock(OpenInvestmentAccountHandler.class),
                Mockito.mock(FindAccountHandler.class),
                Mockito.mock(ListCustomerAccountsHandler.class),
                Mockito.mock(FreezeAccountHandler.class));

        when(openSavingsAccountHandler.handle(any(OpenSavingsAccountCommand.class))).thenReturn(new OpenAccountResult(
                "acc-001",
                "cust-001",
                "SA-001",
                "SAVINGS",
                "ACTIVE",
                Instant.parse("2026-04-28T10:00:00Z"),
                null));

        final FakeRequest request = new FakeRequest(
                "POST",
                AccountWebSchema.PATH_OPEN_SAVINGS,
                Map.of(),
                Map.of(),
                """
                {
                  "customer_id": "cust-001",
                  "account_no": "SA-001"
                }
                """);

        final WebResponse response = controller.openSavingsAccount(request);

        assertEquals(201, response.statusCode());
        assertEquals("/accounts/acc-001", response.headers().get("Location").get(0));
        final String responseJson = new String(response.body(), StandardCharsets.UTF_8);
        assertTrue(responseJson.contains("\"account_id\":\"acc-001\""));
        assertTrue(responseJson.contains("\"account_type\":\"SAVINGS\""));
        assertTrue(responseJson.contains("\"customer_id\":\"cust-001\""));

        final ArgumentCaptor<OpenSavingsAccountCommand> commandCaptor =
                ArgumentCaptor.forClass(OpenSavingsAccountCommand.class);
        verify(openSavingsAccountHandler).handle(commandCaptor.capture());
        assertEquals("cust-001", commandCaptor.getValue().customerId());
        assertEquals("SA-001", commandCaptor.getValue().accountNo());
    }

    /**
     * @brief 验证账户列表接口可解析查询参数并输出统一 schema；
     *        Verify account-list endpoint parses query parameters and returns unified schema.
     */
    @Test
    void shouldListCustomerAccountsWithFilters() {
        final ListCustomerAccountsHandler listCustomerAccountsHandler = Mockito.mock(ListCustomerAccountsHandler.class);
        final AccountController controller = newController(
                Mockito.mock(OpenSavingsAccountHandler.class),
                Mockito.mock(OpenFxAccountHandler.class),
                Mockito.mock(OpenInvestmentAccountHandler.class),
                Mockito.mock(FindAccountHandler.class),
                listCustomerAccountsHandler,
                Mockito.mock(FreezeAccountHandler.class));

        when(listCustomerAccountsHandler.handle(any(ListCustomerAccountsQuery.class))).thenReturn(List.of(new AccountResult(
                "acc-101",
                "cust-101",
                "FX-101",
                "FX",
                "ACTIVE",
                Instant.parse("2026-04-28T11:00:00Z"),
                null,
                "sav-101")));

        final FakeRequest request = new FakeRequest(
                "GET",
                AccountWebSchema.PATH_ACCOUNTS,
                Map.of(),
                Map.of(
                        "customer_id", "cust-101",
                        "include_closed_accounts", "yes"),
                "");

        final WebResponse response = controller.listCustomerAccounts(request);

        assertEquals(200, response.statusCode());
        final String responseJson = new String(response.body(), StandardCharsets.UTF_8);
        assertTrue(responseJson.contains("\"total\":1"));
        assertTrue(responseJson.contains("\"account_id\":\"acc-101\""));
        assertTrue(responseJson.contains("\"linked_savings_account_id\":\"sav-101\""));

        final ArgumentCaptor<ListCustomerAccountsQuery> queryCaptor = ArgumentCaptor.forClass(ListCustomerAccountsQuery.class);
        verify(listCustomerAccountsHandler).handle(queryCaptor.capture());
        assertEquals("cust-101", queryCaptor.getValue().customerId());
        assertTrue(queryCaptor.getValue().includeClosedAccounts());
    }

    /**
     * @brief 验证冻结账户接口会映射命令并返回更新后的账户快照；
     *        Verify freeze-account endpoint maps command and returns updated account snapshot.
     */
    @Test
    void shouldFreezeAccountFromPatchRequest() {
        final FreezeAccountHandler freezeAccountHandler = Mockito.mock(FreezeAccountHandler.class);
        final AccountController controller = newController(
                Mockito.mock(OpenSavingsAccountHandler.class),
                Mockito.mock(OpenFxAccountHandler.class),
                Mockito.mock(OpenInvestmentAccountHandler.class),
                Mockito.mock(FindAccountHandler.class),
                Mockito.mock(ListCustomerAccountsHandler.class),
                freezeAccountHandler);

        when(freezeAccountHandler.handle(any(FreezeAccountCommand.class))).thenReturn(new AccountResult(
                "acc-301",
                "cust-301",
                "SA-301",
                "SAVINGS",
                "FROZEN",
                Instant.parse("2026-04-28T12:00:00Z"),
                null,
                null));

        final FakeRequest request = new FakeRequest(
                "PATCH",
                "/accounts/acc-301",
                Map.of(AccountWebSchema.PATH_PARAM_ACCOUNT_ID, "acc-301"),
                Map.of(),
                """
                {
                  "account_status": "FROZEN",
                  "freeze_reason": "risk-review"
                }
                """);

        final WebResponse response = controller.freezeAccount(request);

        assertEquals(200, response.statusCode());
        final String responseJson = new String(response.body(), StandardCharsets.UTF_8);
        assertTrue(responseJson.contains("\"account_status\":\"FROZEN\""));
        assertTrue(responseJson.contains("\"account_id\":\"acc-301\""));

        final ArgumentCaptor<FreezeAccountCommand> commandCaptor = ArgumentCaptor.forClass(FreezeAccountCommand.class);
        verify(freezeAccountHandler).handle(commandCaptor.capture());
        assertEquals("acc-301", commandCaptor.getValue().accountId());
        assertEquals("risk-review", commandCaptor.getValue().freezeReason());
    }

    /**
     * @brief 创建账户控制器（Create Account Controller）；
     *        Create account controller with injected handler mocks.
     *
     * @param openSavingsAccountHandler 开立储蓄账户处理器（Open savings-account handler）。
     * @param openFxAccountHandler 开立外汇账户处理器（Open FX-account handler）。
     * @param openInvestmentAccountHandler 开立投资账户处理器（Open investment-account handler）。
     * @param findAccountHandler 查询单账户处理器（Find-account handler）。
     * @param listCustomerAccountsHandler 列举客户账户处理器（List-customer-accounts handler）。
     * @param freezeAccountHandler 冻结账户处理器（Freeze-account handler）。
     * @return 账户控制器（Account controller）。
     */
    private AccountController newController(
            final OpenSavingsAccountHandler openSavingsAccountHandler,
            final OpenFxAccountHandler openFxAccountHandler,
            final OpenInvestmentAccountHandler openInvestmentAccountHandler,
            final FindAccountHandler findAccountHandler,
            final ListCustomerAccountsHandler listCustomerAccountsHandler,
            final FreezeAccountHandler freezeAccountHandler
    ) {
        return new AccountController(
                openSavingsAccountHandler,
                openFxAccountHandler,
                openInvestmentAccountHandler,
                findAccountHandler,
                listCustomerAccountsHandler,
                freezeAccountHandler,
                new WebJsonCodec());
    }

    /**
     * @brief 记录型 Web Runtime（Recording Web Runtime）；
     *        Recording runtime for route-registration verification.
     */
    private static final class RecordingRuntime implements WebRuntime {

        /**
         * @brief 路由记录列表（Route Records）；
         *        Recorded route entries.
         */
        private final List<RouteRecord> routeRecords = new ArrayList<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void addRoute(final String method, final String pathPattern, final WebRouteHandler handler) {
            routeRecords.add(new RouteRecord(method, pathPattern, handler));
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
         *        Get immutable route snapshot.
         *
         * @return 路由记录列表（Route-record list）。
         */
        List<RouteRecord> routes() {
            return List.copyOf(routeRecords);
        }

        /**
         * @brief 判断是否存在指定路由（Check Route Existence）；
         *        Check whether one route exists.
         *
         * @param method HTTP 方法（HTTP method）。
         * @param pathPattern 路径模式（Path pattern）。
         * @return 是否存在（Whether exists）。
         */
        boolean contains(final String method, final String pathPattern) {
            return routeRecords.stream()
                    .anyMatch(value -> value.method.equals(method) && value.pathPattern.equals(pathPattern));
        }
    }

    /**
     * @brief 路由记录（Route Record）；
     *        Route record used by recording runtime.
     *
     * @param method HTTP 方法（HTTP method）。
     * @param pathPattern 路径模式（Path pattern）。
     * @param handler 路由处理器（Route handler）。
     */
    private record RouteRecord(String method, String pathPattern, WebRouteHandler handler) {
    }

    /**
     * @brief 假请求对象（Fake Web Request）；
     *        Fake web request implementation for controller tests.
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
         * @brief 路径参数映射（Path-parameter Map）；
         *        Path-parameter map.
         */
        private final Map<String, String> pathParams;

        /**
         * @brief 查询参数映射（Query-parameter Map）；
         *        Query-parameter map.
         */
        private final Map<String, String> queryParams;

        /**
         * @brief 请求体文本（Request Body Text）；
         *        Request body text.
         */
        private final String bodyText;

        /**
         * @brief 构造假请求（Construct Fake Request）；
         *        Construct fake request for unit tests.
         *
         * @param method HTTP 方法（HTTP method）。
         * @param path 请求路径（Request path）。
         * @param pathParams 路径参数（Path parameters）。
         * @param queryParams 查询参数（Query parameters）。
         * @param bodyText 请求体文本（Request body text）。
         */
        private FakeRequest(
                final String method,
                final String path,
                final Map<String, String> pathParams,
                final Map<String, String> queryParams,
                final String bodyText
        ) {
            this.method = method;
            this.path = path;
            this.pathParams = Map.copyOf(pathParams);
            this.queryParams = Map.copyOf(queryParams);
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

