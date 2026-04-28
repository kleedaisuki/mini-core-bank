package com.moesegfault.banking.presentation.cli.account;

import com.moesegfault.banking.application.account.query.FindAccountHandler;
import com.moesegfault.banking.application.account.query.FindAccountQuery;
import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.Objects;

/**
 * @brief 查询账户 CLI 处理器（Show Account CLI Handler），处理 `account show` 命令；
 *        CLI handler for `account show` command.
 */
public final class ShowAccountCliHandler implements CliCommandHandler {

    /**
     * @brief 查询单账户应用服务（Find Account Application Service）；
     *        Application handler for finding one account.
     */
    private final FindAccountHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct show-account CLI handler using `System.out`.
     *
     * @param applicationHandler 查询单账户应用服务（Application handler）。
     */
    public ShowAccountCliHandler(final FindAccountHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct show-account CLI handler.
     *
     * @param applicationHandler 查询单账户应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public ShowAccountCliHandler(
            final FindAccountHandler applicationHandler,
            final PrintStream output
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for account query.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final String accountId = AccountCliOptionReader.optionalOption(
                command,
                "account_id",
                "account-id",
                "accountId");
        final String accountNo = AccountCliOptionReader.optionalOption(
                command,
                "account_no",
                "account-no",
                "accountNo");

        final boolean hasAccountId = accountId != null;
        final boolean hasAccountNo = accountNo != null;
        if (hasAccountId == hasAccountNo) {
            throw new IllegalArgumentException("Exactly one of --account_id or --account_no must be provided");
        }

        final FindAccountQuery query = hasAccountId
                ? FindAccountQuery.byAccountId(accountId)
                : FindAccountQuery.byAccountNo(accountNo);
        final AccountResult result = applicationHandler.handle(query);
        AccountCliOutputFormatter.printAccountResult(output, result);
    }
}
