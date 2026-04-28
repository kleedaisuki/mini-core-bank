package com.moesegfault.banking.presentation.cli.ledger;

import com.moesegfault.banking.application.ledger.query.FindBalanceHandler;
import com.moesegfault.banking.application.ledger.query.FindBalanceQuery;
import com.moesegfault.banking.application.ledger.result.BalanceResult;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 余额查询 CLI 处理器（Show Balance CLI Handler），处理 `ledger balance` 命令；
 *        CLI handler for `ledger balance` command.
 */
public final class ShowBalanceCliHandler implements CliCommandHandler {

    /**
     * @brief 查询余额应用服务（Find Balance Application Service）；
     *        Application handler for querying one account balance.
     */
    private final FindBalanceHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct show-balance CLI handler using `System.out`.
     *
     * @param applicationHandler 查询余额应用服务（Application handler）。
     */
    public ShowBalanceCliHandler(final FindBalanceHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct show-balance CLI handler.
     *
     * @param applicationHandler 查询余额应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public ShowBalanceCliHandler(
            final FindBalanceHandler applicationHandler,
            final PrintStream output
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for balance query.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final String accountId = LedgerCliOptionReader.requiredOption(
                command,
                "account_id",
                "account-id",
                "accountId");
        final String currencyCodeValue = LedgerCliOptionReader.requiredOption(
                command,
                "currency_code",
                "currency-code",
                "currencyCode",
                "currency",
                "ccy");
        final CurrencyCode currencyCode = CurrencyCode.of(currencyCodeValue);

        final Optional<BalanceResult> maybeResult = applicationHandler.handle(new FindBalanceQuery(accountId, currencyCode));
        if (maybeResult.isEmpty()) {
            output.printf(
                    Locale.ROOT,
                    "ledger_balance_not_found account_id=%s currency_code=%s%n",
                    accountId,
                    currencyCode.value());
            return;
        }

        LedgerCliOutputFormatter.printBalanceResult(output, maybeResult.orElseThrow());
    }
}
