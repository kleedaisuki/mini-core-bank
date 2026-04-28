package com.moesegfault.banking.presentation.cli.account;

import com.moesegfault.banking.application.account.query.ListCustomerAccountsHandler;
import com.moesegfault.banking.application.account.query.ListCustomerAccountsQuery;
import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

/**
 * @brief 列出客户账户 CLI 处理器（List Accounts CLI Handler），处理 `account list` 命令；
 *        CLI handler for `account list` command.
 */
public final class ListAccountsCliHandler implements CliCommandHandler {

    /**
     * @brief 列表查询应用服务（List Customer Accounts Application Service）；
     *        Application handler for listing customer accounts.
     */
    private final ListCustomerAccountsHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct list-accounts CLI handler using `System.out`.
     *
     * @param applicationHandler 列表查询应用服务（Application handler）。
     */
    public ListAccountsCliHandler(final ListCustomerAccountsHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct list-accounts CLI handler.
     *
     * @param applicationHandler 列表查询应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public ListAccountsCliHandler(
            final ListCustomerAccountsHandler applicationHandler,
            final PrintStream output
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for listing customer accounts.
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
        final boolean includeClosedAccounts = AccountCliOptionReader.optionalBoolean(
                command,
                "include_closed_accounts",
                false,
                "include-closed-accounts",
                "includeClosedAccounts");

        final List<AccountResult> results = applicationHandler.handle(
                new ListCustomerAccountsQuery(customerId, includeClosedAccounts));
        AccountCliOutputFormatter.printAccountList(output, results);
    }
}
