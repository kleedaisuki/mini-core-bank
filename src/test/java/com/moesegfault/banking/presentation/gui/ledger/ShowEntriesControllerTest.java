package com.moesegfault.banking.presentation.gui.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moesegfault.banking.application.ledger.query.ListLedgerEntriesHandler;
import com.moesegfault.banking.application.ledger.query.ListLedgerEntriesQuery;
import com.moesegfault.banking.domain.ledger.EntryDirection;
import com.moesegfault.banking.domain.ledger.EntryType;
import com.moesegfault.banking.domain.ledger.LedgerEntry;
import com.moesegfault.banking.domain.ledger.LedgerEntryId;
import com.moesegfault.banking.domain.ledger.LedgerRepository;
import com.moesegfault.banking.domain.ledger.PostingBatchId;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.Money;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @brief 分录查询控制器测试（Show Entries Controller Test），验证 limit 解析与应用层查询链路；
 *        Tests for entries-query controller covering limit parsing and app-layer invocation.
 */
class ShowEntriesControllerTest {

    /**
     * @brief 验证指定 limit 时会查询并回填分录结果；
     *        Verify specified limit is passed through and entry results are written.
     */
    @Test
    void shouldLoadEntriesWithProvidedLimit() {
        final LedgerRepository ledgerRepository = Mockito.mock(LedgerRepository.class);
        when(ledgerRepository.listRecentEntriesByAccountId(eq("acc-001"), eq(5)))
                .thenReturn(List.of(sampleEntry()));

        final ShowEntriesModel model = new ShowEntriesModel();
        final ShowEntriesController controller = new ShowEntriesController(
                model,
                new ListLedgerEntriesHandler(ledgerRepository),
                new GuiExceptionHandler());

        controller.onViewEvent(new ViewEvent(
                ShowEntriesController.EVENT_SEARCH_ENTRIES,
                Map.of(
                        ShowEntriesController.FIELD_ACCOUNT_ID, "acc-001",
                        ShowEntriesController.FIELD_LIMIT, "5")));

        verify(ledgerRepository).listRecentEntriesByAccountId("acc-001", 5);
        assertEquals(1, model.entries().size());
        assertEquals("", model.errorMessage());
        assertFalse(model.loading());
    }

    /**
     * @brief 验证缺省 limit 会回落到默认值；
     *        Verify missing limit falls back to default limit.
     */
    @Test
    void shouldUseDefaultLimitWhenLimitIsMissing() {
        final LedgerRepository ledgerRepository = Mockito.mock(LedgerRepository.class);
        when(ledgerRepository.listRecentEntriesByAccountId(eq("acc-002"), eq(ListLedgerEntriesQuery.DEFAULT_LIMIT)))
                .thenReturn(List.of());

        final ShowEntriesModel model = new ShowEntriesModel();
        final ShowEntriesController controller = new ShowEntriesController(
                model,
                new ListLedgerEntriesHandler(ledgerRepository),
                new GuiExceptionHandler());

        controller.onViewEvent(new ViewEvent(
                ShowEntriesController.EVENT_SEARCH_ENTRIES,
                Map.of(ShowEntriesController.FIELD_ACCOUNT_ID, "acc-002")));

        verify(ledgerRepository).listRecentEntriesByAccountId("acc-002", ListLedgerEntriesQuery.DEFAULT_LIMIT);
        assertEquals("total=0", model.hintMessage());
        assertEquals(0, model.entries().size());
        assertFalse(model.loading());
    }

    /**
     * @brief 验证非法 limit 会映射为可读错误；
     *        Verify invalid limit is mapped to readable error message.
     */
    @Test
    void shouldMapInvalidLimitToUserErrorMessage() {
        final LedgerRepository ledgerRepository = Mockito.mock(LedgerRepository.class);

        final ShowEntriesModel model = new ShowEntriesModel();
        final ShowEntriesController controller = new ShowEntriesController(
                model,
                new ListLedgerEntriesHandler(ledgerRepository),
                new GuiExceptionHandler());

        controller.onViewEvent(new ViewEvent(
                ShowEntriesController.EVENT_SEARCH_ENTRIES,
                Map.of(
                        ShowEntriesController.FIELD_ACCOUNT_ID, "acc-003",
                        ShowEntriesController.FIELD_LIMIT, "oops")));

        assertTrue(model.errorMessage().startsWith("输入参数不合法:"));
        assertEquals(0, model.entries().size());
        assertFalse(model.loading());
    }

    /**
     * @brief 构造样例分录（Build Sample Ledger Entry）；
     *        Build one sample ledger entry for query result tests.
     *
     * @return 样例分录（Sample ledger entry）。
     */
    private static LedgerEntry sampleEntry() {
        return LedgerEntry.restore(
                LedgerEntryId.of("entry-001"),
                "txn-001",
                PostingBatchId.of("batch-001"),
                "acc-001",
                CurrencyCode.of("USD"),
                EntryDirection.DEBIT,
                usd("12.34"),
                usd("120.00"),
                usd("100.00"),
                EntryType.FEE,
                Instant.parse("2026-04-28T11:22:33Z"));
    }

    /**
     * @brief 构造 USD 金额（Build USD Money）；
     *        Build USD money helper.
     *
     * @param amount 金额字符串（Amount string）。
     * @return USD 金额（USD money）。
     */
    private static Money usd(final String amount) {
        return Money.of(CurrencyCode.of("USD"), new BigDecimal(amount));
    }
}
