package com.moesegfault.banking.presentation.cli.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @brief 分录列表 CLI 处理器测试（Show Entries CLI Handler Test），覆盖默认分页和输出 schema；
 *        Show-entries CLI handler tests covering default paging and output schema.
 */
class ShowEntriesCliHandlerTest {

    /**
     * @brief 验证默认 limit 会传递到应用层并输出分录 CSV；
     *        Verify default limit is passed to application layer and entries are printed as CSV.
     */
    @Test
    void shouldUseDefaultLimitAndPrintEntryList() {
        final LedgerRepository ledgerRepository = Mockito.mock(LedgerRepository.class);
        final ListLedgerEntriesHandler applicationHandler = new ListLedgerEntriesHandler(ledgerRepository);

        final LedgerEntry entry = LedgerEntry.restore(
                LedgerEntryId.of("entry-001"),
                "txn-001",
                PostingBatchId.of("batch-001"),
                "acc-001",
                CurrencyCode.of("USD"),
                EntryDirection.CREDIT,
                usd("100.0000"),
                usd("200.0000"),
                usd("150.0000"),
                EntryType.PRINCIPAL,
                Instant.parse("2026-04-28T10:00:01Z"));
        when(ledgerRepository.listRecentEntriesByAccountId(eq("acc-001"), eq(ListLedgerEntriesQuery.DEFAULT_LIMIT)))
                .thenReturn(List.of(entry));

        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final ShowEntriesCliHandler handler = new ShowEntriesCliHandler(
                applicationHandler,
                new PrintStream(buffer, true, StandardCharsets.UTF_8));

        handler.handle(new ParsedCommand(
                "ledger entries --account_id acc-001",
                List.of("ledger", "entries"),
                Map.of("account_id", "acc-001")));

        verify(ledgerRepository).listRecentEntriesByAccountId("acc-001", ListLedgerEntriesQuery.DEFAULT_LIMIT);
        assertEquals(
                "total=1\n"
                        + "entry_id,transaction_id,batch_id,account_id,currency_code,entry_direction,amount,"
                        + "ledger_balance_after,available_balance_after,entry_type,posted_at\n"
                        + "entry-001,txn-001,batch-001,acc-001,USD,CREDIT,100.0000,200.0000,150.0000,"
                        + "PRINCIPAL,2026-04-28T10:00:01Z\n",
                buffer.toString(StandardCharsets.UTF_8));
    }

    /**
     * @brief 验证可通过别名参数设置 limit；
     *        Verify limit can be set via alias option.
     */
    @Test
    void shouldAcceptLimitAliasOption() {
        final LedgerRepository ledgerRepository = Mockito.mock(LedgerRepository.class);
        final ListLedgerEntriesHandler applicationHandler = new ListLedgerEntriesHandler(ledgerRepository);
        when(ledgerRepository.listRecentEntriesByAccountId(eq("acc-002"), eq(5))).thenReturn(List.of());

        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final ShowEntriesCliHandler handler = new ShowEntriesCliHandler(
                applicationHandler,
                new PrintStream(buffer, true, StandardCharsets.UTF_8));

        handler.handle(new ParsedCommand(
                "ledger entries --account-id acc-002 --max 5",
                List.of("ledger", "entries"),
                Map.of("account-id", "acc-002", "max", "5")));

        verify(ledgerRepository).listRecentEntriesByAccountId("acc-002", 5);
        assertEquals(
                "total=0\n"
                        + "entry_id,transaction_id,batch_id,account_id,currency_code,entry_direction,amount,"
                        + "ledger_balance_after,available_balance_after,entry_type,posted_at\n",
                buffer.toString(StandardCharsets.UTF_8));
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
