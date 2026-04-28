package com.moesegfault.banking.presentation.cli.account;

import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.application.account.result.OpenAccountResult;
import java.io.PrintStream;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * @brief Account CLI 输出格式化器（Account CLI Output Formatter），统一 account 领域输出 schema；
 *        Account CLI output formatter that standardizes account-domain output schema.
 */
final class AccountCliOutputFormatter {

    /**
     * @brief 账户输出表头（Account Output Header）；
     *        Header row for account output.
     */
    private static final String ACCOUNT_HEADER =
            "account_id,customer_id,account_no,account_type,account_status,opened_at,closed_at,linked_savings_account_id";

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private AccountCliOutputFormatter() {
    }

    /**
     * @brief 打印开户结果（Print Open-Account Result）；
     *        Print open-account result with canonical schema.
     *
     * @param output 打印流（Output stream）。
     * @param result 开户结果（Open-account result）。
     */
    public static void printOpenAccountResult(final PrintStream output, final OpenAccountResult result) {
        final PrintStream normalizedOutput = Objects.requireNonNull(output, "output must not be null");
        final OpenAccountResult normalizedResult = Objects.requireNonNull(result, "result must not be null");

        normalizedOutput.println(ACCOUNT_HEADER);
        normalizedOutput.println(joinCsv(
                normalizedResult.accountId(),
                normalizedResult.customerId(),
                normalizedResult.accountNo(),
                normalizedResult.accountType(),
                normalizedResult.accountStatus(),
                formatInstant(normalizedResult.openedAt()),
                "",
                normalizeNullable(normalizedResult.linkedSavingsAccountId())));
    }

    /**
     * @brief 打印单账户结果（Print Single Account Result）；
     *        Print one account result with canonical schema.
     *
     * @param output 打印流（Output stream）。
     * @param result 账户结果（Account result）。
     */
    public static void printAccountResult(final PrintStream output, final AccountResult result) {
        final PrintStream normalizedOutput = Objects.requireNonNull(output, "output must not be null");
        final AccountResult normalizedResult = Objects.requireNonNull(result, "result must not be null");

        normalizedOutput.println(ACCOUNT_HEADER);
        normalizedOutput.println(joinCsv(
                normalizedResult.accountId(),
                normalizedResult.customerId(),
                normalizedResult.accountNo(),
                normalizedResult.accountType(),
                normalizedResult.accountStatus(),
                formatInstant(normalizedResult.openedAt()),
                formatInstantNullable(normalizedResult.closedAt()),
                normalizeNullable(normalizedResult.linkedSavingsAccountId())));
    }

    /**
     * @brief 打印账户列表结果（Print Account List Result）；
     *        Print account list result with canonical schema.
     *
     * @param output  打印流（Output stream）。
     * @param results 账户结果列表（Account-result list）。
     */
    public static void printAccountList(final PrintStream output, final List<AccountResult> results) {
        final PrintStream normalizedOutput = Objects.requireNonNull(output, "output must not be null");
        final List<AccountResult> normalizedResults = List.copyOf(Objects.requireNonNull(results, "results must not be null"));

        normalizedOutput.println("total=" + normalizedResults.size());
        normalizedOutput.println(ACCOUNT_HEADER);
        for (AccountResult result : normalizedResults) {
            normalizedOutput.println(joinCsv(
                    result.accountId(),
                    result.customerId(),
                    result.accountNo(),
                    result.accountType(),
                    result.accountStatus(),
                    formatInstant(result.openedAt()),
                    formatInstantNullable(result.closedAt()),
                    normalizeNullable(result.linkedSavingsAccountId())));
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
     * @brief 格式化可空时间（Format Nullable Instant）；
     *        Format nullable instant as string.
     *
     * @param instant 时间戳（Instant, nullable）。
     * @return 格式化字符串（Formatted string, empty when null）。
     */
    private static String formatInstantNullable(final Instant instant) {
        return instant == null ? "" : instant.toString();
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
