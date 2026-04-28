package com.moesegfault.banking.presentation.cli.card;

import com.moesegfault.banking.application.card.command.IssueSupplementaryDebitCardCommand;
import com.moesegfault.banking.application.card.command.IssueSupplementaryDebitCardHandler;
import com.moesegfault.banking.application.card.result.IssueCardResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.util.Objects;

/**
 * @brief 借记附属卡发卡 CLI 处理器（Issue Supplementary Debit Card CLI Handler），处理 `card issue-supplementary-debit` 命令；
 *        CLI handler for `card issue-supplementary-debit` command.
 */
public final class IssueSupplementaryDebitCardCliHandler implements CliCommandHandler {

    /**
     * @brief 借记附属卡发卡应用服务（Issue Supplementary Debit Card Application Service）；
     *        Application handler for issuing supplementary debit card.
     */
    private final IssueSupplementaryDebitCardHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct issue-supplementary-debit-card CLI handler using `System.out`.
     *
     * @param applicationHandler 借记附属卡发卡应用服务（Application handler）。
     */
    public IssueSupplementaryDebitCardCliHandler(final IssueSupplementaryDebitCardHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct issue-supplementary-debit-card CLI handler.
     *
     * @param applicationHandler 借记附属卡发卡应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public IssueSupplementaryDebitCardCliHandler(
            final IssueSupplementaryDebitCardHandler applicationHandler,
            final PrintStream output
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for supplementary debit-card issuance.
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
        final String primaryDebitCardId = CardCliOptionReader.requiredOption(
                command,
                "primary_debit_card_id",
                "primary-debit-card-id",
                "primaryDebitCardId");
        final String cardNo = CardCliOptionReader.requiredOption(
                command,
                "card_no",
                "card-no",
                "cardNo");

        final IssueCardResult result = applicationHandler.handle(new IssueSupplementaryDebitCardCommand(
                holderCustomerId,
                primaryDebitCardId,
                cardNo));
        CardCliOutputFormatter.printIssueResult(output, result);
    }
}
