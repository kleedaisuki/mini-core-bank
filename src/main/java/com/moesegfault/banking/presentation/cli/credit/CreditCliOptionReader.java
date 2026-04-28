package com.moesegfault.banking.presentation.cli.credit;

import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief Credit CLI 参数读取器（Credit CLI Option Reader），统一 canonical schema 参数读取；
 *        Credit CLI option reader that centralizes canonical-schema option extraction.
 */
final class CreditCliOptionReader {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private CreditCliOptionReader() {
    }

    /**
     * @brief 读取必填参数（Read Required Option）；
     *        Read one required option from canonical name and aliases.
     *
     * @param command       已解析命令（Parsed command）。
     * @param canonicalName 规范参数名（Canonical option name）。
     * @param aliases       参数别名（Option aliases）。
     * @return 参数值（Option value）。
     */
    public static String requiredOption(
            final ParsedCommand command,
            final String canonicalName,
            final String... aliases
    ) {
        return resolveOption(command, canonicalName, aliases).orElseThrow(
                () -> new IllegalArgumentException("Missing required option: --" + normalizeName(canonicalName)));
    }

    /**
     * @brief 读取可选参数（Read Optional Option）；
     *        Read one optional option from canonical name and aliases.
     *
     * @param command       已解析命令（Parsed command）。
     * @param canonicalName 规范参数名（Canonical option name）。
     * @param aliases       参数别名（Option aliases）。
     * @return 参数值（Option value），不存在返回 null。
     */
    public static String optionalOption(
            final ParsedCommand command,
            final String canonicalName,
            final String... aliases
    ) {
        return resolveOption(command, canonicalName, aliases).orElse(null);
    }

    /**
     * @brief 读取必填金额参数（Read Required Decimal Option）；
     *        Read one required decimal option.
     *
     * @param command       已解析命令（Parsed command）。
     * @param canonicalName 规范参数名（Canonical option name）。
     * @param aliases       参数别名（Option aliases）。
     * @return 十进制数值（Decimal value）。
     */
    public static BigDecimal requiredDecimal(
            final ParsedCommand command,
            final String canonicalName,
            final String... aliases
    ) {
        final String raw = requiredOption(command, canonicalName, aliases);
        return parseDecimal(raw, canonicalName);
    }

    /**
     * @brief 读取可选金额参数（Read Optional Decimal Option）；
     *        Read one optional decimal option.
     *
     * @param command       已解析命令（Parsed command）。
     * @param canonicalName 规范参数名（Canonical option name）。
     * @param aliases       参数别名（Option aliases）。
     * @return 十进制数值（Decimal value），不存在返回 null。
     */
    public static BigDecimal optionalDecimal(
            final ParsedCommand command,
            final String canonicalName,
            final String... aliases
    ) {
        final String raw = optionalOption(command, canonicalName, aliases);
        if (raw == null) {
            return null;
        }
        return parseDecimal(raw, canonicalName);
    }

    /**
     * @brief 读取必填日期参数（Read Required Date Option）；
     *        Read one required date option in `yyyy-MM-dd` format.
     *
     * @param command       已解析命令（Parsed command）。
     * @param canonicalName 规范参数名（Canonical option name）。
     * @param aliases       参数别名（Option aliases）。
     * @return 日期值（Date value）。
     */
    public static LocalDate requiredDate(
            final ParsedCommand command,
            final String canonicalName,
            final String... aliases
    ) {
        final String raw = requiredOption(command, canonicalName, aliases);
        return parseDate(raw, canonicalName);
    }

    /**
     * @brief 读取可选日期参数（Read Optional Date Option）；
     *        Read one optional date option in `yyyy-MM-dd` format.
     *
     * @param command       已解析命令（Parsed command）。
     * @param canonicalName 规范参数名（Canonical option name）。
     * @param aliases       参数别名（Option aliases）。
     * @return 日期值（Date value），不存在返回 null。
     */
    public static LocalDate optionalDate(
            final ParsedCommand command,
            final String canonicalName,
            final String... aliases
    ) {
        final String raw = optionalOption(command, canonicalName, aliases);
        if (raw == null) {
            return null;
        }
        return parseDate(raw, canonicalName);
    }

    /**
     * @brief 解析十进制文本（Parse Decimal Text）；
     *        Parse decimal text into BigDecimal.
     *
     * @param rawValue      原始值（Raw value）。
     * @param canonicalName 规范参数名（Canonical option name）。
     * @return 十进制数值（Decimal value）。
     */
    private static BigDecimal parseDecimal(final String rawValue, final String canonicalName) {
        final String normalizedValue = normalizeRequiredValue(rawValue, canonicalName);
        try {
            return new BigDecimal(normalizedValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Invalid decimal option for --"
                            + normalizeName(canonicalName)
                            + ": "
                            + rawValue,
                    exception);
        }
    }

    /**
     * @brief 解析日期文本（Parse Date Text）；
     *        Parse date text into LocalDate.
     *
     * @param rawValue      原始值（Raw value）。
     * @param canonicalName 规范参数名（Canonical option name）。
     * @return 日期值（Date value）。
     */
    private static LocalDate parseDate(final String rawValue, final String canonicalName) {
        final String normalizedValue = normalizeRequiredValue(rawValue, canonicalName);
        try {
            return LocalDate.parse(normalizedValue);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "Invalid date option for --"
                            + normalizeName(canonicalName)
                            + ": "
                            + rawValue
                            + ", expected yyyy-MM-dd",
                    exception);
        }
    }

    /**
     * @brief 解析参数并检查别名冲突（Resolve Option and Validate Alias Conflicts）；
     *        Resolve option value from canonical name and aliases while rejecting conflicting values.
     *
     * @param command       已解析命令（Parsed command）。
     * @param canonicalName 规范参数名（Canonical option name）。
     * @param aliases       参数别名（Option aliases）。
     * @return 参数值（Option value），不存在则 empty。
     */
    private static Optional<String> resolveOption(
            final ParsedCommand command,
            final String canonicalName,
            final String... aliases
    ) {
        final ParsedCommand normalizedCommand = Objects.requireNonNull(command, "command must not be null");
        final String normalizedCanonical = normalizeName(canonicalName);

        final List<String> candidates = new ArrayList<>();
        candidates.add(normalizedCanonical);
        if (aliases != null) {
            for (String alias : aliases) {
                final String normalizedAlias = normalizeName(alias);
                if (!candidates.contains(normalizedAlias)) {
                    candidates.add(normalizedAlias);
                }
            }
        }

        String resolvedValue = null;
        String resolvedFrom = null;
        for (String optionName : candidates) {
            final Optional<String> maybeValue = normalizedCommand.option(optionName);
            if (maybeValue.isEmpty()) {
                continue;
            }

            final String currentValue = normalizeRequiredValue(maybeValue.get(), optionName);
            if (resolvedValue == null) {
                resolvedValue = currentValue;
                resolvedFrom = optionName;
                continue;
            }

            if (!resolvedValue.equals(currentValue)) {
                throw new IllegalArgumentException(
                        "Conflicting option values between --"
                                + resolvedFrom
                                + " and --"
                                + optionName);
            }
        }

        return Optional.ofNullable(resolvedValue);
    }

    /**
     * @brief 规范化参数名（Normalize Option Name）；
     *        Normalize option name.
     *
     * @param rawName 原始参数名（Raw option name）。
     * @return 规范化参数名（Normalized option name）。
     */
    private static String normalizeName(final String rawName) {
        final String normalized = Objects.requireNonNull(rawName, "option name must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("option name must not be blank");
        }
        return normalized;
    }

    /**
     * @brief 规范化非空参数值（Normalize Required Option Value）；
     *        Normalize required non-blank option value.
     *
     * @param rawValue 原始参数值（Raw option value）。
     * @param field    字段名（Field name）。
     * @return 规范化参数值（Normalized option value）。
     */
    private static String normalizeRequiredValue(final String rawValue, final String field) {
        final String normalized = Objects.requireNonNull(rawValue, field + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return normalized;
    }
}
