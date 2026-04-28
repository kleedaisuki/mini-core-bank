package com.moesegfault.banking.presentation.cli.credit;

import com.moesegfault.banking.application.credit.command.RepayCreditCardCommand;
import com.moesegfault.banking.application.credit.command.RepayCreditCardHandler;
import com.moesegfault.banking.application.credit.result.RepayCreditCardResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @brief 信用卡还款 CLI 处理器（Repay Credit Card CLI Handler），处理 `credit repay` 命令；
 *        CLI handler for `credit repay` command.
 */
public final class RepayCreditCardCliHandler implements CliCommandHandler {

    /**
     * @brief 信用卡还款应用服务（Repay Credit Card Application Service）；
     *        Application handler for credit-card repayment.
     */
    private final RepayCreditCardHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct repay-credit-card CLI handler using `System.out`.
     *
     * @param applicationHandler 信用卡还款应用服务（Application handler）。
     */
    public RepayCreditCardCliHandler(final RepayCreditCardHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct repay-credit-card CLI handler.
     *
     * @param applicationHandler 信用卡还款应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public RepayCreditCardCliHandler(
            final RepayCreditCardHandler applicationHandler,
            final PrintStream output
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for credit-card repayment.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final String creditCardAccountId = CreditCliOptionReader.requiredOption(
                command,
                "credit_card_account_id",
                "credit-card-account-id",
                "creditCardAccountId",
                "account_id",
                "account-id");
        final BigDecimal repaymentAmount = CreditCliOptionReader.requiredDecimal(
                command,
                "repayment_amount",
                "repayment-amount",
                "amount");
        final String repaymentCurrencyCode = CreditCliOptionReader.requiredOption(
                command,
                "repayment_currency_code",
                "repayment-currency-code",
                "repayment_currency",
                "repayment-currency",
                "currency_code",
                "currency-code",
                "currency");
        final String statementIdOrNull = CreditCliOptionReader.optionalOption(
                command,
                "statement_id",
                "statement-id");
        final String sourceAccountIdOrNull = CreditCliOptionReader.optionalOption(
                command,
                "source_account_id",
                "source-account-id");
        final LocalDate asOfDateOrNull = CreditCliOptionReader.optionalDate(
                command,
                "as_of_date",
                "as-of-date",
                "asOfDate");

        final RepayCreditCardResult result = applicationHandler.handle(new RepayCreditCardCommand(
                creditCardAccountId,
                repaymentAmount,
                repaymentCurrencyCode,
                statementIdOrNull,
                sourceAccountIdOrNull,
                asOfDateOrNull));
        CreditCliOutputFormatter.printRepayResult(output, result);
    }
}
