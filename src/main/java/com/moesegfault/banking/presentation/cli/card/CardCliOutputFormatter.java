package com.moesegfault.banking.presentation.cli.card;

import com.moesegfault.banking.application.card.result.CardResult;
import com.moesegfault.banking.application.card.result.IssueCardResult;
import java.io.PrintStream;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief Card CLI 输出格式化器（Card CLI Output Formatter），统一 card 领域输出 schema；
 *        Card CLI output formatter that standardizes card-domain output schema.
 */
final class CardCliOutputFormatter {

    /**
     * @brief 发卡结果输出表头（Issue Result Output Header）；
     *        Header row for card-issue result output.
     */
    private static final String ISSUE_RESULT_HEADER =
            "card_id,masked_card_number,card_kind,card_status,holder_customer_id,issued_at,primary_card_id";

    /**
     * @brief 卡详情输出表头（Card Detail Output Header）；
     *        Header row for card-detail output.
     */
    private static final String CARD_DETAIL_HEADER =
            "card_id,masked_card_number,holder_customer_id,card_status,card_kind,issued_at,expired_at,"
                    + "savings_account_id,fx_account_id,credit_card_account_id,primary_card_id";

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private CardCliOutputFormatter() {
    }

    /**
     * @brief 打印发卡结果（Print Issue Result）；
     *        Print card-issue result with canonical schema.
     *
     * @param output 输出流（Output stream）。
     * @param result 发卡结果（Card-issue result）。
     */
    public static void printIssueResult(final PrintStream output, final IssueCardResult result) {
        final PrintStream normalizedOutput = Objects.requireNonNull(output, "output must not be null");
        final IssueCardResult normalizedResult = Objects.requireNonNull(result, "result must not be null");

        normalizedOutput.println(ISSUE_RESULT_HEADER);
        normalizedOutput.println(joinCsv(
                normalizedResult.cardId(),
                normalizedResult.maskedCardNumber(),
                normalizedResult.cardKind().name(),
                normalizedResult.cardStatus(),
                normalizedResult.holderCustomerId(),
                formatInstant(normalizedResult.issuedAt()),
                normalizeNullable(normalizedResult.primaryCardIdOrNull())));
    }

    /**
     * @brief 打印卡详情结果（Print Card Detail Result）；
     *        Print card-detail result with canonical schema.
     *
     * @param output 输出流（Output stream）。
     * @param result 卡详情结果（Card-detail result）。
     */
    public static void printCardResult(final PrintStream output, final CardResult result) {
        final PrintStream normalizedOutput = Objects.requireNonNull(output, "output must not be null");
        final CardResult normalizedResult = Objects.requireNonNull(result, "result must not be null");

        normalizedOutput.println(CARD_DETAIL_HEADER);
        normalizedOutput.println(joinCsv(
                normalizedResult.cardId(),
                normalizedResult.maskedCardNumber(),
                normalizedResult.holderCustomerId(),
                normalizedResult.cardStatus(),
                normalizedResult.cardKind().name(),
                formatInstant(normalizedResult.issuedAt()),
                formatInstantNullable(normalizedResult.expiredAtOrNull()),
                normalizeNullable(normalizedResult.savingsAccountIdOrNull()),
                normalizeNullable(normalizedResult.fxAccountIdOrNull()),
                normalizeNullable(normalizedResult.creditCardAccountIdOrNull()),
                normalizeNullable(normalizedResult.primaryCardIdOrNull())));
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
