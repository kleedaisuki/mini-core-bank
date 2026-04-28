package com.moesegfault.banking.presentation.cli.ledger;

import com.moesegfault.banking.application.ledger.result.BalanceResult;
import com.moesegfault.banking.application.ledger.result.LedgerEntryResult;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * @brief Ledger CLI 输出格式化器（Ledger CLI Output Formatter），统一 ledger 领域输出 schema；
 *        Ledger CLI output formatter that standardizes ledger-domain output schema.
 */
final class LedgerCliOutputFormatter {

    /**
     * @brief 余额输出表头（Balance Output Header）；
     *        Header row for balance output.
     */
    private static final String BALANCE_HEADER =
            "account_id,currency_code,ledger_balance,available_balance,updated_at";

    /**
     * @brief 分录输出表头（Ledger-Entry Output Header）；
     *        Header row for ledger-entry output.
     */
    private static final String ENTRY_HEADER =
            "entry_id,transaction_id,batch_id,account_id,currency_code,entry_direction,"
                    + "amount,ledger_balance_after,available_balance_after,entry_type,posted_at";

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private LedgerCliOutputFormatter() {
    }

    /**
     * @brief 打印单余额结果（Print Single Balance Result）；
     *        Print one balance result with canonical schema.
     *
     * @param output 打印流（Output stream）。
     * @param result 余额结果（Balance result）。
     */
    public static void printBalanceResult(final PrintStream output, final BalanceResult result) {
        final PrintStream normalizedOutput = Objects.requireNonNull(output, "output must not be null");
        final BalanceResult normalizedResult = Objects.requireNonNull(result, "result must not be null");

        normalizedOutput.println(BALANCE_HEADER);
        normalizedOutput.println(joinCsv(
                normalizedResult.accountId(),
                normalizedResult.currencyCode().value(),
                formatAmount(normalizedResult.ledgerBalance().amount()),
                formatAmount(normalizedResult.availableBalance().amount()),
                formatInstant(normalizedResult.updatedAt())));
    }

    /**
     * @brief 打印分录列表结果（Print Ledger-Entry List Result）；
     *        Print ledger-entry list with canonical schema.
     *
     * @param output  打印流（Output stream）。
     * @param results 分录结果列表（Ledger-entry result list）。
     */
    public static void printEntryList(final PrintStream output, final List<LedgerEntryResult> results) {
        final PrintStream normalizedOutput = Objects.requireNonNull(output, "output must not be null");
        final List<LedgerEntryResult> normalizedResults = List.copyOf(Objects.requireNonNull(results, "results must not be null"));

        normalizedOutput.println("total=" + normalizedResults.size());
        normalizedOutput.println(ENTRY_HEADER);
        for (LedgerEntryResult result : normalizedResults) {
            normalizedOutput.println(joinCsv(
                    result.entryId(),
                    result.transactionId(),
                    normalizeNullable(result.batchId()),
                    result.accountId(),
                    result.currencyCode().value(),
                    result.entryDirection().name(),
                    formatAmount(result.amount().amount()),
                    formatAmountNullable(result.ledgerBalanceAfter() == null ? null : result.ledgerBalanceAfter().amount()),
                    formatAmountNullable(result.availableBalanceAfter() == null
                            ? null
                            : result.availableBalanceAfter().amount()),
                    result.entryType().name(),
                    formatInstant(result.postedAt())));
        }
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
     * @brief 格式化金额（Format Amount）；
     *        Format amount as plain decimal string.
     *
     * @param amount 金额数值（Amount value）。
     * @return 格式化金额（Formatted amount）。
     */
    private static String formatAmount(final BigDecimal amount) {
        return Objects.requireNonNull(amount, "amount must not be null").toPlainString();
    }

    /**
     * @brief 格式化可空金额（Format Nullable Amount）；
     *        Format nullable amount as plain decimal string.
     *
     * @param amount 金额数值（Amount value, nullable）。
     * @return 格式化金额（Formatted amount, empty when null）。
     */
    private static String formatAmountNullable(final BigDecimal amount) {
        return amount == null ? "" : amount.toPlainString();
    }

    /**
     * @brief 格式化时间（Format Instant）；
     *        Format instant as ISO-8601 string.
     *
     * @param instant 时间戳（Instant）。
     * @return 格式化字符串（Formatted string）。
     */
    private static String formatInstant(final Instant instant) {
        return Objects.requireNonNull(instant, "instant must not be null").toString();
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
