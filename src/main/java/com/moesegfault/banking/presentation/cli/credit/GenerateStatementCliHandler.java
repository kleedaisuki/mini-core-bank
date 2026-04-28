package com.moesegfault.banking.presentation.cli.credit;

import com.moesegfault.banking.application.credit.command.GenerateStatementCommand;
import com.moesegfault.banking.application.credit.command.GenerateStatementHandler;
import com.moesegfault.banking.application.credit.result.CreditCardStatementResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @brief 生成账单 CLI 处理器（Generate Statement CLI Handler），处理 `credit generate-statement` 命令；
 *        CLI handler for `credit generate-statement` command.
 */
public final class GenerateStatementCliHandler implements CliCommandHandler {

    /**
     * @brief 生成账单应用服务（Generate Statement Application Service）；
     *        Application handler for generating statement.
     */
    private final GenerateStatementHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct generate-statement CLI handler using `System.out`.
     *
     * @param applicationHandler 生成账单应用服务（Application handler）。
     */
    public GenerateStatementCliHandler(final GenerateStatementHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct generate-statement CLI handler.
     *
     * @param applicationHandler 生成账单应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public GenerateStatementCliHandler(
            final GenerateStatementHandler applicationHandler,
            final PrintStream output
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for statement generation.
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
        final LocalDate statementDate = CreditCliOptionReader.requiredDate(
                command,
                "statement_date",
                "statement-date",
                "statementDate");
        final BigDecimal minimumPaymentRateDecimal = CreditCliOptionReader.requiredDecimal(
                command,
                "minimum_payment_rate_decimal",
                "minimum-payment-rate-decimal",
                "minimum_payment_rate",
                "minimum-payment-rate",
                "minimumPaymentRateDecimal");
        final BigDecimal minimumPaymentFloorAmount = CreditCliOptionReader.requiredDecimal(
                command,
                "minimum_payment_floor_amount",
                "minimum-payment-floor-amount",
                "minimum_payment_floor",
                "minimum-payment-floor",
                "minimumPaymentFloorAmount");

        final CreditCardStatementResult result = applicationHandler.handle(new GenerateStatementCommand(
                creditCardAccountId,
                statementDate,
                minimumPaymentRateDecimal,
                minimumPaymentFloorAmount));
        CreditCliOutputFormatter.printStatementResult(output, result);
    }
}
