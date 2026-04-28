package com.moesegfault.banking.presentation.cli.account;

import com.moesegfault.banking.application.account.command.OpenSavingsAccountCommand;
import com.moesegfault.banking.application.account.command.OpenSavingsAccountHandler;
import com.moesegfault.banking.application.account.result.OpenAccountResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.Objects;

/**
 * @brief 储蓄开户 CLI 处理器（Open Savings Account CLI Handler），处理 `account open-savings` 命令；
 *        CLI handler for `account open-savings` command.
 */
public final class OpenSavingsAccountCliHandler implements CliCommandHandler {

    /**
     * @brief 开储蓄账户应用服务（Open Savings Account Application Service）；
     *        Application handler for opening savings account.
     */
    private final OpenSavingsAccountHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct open-savings CLI handler using `System.out`.
     *
     * @param applicationHandler 开储蓄账户应用服务（Application handler）。
     */
    public OpenSavingsAccountCliHandler(final OpenSavingsAccountHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct open-savings CLI handler.
     *
     * @param applicationHandler 开储蓄账户应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public OpenSavingsAccountCliHandler(
            final OpenSavingsAccountHandler applicationHandler,
            final PrintStream output
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for opening savings account.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final String customerId = AccountCliOptionReader.requiredOption(
                command,
                "customer_id",
                "customer-id",
                "customerId");
        final String accountNo = AccountCliOptionReader.requiredOption(
                command,
                "account_no",
                "account-no",
                "accountNo");

        final OpenAccountResult result = applicationHandler.handle(new OpenSavingsAccountCommand(customerId, accountNo));
        AccountCliOutputFormatter.printOpenAccountResult(output, result);
    }
}
