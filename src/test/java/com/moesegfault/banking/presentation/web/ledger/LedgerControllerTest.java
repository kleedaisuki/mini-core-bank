package com.moesegfault.banking.presentation.web.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moesegfault.banking.application.ledger.query.FindBalanceHandler;
import com.moesegfault.banking.application.ledger.query.FindBalanceQuery;
import com.moesegfault.banking.application.ledger.query.ListLedgerEntriesHandler;
import com.moesegfault.banking.application.ledger.query.ListLedgerEntriesQuery;
import com.moesegfault.banking.application.ledger.result.BalanceResult;
import com.moesegfault.banking.application.ledger.result.LedgerEntryResult;
import com.moesegfault.banking.domain.ledger.EntryDirection;
import com.moesegfault.banking.domain.ledger.EntryType;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import com.moesegfault.banking.presentation.web.WebRequest;
import com.moesegfault.banking.presentation.web.WebResponse;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @brief Ledger 控制器测试（Ledger Controller Test），验证请求参数解析与响应 schema 映射；
 *        Ledger controller tests verifying request-parameter parsing and response-schema mapping.
 */
class LedgerControllerTest {

    /**
     * @brief JSON 解析器（JSON ObjectMapper）；
     *        JSON object mapper for assertions.
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

    /**
     * @brief 验证余额查询成功时返回 200 与 snake_case JSON；
     *        Verify successful balance query returns HTTP 200 with snake_case JSON payload.
     *
     * @throws Exception 断言解析异常（Assertion parsing exception）。
     */
    @Test
    void shouldReturnBalanceResponseWhenFound() throws Exception {
        final FindBalanceHandler findBalanceHandler = Mockito.mock(FindBalanceHandler.class);
        final ListLedgerEntriesHandler listLedgerEntriesHandler = Mockito.mock(ListLedgerEntriesHandler.class);
        final LedgerController controller = new LedgerController(findBalanceHandler, listLedgerEntriesHandler);

        when(findBalanceHandler.handle(any())).thenReturn(Optional.of(new BalanceResult(
                "acc-001",
                CurrencyCode.of("USD"),
                usd("100.0000"),
                usd("80.5000"),
                Instant.parse("2026-04-28T13:00:00Z"))));

        final WebRequest request = new FakeWebRequest(
                Map.of(),
                Map.of("accountId", "acc-001", "currencyCode", "usd"));
        final WebResponse response = controller.findBalance(request);

        assertEquals(200, response.statusCode());
        final JsonNode payload = parseResponseBody(response);
        assertEquals("acc-001", payload.get("account_id").asText());
        assertEquals("USD", payload.get("currency_code").asText());
        assertEquals(0, payload.get("ledger_balance").decimalValue().compareTo(new BigDecimal("100.0000")));
        assertEquals(0, payload.get("available_balance").decimalValue().compareTo(new BigDecimal("80.5000")));
        assertEquals("2026-04-28T13:00:00Z", payload.get("updated_at").asText());

        final ArgumentCaptor<FindBalanceQuery> queryCaptor = ArgumentCaptor.forClass(FindBalanceQuery.class);
        verify(findBalanceHandler).handle(queryCaptor.capture());
        assertEquals("acc-001", queryCaptor.getValue().accountId());
        assertEquals(CurrencyCode.of("USD"), queryCaptor.getValue().currencyCode());
    }

    /**
     * @brief 验证余额不存在时抛出 NotFound 异常；
     *        Verify missing balance throws not-found exception.
     */
    @Test
    void shouldThrowNotFoundWhenBalanceMissing() {
        final FindBalanceHandler findBalanceHandler = Mockito.mock(FindBalanceHandler.class);
        final ListLedgerEntriesHandler listLedgerEntriesHandler = Mockito.mock(ListLedgerEntriesHandler.class);
        final LedgerController controller = new LedgerController(findBalanceHandler, listLedgerEntriesHandler);

        when(findBalanceHandler.handle(any())).thenReturn(Optional.empty());

        final WebRequest request = new FakeWebRequest(
                Map.of(),
                Map.of("accountId", "acc-404", "currencyCode", "USD"));
        assertThrows(LedgerBalanceNotFoundException.class, () -> controller.findBalance(request));
    }

    /**
     * @brief 验证分录查询返回列表与元信息；
     *        Verify ledger-entry query returns items with metadata.
     *
     * @throws Exception 断言解析异常（Assertion parsing exception）。
     */
    @Test
    void shouldReturnEntriesResponseWithLimitAndTotal() throws Exception {
        final FindBalanceHandler findBalanceHandler = Mockito.mock(FindBalanceHandler.class);
        final ListLedgerEntriesHandler listLedgerEntriesHandler = Mockito.mock(ListLedgerEntriesHandler.class);
        final LedgerController controller = new LedgerController(findBalanceHandler, listLedgerEntriesHandler);

        final LedgerEntryResult entry = new LedgerEntryResult(
                "entry-001",
                "txn-001",
                "batch-001",
                "acc-001",
                CurrencyCode.of("USD"),
                EntryDirection.CREDIT,
                usd("20.0000"),
                usd("120.0000"),
                usd("95.0000"),
                EntryType.PRINCIPAL,
                Instant.parse("2026-04-28T13:01:00Z"));
        when(listLedgerEntriesHandler.handle(any())).thenReturn(List.of(entry));

        final WebRequest request = new FakeWebRequest(
                Map.of("limit", "10"),
                Map.of("accountId", "acc-001"));
        final WebResponse response = controller.listLedgerEntries(request);

        assertEquals(200, response.statusCode());
        final JsonNode payload = parseResponseBody(response);
        assertEquals("acc-001", payload.get("account_id").asText());
        assertEquals(10, payload.get("limit").asInt());
        assertEquals(1, payload.get("total").asInt());
        assertEquals(1, payload.get("items").size());
        assertEquals("entry-001", payload.get("items").get(0).get("entry_id").asText());
        assertEquals("USD", payload.get("items").get(0).get("currency_code").asText());
        assertEquals("CREDIT", payload.get("items").get(0).get("entry_direction").asText());

        final ArgumentCaptor<ListLedgerEntriesQuery> queryCaptor = ArgumentCaptor.forClass(ListLedgerEntriesQuery.class);
        verify(listLedgerEntriesHandler).handle(queryCaptor.capture());
        assertEquals("acc-001", queryCaptor.getValue().accountId());
        assertEquals(10, queryCaptor.getValue().limit());
    }

    /**
     * @brief 验证缺省 limit 时使用应用层默认值；
     *        Verify default limit is used when query parameter is absent.
     */
    @Test
    void shouldUseDefaultLimitWhenLimitIsMissing() {
        final FindBalanceHandler findBalanceHandler = Mockito.mock(FindBalanceHandler.class);
        final ListLedgerEntriesHandler listLedgerEntriesHandler = Mockito.mock(ListLedgerEntriesHandler.class);
        final LedgerController controller = new LedgerController(findBalanceHandler, listLedgerEntriesHandler);

        when(listLedgerEntriesHandler.handle(any())).thenReturn(List.of());

        final WebRequest request = new FakeWebRequest(
                Map.of(),
                Map.of("accountId", "acc-002"));
        controller.listLedgerEntries(request);

        final ArgumentCaptor<ListLedgerEntriesQuery> queryCaptor = ArgumentCaptor.forClass(ListLedgerEntriesQuery.class);
        verify(listLedgerEntriesHandler).handle(queryCaptor.capture());
        assertEquals(ListLedgerEntriesQuery.DEFAULT_LIMIT, queryCaptor.getValue().limit());
    }

    /**
     * @brief 验证非法 limit 会抛参数异常；
     *        Verify invalid limit throws invalid-argument exception.
     */
    @Test
    void shouldRejectNonIntegerLimit() {
        final FindBalanceHandler findBalanceHandler = Mockito.mock(FindBalanceHandler.class);
        final ListLedgerEntriesHandler listLedgerEntriesHandler = Mockito.mock(ListLedgerEntriesHandler.class);
        final LedgerController controller = new LedgerController(findBalanceHandler, listLedgerEntriesHandler);

        final WebRequest request = new FakeWebRequest(
                Map.of("limit", "oops"),
                Map.of("accountId", "acc-003"));
        assertThrows(IllegalArgumentException.class, () -> controller.listLedgerEntries(request));
    }

    /**
     * @brief 解析响应 JSON（Parse Response JSON）；
     *        Parse response body bytes into JSON node.
     *
     * @param response Web 响应（Web response）。
     * @return JSON 节点（JSON node）。
     * @throws Exception JSON 解析异常（JSON parsing exception）。
     */
    private static JsonNode parseResponseBody(final WebResponse response) throws Exception {
        return OBJECT_MAPPER.readTree(new String(response.body(), StandardCharsets.UTF_8));
    }

    /**
     * @brief 创建 USD 金额（Create USD Money）；
     *        Create USD money helper.
     *
     * @param amount 金额字符串（Amount string）。
     * @return USD 金额对象（USD money object）。
     */
    private static Money usd(final String amount) {
        return Money.of(CurrencyCode.of("USD"), new BigDecimal(amount));
    }

    /**
     * @brief 控制器测试用假请求（Fake Request for Controller Tests）；
     *        Fake web request for ledger-controller unit tests.
     */
    private static final class FakeWebRequest implements WebRequest {

        /**
         * @brief 查询参数映射（Query Parameter Mapping）；
         *        Query-parameter mapping.
         */
        private final Map<String, String> queryParams;

        /**
         * @brief 路径参数映射（Path Parameter Mapping）；
         *        Path-parameter mapping.
         */
        private final Map<String, String> pathParams;

        /**
         * @brief 构造假请求（Construct Fake Request）；
         *        Construct fake request for unit tests.
         *
         * @param queryParams 查询参数映射（Query-parameter mapping）。
         * @param pathParams 路径参数映射（Path-parameter mapping）。
         */
        private FakeWebRequest(final Map<String, String> queryParams, final Map<String, String> pathParams) {
            this.queryParams = Map.copyOf(queryParams);
            this.pathParams = Map.copyOf(pathParams);
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
            return "/";
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
