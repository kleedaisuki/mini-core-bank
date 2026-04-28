package com.moesegfault.banking.presentation.cli.ledger;

import com.moesegfault.banking.application.ledger.query.ListLedgerEntriesHandler;
import com.moesegfault.banking.application.ledger.query.ListLedgerEntriesQuery;
import com.moesegfault.banking.application.ledger.result.LedgerEntryResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

/**
 * @brief 分录列表 CLI 处理器（Show Entries CLI Handler），处理 `ledger entries` 命令；
 *        CLI handler for `ledger entries` command.
 */
public final class ShowEntriesCliHandler implements CliCommandHandler {

    /**
     * @brief 查询分录列表应用服务（List Ledger Entries Application Service）；
     *        Application handler for listing ledger entries.
     */
    private final ListLedgerEntriesHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct show-entries CLI handler using `System.out`.
     *
     * @param applicationHandler 查询分录列表应用服务（Application handler）。
     */
    public ShowEntriesCliHandler(final ListLedgerEntriesHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct show-entries CLI handler.
     *
     * @param applicationHandler 查询分录列表应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public ShowEntriesCliHandler(
            final ListLedgerEntriesHandler applicationHandler,
            final PrintStream output
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for ledger-entry list query.
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
        final int limit = LedgerCliOptionReader.optionalInteger(
                command,
                "limit",
                ListLedgerEntriesQuery.DEFAULT_LIMIT,
                "max",
                "size");

        final List<LedgerEntryResult> results = applicationHandler.handle(new ListLedgerEntriesQuery(accountId, limit));
        LedgerCliOutputFormatter.printEntryList(output, results);
    }
}
