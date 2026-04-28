package com.moesegfault.banking.presentation.cli.credit;

import com.moesegfault.banking.application.credit.result.CreditCardAccountResult;
import com.moesegfault.banking.application.credit.result.CreditCardStatementResult;
import com.moesegfault.banking.application.credit.result.RepayCreditCardResult;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @brief Credit CLI 输出格式化器（Credit CLI Output Formatter），统一 credit 领域输出 schema；
 *        Credit CLI output formatter that standardizes credit-domain output schema.
 */
final class CreditCliOutputFormatter {

    /**
     * @brief 账单输出表头（Statement Output Header）；
     *        Header row for statement output.
     */
    private static final String STATEMENT_HEADER =
            "statement_id,credit_card_account_id,statement_period_start,statement_period_end,statement_date,"
                    + "payment_due_date,total_amount_due,minimum_amount_due,paid_amount,outstanding_amount,"
                    + "statement_status,currency_code";

    /**
     * @brief 信用账户输出表头（Credit Account Output Header）；
     *        Header row for credit-card-account snapshot output.
     */
    private static final String ACCOUNT_HEADER =
            "credit_card_account_id,credit_limit,available_credit,used_credit,cash_advance_limit,billing_cycle_day,"
                    + "payment_due_day,interest_rate_decimal,account_currency_code";

    /**
     * @brief 还款汇总输出表头（Repayment Summary Output Header）；
     *        Header row for repayment summary output.
     */
    private static final String REPAYMENT_SUMMARY_HEADER =
            "credit_card_account_id,applied_to_account_amount,applied_to_statement_amount,unapplied_amount,"
                    + "currency_code,target_statement_id";

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private CreditCliOutputFormatter() {
    }

    /**
     * @brief 打印账单结果（Print Statement Result）；
     *        Print one statement result with canonical schema.
     *
     * @param output 输出流（Output stream）。
     * @param result 账单结果（Statement result）。
     */
    public static void printStatementResult(final PrintStream output, final CreditCardStatementResult result) {
        final PrintStream normalizedOutput = Objects.requireNonNull(output, "output must not be null");
        final CreditCardStatementResult normalizedResult = Objects.requireNonNull(result, "result must not be null");

        normalizedOutput.println(STATEMENT_HEADER);
        normalizedOutput.println(joinCsv(
                normalizedResult.statementId(),
                normalizedResult.creditCardAccountId(),
                formatDate(normalizedResult.statementPeriodStart()),
                formatDate(normalizedResult.statementPeriodEnd()),
                formatDate(normalizedResult.statementDate()),
                formatDate(normalizedResult.paymentDueDate()),
                formatDecimal(normalizedResult.totalAmountDue()),
                formatDecimal(normalizedResult.minimumAmountDue()),
                formatDecimal(normalizedResult.paidAmount()),
                formatDecimal(normalizedResult.outstandingAmount()),
                normalizedResult.statementStatus(),
                normalizedResult.currencyCode()));
    }

    /**
     * @brief 打印还款结果（Print Repayment Result）；
     *        Print repayment result with summary, account snapshot, and affected statements.
     *
     * @param output 输出流（Output stream）。
     * @param result 还款结果（Repayment result）。
     */
    public static void printRepayResult(final PrintStream output, final RepayCreditCardResult result) {
        final PrintStream normalizedOutput = Objects.requireNonNull(output, "output must not be null");
        final RepayCreditCardResult normalizedResult = Objects.requireNonNull(result, "result must not be null");

        normalizedOutput.println(REPAYMENT_SUMMARY_HEADER);
        normalizedOutput.println(joinCsv(
                normalizedResult.creditCardAccount().creditCardAccountId(),
                formatDecimal(normalizedResult.appliedToAccountAmount()),
                formatDecimal(normalizedResult.appliedToStatementAmount()),
                formatDecimal(normalizedResult.unappliedAmount()),
                normalizedResult.currencyCode(),
                normalizeNullable(normalizedResult.statementIdOrNull())));

        printCreditCardAccountResult(normalizedOutput, normalizedResult.creditCardAccount());

        final List<CreditCardStatementResult> affectedStatements = normalizedResult.affectedStatements();
        normalizedOutput.println("affected_statements_total=" + affectedStatements.size());
        normalizedOutput.println(STATEMENT_HEADER);
        for (CreditCardStatementResult affectedStatement : affectedStatements) {
            normalizedOutput.println(joinCsv(
                    affectedStatement.statementId(),
                    affectedStatement.creditCardAccountId(),
                    formatDate(affectedStatement.statementPeriodStart()),
                    formatDate(affectedStatement.statementPeriodEnd()),
                    formatDate(affectedStatement.statementDate()),
                    formatDate(affectedStatement.paymentDueDate()),
                    formatDecimal(affectedStatement.totalAmountDue()),
                    formatDecimal(affectedStatement.minimumAmountDue()),
                    formatDecimal(affectedStatement.paidAmount()),
                    formatDecimal(affectedStatement.outstandingAmount()),
                    affectedStatement.statementStatus(),
                    affectedStatement.currencyCode()));
        }
    }

    /**
     * @brief 打印信用账户快照（Print Credit-Card-Account Snapshot）；
     *        Print one credit-card-account snapshot with canonical schema.
     *
     * @param output  输出流（Output stream）。
     * @param account 账户快照（Account snapshot）。
     */
    private static void printCreditCardAccountResult(final PrintStream output, final CreditCardAccountResult account) {
        output.println(ACCOUNT_HEADER);
        output.println(joinCsv(
                account.creditCardAccountId(),
                formatDecimal(account.creditLimit()),
                formatDecimal(account.availableCredit()),
                formatDecimal(account.usedCredit()),
                formatDecimal(account.cashAdvanceLimit()),
                Integer.toString(account.billingCycleDay()),
                Integer.toString(account.paymentDueDay()),
                formatDecimal(account.interestRateDecimal()),
                account.accountCurrencyCode()));
    }

    /**
     * @brief 拼接 CSV 行（Join CSV Row）；
     *        Join fields into one CSV row.
     *
     * @param values 字段值数组（Field values）。
     * @return CSV 行（CSV row）。
     */
    private static String joinCsv(final String... values) {
        final StringBuilder builder = new StringBuilder();
        for (int index = 0; index < values.length; index++) {
            if (index > 0) {
                builder.append(',');
            }
            builder.append(escapeCsv(values[index]));
        }
        return builder.toString();
    }

    /**
     * @brief 转义 CSV 字段（Escape CSV Field）；
     *        Escape one CSV field.
     *
     * @param raw 原始字段值（Raw field value）。
     * @return 转义后字段值（Escaped field value）。
     */
    private static String escapeCsv(final String raw) {
        final String normalized = normalizeNullable(raw);
        if (normalized.indexOf(',') < 0 && normalized.indexOf('"') < 0 && normalized.indexOf('\n') < 0) {
            return normalized;
        }

        final String escaped = normalized.replace("\"", "\"\"");
        return '"' + escaped + '"';
    }

    /**
     * @brief 格式化金额（Format Decimal Amount）；
     *        Format BigDecimal as plain string.
     *
     * @param amount 金额（Amount）。
     * @return 格式化金额（Formatted amount）。
     */
    private static String formatDecimal(final BigDecimal amount) {
        return Objects.requireNonNull(amount, "amount must not be null").toPlainString();
    }

    /**
     * @brief 格式化日期（Format Local Date）；
     *        Format LocalDate as ISO-8601 string.
     *
     * @param date 日期（Date）。
     * @return 格式化日期字符串（Formatted date string）。
     */
    private static String formatDate(final LocalDate date) {
        return Objects.requireNonNull(date, "date must not be null").toString();
    }

    /**
     * @brief 规范化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text by trimming and replacing null with empty string.
     *
     * @param raw 原始值（Raw value）。
     * @return 规范化文本（Normalized text）。
     */
    private static String normalizeNullable(final String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim();
    }
}
