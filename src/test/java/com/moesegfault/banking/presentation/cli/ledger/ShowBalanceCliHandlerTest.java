package com.moesegfault.banking.presentation.cli.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.moesegfault.banking.application.ledger.query.FindBalanceHandler;
import com.moesegfault.banking.domain.ledger.Balance;
import com.moesegfault.banking.domain.ledger.LedgerRepository;
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
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @brief 余额查询 CLI 处理器测试（Show Balance CLI Handler Test），覆盖命中与未命中输出；
 *        Show-balance CLI handler tests covering found and not-found outputs.
 */
class ShowBalanceCliHandlerTest {

    /**
     * @brief 验证命中余额时按 schema 输出 CSV；
     *        Verify found balance is printed with canonical CSV schema.
     */
    @Test
    void shouldPrintBalanceCsvWhenFound() {
        final LedgerRepository ledgerRepository = Mockito.mock(LedgerRepository.class);
        final FindBalanceHandler applicationHandler = new FindBalanceHandler(ledgerRepository);

        final Balance balance = Balance.restore(
                "acc-001",
                CurrencyCode.of("USD"),
                usd("100.0000"),
                usd("80.0000"),
                Instant.parse("2026-04-28T10:00:00Z"));
        when(ledgerRepository.findBalance(eq("acc-001"), eq(CurrencyCode.of("USD"))))
                .thenReturn(Optional.of(balance));

        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final ShowBalanceCliHandler handler = new ShowBalanceCliHandler(
                applicationHandler,
                new PrintStream(buffer, true, StandardCharsets.UTF_8));

        handler.handle(new ParsedCommand(
                "ledger balance --account-id acc-001 --currency usd",
                List.of("ledger", "balance"),
                Map.of("account-id", "acc-001", "currency", "usd")));

        final String output = buffer.toString(StandardCharsets.UTF_8);
        assertEquals(
                "account_id,currency_code,ledger_balance,available_balance,updated_at\n"
                        + "acc-001,USD,100.0000,80.0000,2026-04-28T10:00:00Z\n",
                output);
    }

    /**
     * @brief 验证余额未命中时输出 not found 提示；
     *        Verify not-found message is printed when balance is missing.
     */
    @Test
    void shouldPrintNotFoundWhenBalanceMissing() {
        final LedgerRepository ledgerRepository = Mockito.mock(LedgerRepository.class);
        final FindBalanceHandler applicationHandler = new FindBalanceHandler(ledgerRepository);
        when(ledgerRepository.findBalance(eq("acc-404"), eq(CurrencyCode.of("USD"))))
                .thenReturn(Optional.empty());

        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final ShowBalanceCliHandler handler = new ShowBalanceCliHandler(
                applicationHandler,
                new PrintStream(buffer, true, StandardCharsets.UTF_8));

        handler.handle(new ParsedCommand(
                "ledger balance --account_id acc-404 --currency_code USD",
                List.of("ledger", "balance"),
                Map.of("account_id", "acc-404", "currency_code", "USD")));

        final String output = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("ledger_balance_not_found account_id=acc-404 currency_code=USD"));
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
