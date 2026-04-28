package com.moesegfault.banking.presentation.cli.card;

import com.moesegfault.banking.application.card.command.IssueCreditCardCommand;
import com.moesegfault.banking.application.card.command.IssueCreditCardHandler;
import com.moesegfault.banking.application.card.result.IssueCardResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.Objects;

/**
 * @brief 主信用卡发卡 CLI 处理器（Issue Credit Card CLI Handler），处理 `card issue-credit` 命令；
 *        CLI handler for `card issue-credit` command.
 */
public final class IssueCreditCardCliHandler implements CliCommandHandler {

    /**
     * @brief 主信用卡发卡应用服务（Issue Credit Card Application Service）；
     *        Application handler for issuing credit card.
     */
    private final IssueCreditCardHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct issue-credit-card CLI handler using `System.out`.
     *
     * @param applicationHandler 主信用卡发卡应用服务（Application handler）。
     */
    public IssueCreditCardCliHandler(final IssueCreditCardHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct issue-credit-card CLI handler.
     *
     * @param applicationHandler 主信用卡发卡应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public IssueCreditCardCliHandler(final IssueCreditCardHandler applicationHandler, final PrintStream output) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for credit-card issuance.
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
        final String creditCardAccountId = CardCliOptionReader.requiredOption(
                command,
                "credit_card_account_id",
                "credit-card-account-id",
                "creditCardAccountId",
                "account_id",
                "account-id");
        final String cardNo = CardCliOptionReader.requiredOption(
                command,
                "card_no",
                "card-no",
                "cardNo");

        final IssueCardResult result = applicationHandler.handle(new IssueCreditCardCommand(
                holderCustomerId,
                creditCardAccountId,
                cardNo));
        CardCliOutputFormatter.printIssueResult(output, result);
    }
}
