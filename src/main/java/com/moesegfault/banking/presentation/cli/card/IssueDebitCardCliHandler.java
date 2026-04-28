package com.moesegfault.banking.presentation.cli.card;

import com.moesegfault.banking.application.card.command.IssueDebitCardCommand;
import com.moesegfault.banking.application.card.command.IssueDebitCardHandler;
import com.moesegfault.banking.application.card.result.IssueCardResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.Objects;

/**
 * @brief 主借记卡发卡 CLI 处理器（Issue Debit Card CLI Handler），处理 `card issue-debit` 命令；
 *        CLI handler for `card issue-debit` command.
 */
public final class IssueDebitCardCliHandler implements CliCommandHandler {

    /**
     * @brief 主借记卡发卡应用服务（Issue Debit Card Application Service）；
     *        Application handler for issuing debit card.
     */
    private final IssueDebitCardHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct issue-debit-card CLI handler using `System.out`.
     *
     * @param applicationHandler 主借记卡发卡应用服务（Application handler）。
     */
    public IssueDebitCardCliHandler(final IssueDebitCardHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct issue-debit-card CLI handler.
     *
     * @param applicationHandler 主借记卡发卡应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public IssueDebitCardCliHandler(final IssueDebitCardHandler applicationHandler, final PrintStream output) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for debit-card issuance.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final String holderCustomerId = CardCliOptionReader.requiredOption(
                command,
                "holder_customer_id",
                "holder-customer-id",
                "holderCustomerId");
        final String savingsAccountId = CardCliOptionReader.requiredOption(
                command,
                "savings_account_id",
                "savings-account-id",
                "savingsAccountId");
        final String fxAccountId = CardCliOptionReader.requiredOption(
                command,
                "fx_account_id",
                "fx-account-id",
                "fxAccountId");
        final String cardNo = CardCliOptionReader.requiredOption(
                command,
                "card_no",
                "card-no",
                "cardNo");

        final IssueCardResult result = applicationHandler.handle(new IssueDebitCardCommand(
                holderCustomerId,
                savingsAccountId,
                fxAccountId,
                cardNo));
        CardCliOutputFormatter.printIssueResult(output, result);
    }
}
