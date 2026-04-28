package com.moesegfault.banking.presentation.cli.account;

import com.moesegfault.banking.application.account.command.FreezeAccountCommand;
import com.moesegfault.banking.application.account.command.FreezeAccountHandler;
import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.Objects;

/**
 * @brief 冻结账户 CLI 处理器（Freeze Account CLI Handler），处理 `account freeze` 命令；
 *        CLI handler for `account freeze` command.
 */
public final class FreezeAccountCliHandler implements CliCommandHandler {

    /**
     * @brief 冻结账户应用服务（Freeze Account Application Service）；
     *        Application handler for freezing account.
     */
    private final FreezeAccountHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct freeze-account CLI handler using `System.out`.
     *
     * @param applicationHandler 冻结账户应用服务（Application handler）。
     */
    public FreezeAccountCliHandler(final FreezeAccountHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct freeze-account CLI handler.
     *
     * @param applicationHandler 冻结账户应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public FreezeAccountCliHandler(
            final FreezeAccountHandler applicationHandler,
            final PrintStream output
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for freezing account.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final String accountId = AccountCliOptionReader.requiredOption(
                command,
                "account_id",
                "account-id",
                "accountId");
        final String freezeReason = AccountCliOptionReader.requiredOption(
                command,
                "freeze_reason",
                "freeze-reason",
                "freezeReason");

        final AccountResult result = applicationHandler.handle(new FreezeAccountCommand(accountId, freezeReason));
        AccountCliOutputFormatter.printAccountResult(output, result);
    }
}
