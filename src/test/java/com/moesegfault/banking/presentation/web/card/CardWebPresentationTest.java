package com.moesegfault.banking.presentation.web.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moesegfault.banking.application.card.command.IssueCreditCardHandler;
import com.moesegfault.banking.application.card.command.IssueDebitCardCommand;
import com.moesegfault.banking.application.card.command.IssueDebitCardHandler;
import com.moesegfault.banking.application.card.command.IssueSupplementaryCreditCardHandler;
import com.moesegfault.banking.application.card.command.IssueSupplementaryDebitCardHandler;
import com.moesegfault.banking.application.card.query.FindCardHandler;
import com.moesegfault.banking.application.card.result.CardKind;
import com.moesegfault.banking.application.card.result.CardResult;
import com.moesegfault.banking.application.card.result.IssueCardResult;
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
 * @brief Card Web 展示层测试（Card Web Presentation Test），验证路由注册、请求映射与响应 schema；
 *        Card web-presentation tests verifying route registration, request mapping, and response schema.
 */
class CardWebPresentationTest {

    /**
     * @brief 验证 CardRoutes 会注册全部 card REST 路由；
     *        Verify CardRoutes registers all card REST routes.
     */
    @Test
    void shouldRegisterAllCardRoutes() {
        final CardController controller = newController(
                Mockito.mock(IssueDebitCardHandler.class),
                Mockito.mock(IssueSupplementaryDebitCardHandler.class),
                Mockito.mock(IssueCreditCardHandler.class),
                Mockito.mock(IssueSupplementaryCreditCardHandler.class),
                Mockito.mock(FindCardHandler.class));
        final CardRoutes routes = new CardRoutes(controller);
        final RecordingRuntime runtime = new RecordingRuntime();

        routes.registerRoutes(runtime);

        assertEquals(5, runtime.routes().size());
        assertTrue(runtime.contains("POST", "/cards/debit"));
        assertTrue(runtime.contains("POST", "/cards/supplementary-debit"));
        assertTrue(runtime.contains("POST", "/cards/credit"));
        assertTrue(runtime.contains("POST", "/cards/supplementary-credit"));
        assertTrue(runtime.contains("GET", "/cards/{cardId}"));
    }

    /**
     * @brief 验证主借记卡请求体可映射为应用命令并返回 201；
     *        Verify issue-debit JSON body maps to application command and returns HTTP 201.
     */
    @Test
    void shouldIssueDebitCardFromJsonBody() {
        final IssueDebitCardHandler issueDebitCardHandler = Mockito.mock(IssueDebitCardHandler.class);
        final CardController controller = newController(
                issueDebitCardHandler,
                Mockito.mock(IssueSupplementaryDebitCardHandler.class),
                Mockito.mock(IssueCreditCardHandler.class),
                Mockito.mock(IssueSupplementaryCreditCardHandler.class),
                Mockito.mock(FindCardHandler.class));

        when(issueDebitCardHandler.handle(any(IssueDebitCardCommand.class))).thenReturn(new IssueCardResult(
                "card-001",
                "6222****5555",
                CardKind.DEBIT,
                "ACTIVE",
                "cust-001",
                Instant.parse("2026-04-28T10:00:00Z"),
                null));

        final FakeRequest request = new FakeRequest(
                "POST",
                "/cards/debit",
                Map.of(),
                Map.of(),
                """
                {
                  "holder_customer_id": "cust-001",
                  "savings_account_id": "sav-001",
                  "fx_account_id": "fx-001",
                  "card_no": "6222000011115555"
                }
                """);

        final WebResponse response = controller.issueDebitCard(request);

        assertEquals(201, response.statusCode());
        assertEquals("/cards/card-001", response.headers().get("Location").get(0));
        final String responseJson = new String(response.body(), StandardCharsets.UTF_8);
        assertTrue(responseJson.contains("\"card_id\":\"card-001\""));
        assertTrue(responseJson.contains("\"card_kind\":\"DEBIT\""));

        final ArgumentCaptor<IssueDebitCardCommand> commandCaptor = ArgumentCaptor.forClass(IssueDebitCardCommand.class);
        verify(issueDebitCardHandler).handle(commandCaptor.capture());
        assertEquals("cust-001", commandCaptor.getValue().holderCustomerId());
        assertEquals("sav-001", commandCaptor.getValue().savingsAccountId());
        assertEquals("fx-001", commandCaptor.getValue().fxAccountId());
        assertEquals("6222000011115555", commandCaptor.getValue().cardNo());
    }

    /**
     * @brief 验证 `GET /cards/{cardId}` 返回统一卡响应 schema；
     *        Verify `GET /cards/{cardId}` returns unified card-response schema.
     */
    @Test
    void shouldShowCardByPathParameter() {
        final FindCardHandler findCardHandler = Mockito.mock(FindCardHandler.class);
        final CardController controller = newController(
                Mockito.mock(IssueDebitCardHandler.class),
                Mockito.mock(IssueSupplementaryDebitCardHandler.class),
                Mockito.mock(IssueCreditCardHandler.class),
                Mockito.mock(IssueSupplementaryCreditCardHandler.class),
                findCardHandler);

        when(findCardHandler.handle(any())).thenReturn(new CardResult(
                "card-200",
                "4333****6666",
                "cust-020",
                "ACTIVE",
                CardKind.SUPPLEMENTARY_CREDIT,
                Instant.parse("2026-04-28T11:00:00Z"),
                null,
                null,
                null,
                "cc-acc-20",
                "card-100"));

        final FakeRequest request = new FakeRequest(
                "GET",
                "/cards/card-200",
                Map.of("cardId", "card-200"),
                Map.of(),
                "");

        final WebResponse response = controller.showCard(request);

        assertEquals(200, response.statusCode());
        final String responseJson = new String(response.body(), StandardCharsets.UTF_8);
        assertTrue(responseJson.contains("\"card_id\":\"card-200\""));
        assertTrue(responseJson.contains("\"card_kind\":\"SUPPLEMENTARY_CREDIT\""));
        assertTrue(responseJson.contains("\"credit_card_account_id\":\"cc-acc-20\""));
        assertTrue(responseJson.contains("\"primary_card_id\":\"card-100\""));
    }

    /**
     * @brief 创建卡控制器（Create Card Controller）；
     *        Create card controller with injected handler mocks.
     *
     * @param issueDebitCardHandler 主借记卡发卡处理器（Issue debit-card handler）。
     * @param issueSupplementaryDebitCardHandler 借记附属卡发卡处理器（Issue supplementary debit-card handler）。
     * @param issueCreditCardHandler 主信用卡发卡处理器（Issue credit-card handler）。
     * @param issueSupplementaryCreditCardHandler 信用附属卡发卡处理器（Issue supplementary credit-card handler）。
     * @param findCardHandler 卡查询处理器（Find-card handler）。
     * @return 卡控制器（Card controller）。
     */
    private CardController newController(
            final IssueDebitCardHandler issueDebitCardHandler,
            final IssueSupplementaryDebitCardHandler issueSupplementaryDebitCardHandler,
            final IssueCreditCardHandler issueCreditCardHandler,
            final IssueSupplementaryCreditCardHandler issueSupplementaryCreditCardHandler,
            final FindCardHandler findCardHandler
    ) {
        return new CardController(
                issueDebitCardHandler,
                issueSupplementaryDebitCardHandler,
                issueCreditCardHandler,
                issueSupplementaryCreditCardHandler,
                findCardHandler,
                new WebJsonCodec());
    }

    /**
     * @brief 记录型 Web Runtime（Recording Web Runtime）；
     *        Recording runtime for route-registration verification.
     */
    private static final class RecordingRuntime implements WebRuntime {

        /**
         * @brief 已注册路由列表（Registered Route Entries）；
         *        Registered route entries.
         */
        private final List<RouteEntry> routeEntries = new ArrayList<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void addRoute(final String method, final String pathPattern, final WebRouteHandler handler) {
            routeEntries.add(new RouteEntry(method, pathPattern, handler));
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
         * @return 路由列表（Route list）。
         */
        List<RouteEntry> routes() {
            return List.copyOf(routeEntries);
        }

        /**
         * @brief 判断是否包含指定路由（Check Whether Route Exists）；
         *        Check whether one route entry exists.
         *
         * @param method HTTP 方法（HTTP method）。
         * @param path 路径模式（Path pattern）。
         * @return 是否存在（Whether exists）。
         */
        boolean contains(final String method, final String path) {
            return routeEntries.stream().anyMatch(entry -> entry.method.equals(method) && entry.path.equals(path));
        }
    }

    /**
     * @brief 路由条目（Route Entry）；
     *        Route entry record.
     *
     * @param method HTTP 方法（HTTP method）。
     * @param path 路径（Path）。
     * @param handler 路由处理器（Route handler）。
     */
    private record RouteEntry(String method, String path, WebRouteHandler handler) {
    }

    /**
     * @brief 假请求对象（Fake Web Request）；
     *        Fake web request for controller unit tests.
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
         * @param pathParams 路径参数映射（Path-parameter map）。
         * @param queryParams 查询参数映射（Query-parameter map）。
         * @param bodyText 请求体文本（Request body text）。
         */
        private FakeRequest(final String method,
                            final String path,
                            final Map<String, String> pathParams,
                            final Map<String, String> queryParams,
                            final String bodyText) {
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

