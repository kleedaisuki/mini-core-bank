package com.moesegfault.banking.presentation.gui.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moesegfault.banking.application.business.query.FindBusinessTransactionHandler;
import com.moesegfault.banking.application.business.query.FindBusinessTransactionQuery;
import com.moesegfault.banking.application.business.result.BusinessTransactionResult;
import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.business.BusinessTransactionStatus;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @brief 业务流水详情控制器测试（Show Business Transaction Controller Test），验证查询选择器与模型更新逻辑；
 *        Tests for detail controller selector handling and model-state updates.
 */
class ShowBusinessTransactionControllerTest {

    /**
     * @brief 验证按交易 ID 查询会更新模型详情；
     *        Verify querying by transaction id updates detail model.
     */
    @Test
    void shouldLoadTransactionByTransactionId() {
        final FindBusinessTransactionHandler queryHandler = Mockito.mock(FindBusinessTransactionHandler.class);
        when(queryHandler.handle(any())).thenReturn(Optional.of(sampleResult("txn-001", "ref-001")));

        final ShowBusinessTransactionModel model = new ShowBusinessTransactionModel();
        final ShowBusinessTransactionController controller = new ShowBusinessTransactionController(queryHandler, model);
        final ShowBusinessTransactionView view = new ShowBusinessTransactionView();
        view.bindEventHandler(controller::onViewEvent);
        view.bindModel(model);

        view.submitByTransactionId("txn-001");

        final ArgumentCaptor<FindBusinessTransactionQuery> queryCaptor = ArgumentCaptor.forClass(FindBusinessTransactionQuery.class);
        verify(queryHandler).handle(queryCaptor.capture());
        assertEquals("txn-001", queryCaptor.getValue().transactionIdOrNull());
        assertNotNull(model.transactionOrNull());
        assertEquals("txn-001", model.transactionOrNull().transactionId());
        assertEquals("false", view.lastRenderedSnapshot().get("loading"));
    }

    /**
     * @brief 验证未命中时会输出 reference_no 维度错误消息；
     *        Verify not-found result produces error message with reference_no selector.
     */
    @Test
    void shouldSetNotFoundMessageWhenQueryByReferenceNoMisses() {
        final FindBusinessTransactionHandler queryHandler = Mockito.mock(FindBusinessTransactionHandler.class);
        when(queryHandler.handle(any())).thenReturn(Optional.empty());

        final ShowBusinessTransactionModel model = new ShowBusinessTransactionModel();
        final ShowBusinessTransactionController controller = new ShowBusinessTransactionController(queryHandler, model);

        controller.onViewEvent(new ViewEvent(
                ShowBusinessTransactionController.EVENT_QUERY,
                Map.of("reference_no", "ref-404")));

        assertEquals(
                "business_transaction_not_found reference_no=ref-404",
                model.errorMessageOrNull());
        assertEquals(false, model.isLoading());
    }

    /**
     * @brief 验证同时传入两种选择器会被拒绝；
     *        Verify providing both selectors is rejected.
     */
    @Test
    void shouldRejectAmbiguousSelectors() {
        final FindBusinessTransactionHandler queryHandler = Mockito.mock(FindBusinessTransactionHandler.class);
        final ShowBusinessTransactionModel model = new ShowBusinessTransactionModel();
        final ShowBusinessTransactionController controller = new ShowBusinessTransactionController(queryHandler, model);

        assertThrows(
                IllegalArgumentException.class,
                () -> controller.onViewEvent(new ViewEvent(
                        ShowBusinessTransactionController.EVENT_QUERY,
                        Map.of("transaction_id", "txn-001", "reference_no", "ref-001"))));
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
