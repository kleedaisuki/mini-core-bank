package com.moesegfault.banking.presentation.cli.account;

import com.moesegfault.banking.application.account.command.OpenFxAccountCommand;
import com.moesegfault.banking.application.account.command.OpenFxAccountHandler;
import com.moesegfault.banking.application.account.result.OpenAccountResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.Objects;

/**
 * @brief 外汇开户 CLI 处理器（Open FX Account CLI Handler），处理 `account open-fx` 命令；
 *        CLI handler for `account open-fx` command.
 */
public final class OpenFxAccountCliHandler implements CliCommandHandler {

    /**
     * @brief 开外汇账户应用服务（Open FX Account Application Service）；
     *        Application handler for opening FX account.
     */
    private final OpenFxAccountHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct open-FX CLI handler using `System.out`.
     *
     * @param applicationHandler 开外汇账户应用服务（Application handler）。
     */
    public OpenFxAccountCliHandler(final OpenFxAccountHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct open-FX CLI handler.
     *
     * @param applicationHandler 开外汇账户应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public OpenFxAccountCliHandler(
            final OpenFxAccountHandler applicationHandler,
            final PrintStream output
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for opening FX account.
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
        final String linkedSavingsAccountId = AccountCliOptionReader.requiredOption(
                command,
                "linked_savings_account_id",
                "linked-savings-account-id",
                "linkedSavingsAccountId");

        final OpenAccountResult result = applicationHandler.handle(
                new OpenFxAccountCommand(customerId, accountNo, linkedSavingsAccountId));
        AccountCliOutputFormatter.printOpenAccountResult(output, result);
    }
}
