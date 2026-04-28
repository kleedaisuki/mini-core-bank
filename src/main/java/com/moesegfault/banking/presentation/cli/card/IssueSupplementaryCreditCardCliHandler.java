package com.moesegfault.banking.presentation.cli.card;

import com.moesegfault.banking.application.card.command.IssueSupplementaryCreditCardCommand;
import com.moesegfault.banking.application.card.command.IssueSupplementaryCreditCardHandler;
import com.moesegfault.banking.application.card.result.IssueCardResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.Objects;

/**
 * @brief 信用附属卡发卡 CLI 处理器（Issue Supplementary Credit Card CLI Handler），处理 `card issue-supplementary-credit` 命令；
 *        CLI handler for `card issue-supplementary-credit` command.
 */
public final class IssueSupplementaryCreditCardCliHandler implements CliCommandHandler {

    /**
     * @brief 信用附属卡发卡应用服务（Issue Supplementary Credit Card Application Service）；
     *        Application handler for issuing supplementary credit card.
     */
    private final IssueSupplementaryCreditCardHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct issue-supplementary-credit-card CLI handler using `System.out`.
     *
     * @param applicationHandler 信用附属卡发卡应用服务（Application handler）。
     */
    public IssueSupplementaryCreditCardCliHandler(final IssueSupplementaryCreditCardHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct issue-supplementary-credit-card CLI handler.
     *
     * @param applicationHandler 信用附属卡发卡应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public IssueSupplementaryCreditCardCliHandler(
            final IssueSupplementaryCreditCardHandler applicationHandler,
            final PrintStream output
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for supplementary credit-card issuance.
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
        final String primaryCreditCardId = CardCliOptionReader.requiredOption(
                command,
                "primary_credit_card_id",
                "primary-credit-card-id",
                "primaryCreditCardId");
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

        final IssueCardResult result = applicationHandler.handle(new IssueSupplementaryCreditCardCommand(
                holderCustomerId,
                primaryCreditCardId,
                creditCardAccountId,
                cardNo));
        CardCliOutputFormatter.printIssueResult(output, result);
    }
}
