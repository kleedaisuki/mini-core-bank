package com.moesegfault.banking.presentation.web.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moesegfault.banking.application.business.query.FindBusinessTransactionHandler;
import com.moesegfault.banking.application.business.query.FindBusinessTransactionQuery;
import com.moesegfault.banking.application.business.query.ListBusinessTransactionsHandler;
import com.moesegfault.banking.application.business.query.ListBusinessTransactionsQuery;
import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.business.BusinessTransactionStatus;
import com.moesegfault.banking.presentation.web.WebJsonCodec;
import com.moesegfault.banking.presentation.web.WebRequest;
import com.moesegfault.banking.presentation.web.WebResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @brief 业务流水 Web 控制器测试（Business Transaction Web Controller Test），验证详情查询、列表筛选与错误分支；
 *        Tests for business-transaction web controller covering detail lookup, list filters, and error branches.
 */
class BusinessTransactionControllerTest {

    /**
     * @brief 验证按交易 ID 查询详情会返回统一 schema；
     *        Verify detail query by transaction id returns unified response schema.
     */
    @Test
    void shouldGetBusinessTransactionByTransactionId() {
        final FindBusinessTransactionHandler findHandler = Mockito.mock(FindBusinessTransactionHandler.class);
        final ListBusinessTransactionsHandler listHandler = Mockito.mock(ListBusinessTransactionsHandler.class);
        when(findHandler.handle(any())).thenReturn(Optional.of(sampleResult("txn-001", "ref-001")));

        final BusinessTransactionController controller = new BusinessTransactionController(
                listHandler,
                findHandler,
                new WebJsonCodec());

        final WebResponse response = controller.getBusinessTransactionById(new FakeWebRequest(
                Map.of(),
                Map.of("transactionId", "txn-001"),
                ""));

        assertEquals(200, response.statusCode());
        final String body = new String(response.body(), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"transaction_id\":\"txn-001\""));
        assertTrue(body.contains("\"reference_no\":\"ref-001\""));

        final ArgumentCaptor<FindBusinessTransactionQuery> captor = ArgumentCaptor.forClass(FindBusinessTransactionQuery.class);
        verify(findHandler).handle(captor.capture());
        assertEquals("txn-001", captor.getValue().transactionIdOrNull());
    }

    /**
     * @brief 验证按参考号查询详情会返回统一 schema；
     *        Verify detail query by reference number returns unified response schema.
     */
    @Test
    void shouldGetBusinessTransactionByReferenceNo() {
        final FindBusinessTransactionHandler findHandler = Mockito.mock(FindBusinessTransactionHandler.class);
        final ListBusinessTransactionsHandler listHandler = Mockito.mock(ListBusinessTransactionsHandler.class);
        when(findHandler.handle(any())).thenReturn(Optional.of(sampleResult("txn-002", "ref-002")));

        final BusinessTransactionController controller = new BusinessTransactionController(
                listHandler,
                findHandler,
                new WebJsonCodec());

        final WebResponse response = controller.getBusinessTransactionByReferenceNo(new FakeWebRequest(
                Map.of(),
                Map.of("referenceNo", "ref-002"),
                ""));

        assertEquals(200, response.statusCode());
        final String body = new String(response.body(), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"transaction_id\":\"txn-002\""));
        assertTrue(body.contains("\"reference_no\":\"ref-002\""));

        final ArgumentCaptor<FindBusinessTransactionQuery> captor = ArgumentCaptor.forClass(FindBusinessTransactionQuery.class);
        verify(findHandler).handle(captor.capture());
        assertEquals("ref-002", captor.getValue().referenceNoOrNull());
    }

    /**
     * @brief 验证详情未命中会抛出 NotFound 异常；
     *        Verify missing detail result throws not-found exception.
     */
    @Test
    void shouldThrowNotFoundExceptionWhenTransactionMissing() {
        final FindBusinessTransactionHandler findHandler = Mockito.mock(FindBusinessTransactionHandler.class);
        final ListBusinessTransactionsHandler listHandler = Mockito.mock(ListBusinessTransactionsHandler.class);
        when(findHandler.handle(any())).thenReturn(Optional.empty());

        final BusinessTransactionController controller = new BusinessTransactionController(
                listHandler,
                findHandler,
                new WebJsonCodec());

        assertThrows(
                BusinessTransactionNotFoundException.class,
                () -> controller.getBusinessTransactionById(new FakeWebRequest(
                        Map.of(),
                        Map.of("transactionId", "txn-404"),
                        "")));
    }

    /**
     * @brief 验证列表查询可解析筛选参数并返回列表响应；
     *        Verify list query parses filter parameters and returns list response.
     */
    @Test
    void shouldListBusinessTransactionsWithFilters() {
        final FindBusinessTransactionHandler findHandler = Mockito.mock(FindBusinessTransactionHandler.class);
        final ListBusinessTransactionsHandler listHandler = Mockito.mock(ListBusinessTransactionsHandler.class);
        when(listHandler.handle(any())).thenReturn(List.of(sampleResult("txn-101", "ref-101")));

        final BusinessTransactionController controller = new BusinessTransactionController(
                listHandler,
                findHandler,
                new WebJsonCodec());

        final WebResponse response = controller.listBusinessTransactions(new FakeWebRequest(
                Map.of(
                        "initiator_customer_id", "cust-101",
                        "transaction_status", "success",
                        "limit", "2"),
                Map.of(),
                ""));

        assertEquals(200, response.statusCode());
        final String body = new String(response.body(), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"total\":1"));
        assertTrue(body.contains("\"items\":["));
        assertTrue(body.contains("\"transaction_id\":\"txn-101\""));

        final ArgumentCaptor<ListBusinessTransactionsQuery> captor = ArgumentCaptor.forClass(ListBusinessTransactionsQuery.class);
        verify(listHandler).handle(captor.capture());
        assertEquals("cust-101", captor.getValue().initiatorCustomerIdOrNull());
        assertEquals(BusinessTransactionStatus.SUCCESS, captor.getValue().transactionStatusOrNull());
        assertEquals(2, captor.getValue().limit());
    }

    /**
     * @brief 示例业务流水结果（Sample Business Transaction Result）；
     *        Build sample business-transaction result.
     *
     * @param transactionId 交易 ID（Transaction id）。
     * @param referenceNo 参考号（Reference number）。
     * @return 业务流水结果（Business transaction result）。
     */
    private static BusinessTransactionResult sampleResult(final String transactionId, final String referenceNo) {
        return new BusinessTransactionResult(
                transactionId,
                "ACCOUNT_OPEN",
                "cust-001",
                "op-001",
                BusinessChannel.ONLINE,
                BusinessTransactionStatus.SUCCESS,
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:10Z"),
                referenceNo,
                "ok");
    }

    /**
     * @brief 假 Web 请求对象（Fake Web Request Object）；
     *        Fake web request implementation for controller tests.
     */
    private static final class FakeWebRequest implements WebRequest {

        /**
         * @brief 查询参数（Query Parameters）；
         *        Query parameters.
         */
        private final Map<String, String> queryParameters;

        /**
         * @brief 路径参数（Path Parameters）；
         *        Path parameters.
         */
        private final Map<String, String> pathParameters;

        /**
         * @brief 请求体文本（Body Text）；
         *        Request body text.
         */
        private final String bodyText;

        /**
         * @brief 构造假请求（Construct Fake Request）；
         *        Construct fake request with query/path/body fields.
         *
         * @param queryParameters 查询参数（Query parameters）。
         * @param pathParameters 路径参数（Path parameters）。
         * @param bodyText 请求体文本（Body text）。
         */
        private FakeWebRequest(
                final Map<String, String> queryParameters,
                final Map<String, String> pathParameters,
                final String bodyText
        ) {
            this.queryParameters = Map.copyOf(queryParameters);
            this.pathParameters = Map.copyOf(pathParameters);
            this.bodyText = bodyText;
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
            return "/business-transactions";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<String> pathParam(final String name) {
            return Optional.ofNullable(pathParameters.get(name));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Optional<String> queryParam(final String name) {
            return Optional.ofNullable(queryParameters.get(name));
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

