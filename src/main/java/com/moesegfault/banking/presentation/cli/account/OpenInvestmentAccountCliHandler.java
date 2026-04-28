package com.moesegfault.banking.presentation.cli.account;

import com.moesegfault.banking.application.account.command.OpenInvestmentAccountCommand;
import com.moesegfault.banking.application.account.command.OpenInvestmentAccountHandler;
import com.moesegfault.banking.application.account.result.OpenAccountResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.Objects;

/**
 * @brief 投资开户 CLI 处理器（Open Investment Account CLI Handler），处理 `account open-investment` 命令；
 *        CLI handler for `account open-investment` command.
 */
public final class OpenInvestmentAccountCliHandler implements CliCommandHandler {

    /**
     * @brief 开投资账户应用服务（Open Investment Account Application Service）；
     *        Application handler for opening investment account.
     */
    private final OpenInvestmentAccountHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct open-investment CLI handler using `System.out`.
     *
     * @param applicationHandler 开投资账户应用服务（Application handler）。
     */
    public OpenInvestmentAccountCliHandler(final OpenInvestmentAccountHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct open-investment CLI handler.
     *
     * @param applicationHandler 开投资账户应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public OpenInvestmentAccountCliHandler(
            final OpenInvestmentAccountHandler applicationHandler,
            final PrintStream output
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for opening investment account.
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

        final OpenAccountResult result = applicationHandler.handle(new OpenInvestmentAccountCommand(customerId, accountNo));
        AccountCliOutputFormatter.printOpenAccountResult(output, result);
    }
}
