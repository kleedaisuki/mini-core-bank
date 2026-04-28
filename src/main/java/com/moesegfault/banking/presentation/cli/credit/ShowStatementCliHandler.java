package com.moesegfault.banking.presentation.cli.credit;

import com.moesegfault.banking.application.credit.query.FindStatementHandler;
import com.moesegfault.banking.application.credit.query.FindStatementQuery;
import com.moesegfault.banking.application.credit.result.CreditCardStatementResult;
import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 查询账单 CLI 处理器（Show Statement CLI Handler），处理 `credit statement` 命令；
 *        CLI handler for `credit statement` command.
 */
public final class ShowStatementCliHandler implements CliCommandHandler {

    /**
     * @brief 查询账单应用服务（Find Statement Application Service）；
     *        Application handler for finding one statement.
     */
    private final FindStatementHandler applicationHandler;

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream for CLI response.
     */
    private final PrintStream output;

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct show-statement CLI handler using `System.out`.
     *
     * @param applicationHandler 查询账单应用服务（Application handler）。
     */
    public ShowStatementCliHandler(final FindStatementHandler applicationHandler) {
        this(applicationHandler, System.out);
    }

    /**
     * @brief 构造 CLI 处理器（Construct CLI Handler）；
     *        Construct show-statement CLI handler.
     *
     * @param applicationHandler 查询账单应用服务（Application handler）。
     * @param output             输出流（Output stream）。
     */
    public ShowStatementCliHandler(
            final FindStatementHandler applicationHandler,
            final PrintStream output
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 执行命令（Handle Command）；
     *        Handle parsed CLI command for statement query.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final String statementId = CreditCliOptionReader.optionalOption(
                command,
                "statement_id",
                "statement-id");
        final String creditCardAccountId = CreditCliOptionReader.optionalOption(
                command,
                "credit_card_account_id",
                "credit-card-account-id",
                "creditCardAccountId",
                "account_id",
                "account-id");
        final LocalDate statementPeriodStart = CreditCliOptionReader.optionalDate(
                command,
                "statement_period_start",
                "statement-period-start",
                "statementPeriodStart");
        final LocalDate statementPeriodEnd = CreditCliOptionReader.optionalDate(
                command,
                "statement_period_end",
                "statement-period-end",
                "statementPeriodEnd");

        final boolean hasStatementId = statementId != null;
        final boolean hasPeriodTuple = creditCardAccountId != null
                && statementPeriodStart != null
                && statementPeriodEnd != null;
        if (hasStatementId == hasPeriodTuple) {
            throw new IllegalArgumentException(
                    "Exactly one mode is allowed: --statement_id OR (--credit_card_account_id, --statement_period_start, --statement_period_end)");
        }

        final Optional<CreditCardStatementResult> result = hasStatementId
                ? applicationHandler.handle(FindStatementQuery.byStatementId(statementId))
                : applicationHandler.handle(FindStatementQuery.byPeriod(
                        creditCardAccountId,
                        statementPeriodStart,
                        statementPeriodEnd));
        if (result.isEmpty()) {
            printNotFound(statementId, creditCardAccountId, statementPeriodStart, statementPeriodEnd);
            return;
        }

        CreditCliOutputFormatter.printStatementResult(output, result.orElseThrow());
    }

    /**
     * @brief 打印未找到提示（Print Not-found Message）；
     *        Print not-found message according to query mode.
     *
     * @param statementId         账单 ID（Statement ID）。
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @param statementPeriodStart 账期开始日期（Statement period start）。
     * @param statementPeriodEnd 账期结束日期（Statement period end）。
     */
    private void printNotFound(
            final String statementId,
            final String creditCardAccountId,
            final LocalDate statementPeriodStart,
            final LocalDate statementPeriodEnd
    ) {
        if (statementId != null) {
            output.printf(Locale.ROOT, "statement_not_found statement_id=%s%n", statementId);
            return;
        }

        output.printf(
                Locale.ROOT,
                "statement_not_found credit_card_account_id=%s statement_period_start=%s statement_period_end=%s%n",
                creditCardAccountId,
                statementPeriodStart,
                statementPeriodEnd);
    }
}
