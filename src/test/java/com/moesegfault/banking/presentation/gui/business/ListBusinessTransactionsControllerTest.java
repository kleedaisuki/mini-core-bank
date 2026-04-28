package com.moesegfault.banking.presentation.gui.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moesegfault.banking.application.business.query.ListBusinessTransactionsHandler;
import com.moesegfault.banking.application.business.query.ListBusinessTransactionsQuery;
import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.business.BusinessTransactionStatus;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @brief 业务流水列表控制器测试（List Business Transactions Controller Test），验证筛选解析、列表加载与行选择；
 *        Tests for list controller filter parsing, loading behavior, and row selection.
 */
class ListBusinessTransactionsControllerTest {

    /**
     * @brief 验证初始化会触发默认查询；
     *        Verify controller init triggers default query.
     */
    @Test
    void shouldLoadDefaultListOnInit() {
        final ListBusinessTransactionsHandler listHandler = Mockito.mock(ListBusinessTransactionsHandler.class);
        when(listHandler.handle(any())).thenReturn(List.of(sampleResult("txn-100", "ref-100")));

        final ListBusinessTransactionsModel model = new ListBusinessTransactionsModel();
        final ListBusinessTransactionsController controller = new ListBusinessTransactionsController(listHandler, model);

        controller.init();

        final ArgumentCaptor<ListBusinessTransactionsQuery> queryCaptor = ArgumentCaptor.forClass(ListBusinessTransactionsQuery.class);
        verify(listHandler).handle(queryCaptor.capture());
        assertEquals(ListBusinessTransactionsQuery.DEFAULT_LIMIT, queryCaptor.getValue().limit());
        assertEquals(1, model.transactions().size());
        assertEquals(false, model.isLoading());
    }

    /**
     * @brief 验证支持 CLI 对齐的筛选别名并可按行选择交易；
     *        Verify CLI-aligned filter aliases and row selection are supported.
     */
    @Test
    void shouldParseAliasFiltersAndSelectRow() {
        final ListBusinessTransactionsHandler listHandler = Mockito.mock(ListBusinessTransactionsHandler.class);
        when(listHandler.handle(any())).thenReturn(List.of(
                sampleResult("txn-201", "ref-201"),
                sampleResult("txn-202", "ref-202")));

        final ListBusinessTransactionsModel model = new ListBusinessTransactionsModel();
        final ListBusinessTransactionsController controller = new ListBusinessTransactionsController(listHandler, model);

        controller.onViewEvent(new ViewEvent(
                ListBusinessTransactionsController.EVENT_QUERY,
                Map.of(
                        "customer-id", "cus-201",
                        "status", "success",
                        "limit", "2")));

        final ArgumentCaptor<ListBusinessTransactionsQuery> queryCaptor = ArgumentCaptor.forClass(ListBusinessTransactionsQuery.class);
        verify(listHandler).handle(queryCaptor.capture());
        assertEquals("cus-201", queryCaptor.getValue().initiatorCustomerIdOrNull());
        assertEquals(BusinessTransactionStatus.SUCCESS, queryCaptor.getValue().transactionStatusOrNull());
        assertEquals(2, queryCaptor.getValue().limit());

        controller.onViewEvent(new ViewEvent(
                ListBusinessTransactionsController.EVENT_SELECT_ROW,
                Map.of("row_index", "1")));

        assertEquals("txn-202", model.selectedTransactionIdOrNull());
    }

    /**
     * @brief 验证非法 limit 会被拒绝；
     *        Verify invalid limit is rejected.
     */
    @Test
    void shouldRejectInvalidLimit() {
        final ListBusinessTransactionsHandler listHandler = Mockito.mock(ListBusinessTransactionsHandler.class);
        final ListBusinessTransactionsModel model = new ListBusinessTransactionsModel();
        final ListBusinessTransactionsController controller = new ListBusinessTransactionsController(listHandler, model);

        assertThrows(
                IllegalArgumentException.class,
                () -> controller.onViewEvent(new ViewEvent(
                        ListBusinessTransactionsController.EVENT_QUERY,
                        Map.of("limit", "0"))));
    }

    /**
     * @brief 构造示例业务流水结果（Build Sample Business Transaction Result）；
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
                "cus-001",
                "op-001",
                BusinessChannel.ONLINE,
                BusinessTransactionStatus.SUCCESS,
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:10Z"),
                referenceNo,
                "ok");
    }
}
