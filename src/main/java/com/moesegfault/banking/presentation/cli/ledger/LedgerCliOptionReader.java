package com.moesegfault.banking.presentation.cli.ledger;

import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief Ledger CLI 参数读取器（Ledger CLI Option Reader），统一 canonical schema 参数读取；
 *        Ledger CLI option reader that centralizes canonical-schema option extraction.
 */
final class LedgerCliOptionReader {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private LedgerCliOptionReader() {
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
     * @brief 读取整型参数（Read Integer Option）；
     *        Read one integer option with default fallback.
     *
     * @param command       已解析命令（Parsed command）。
     * @param canonicalName 规范参数名（Canonical option name）。
     * @param defaultValue  默认值（Default value）。
     * @param aliases       参数别名（Option aliases）。
     * @return 参数值（Integer value）。
     */
    public static int optionalInteger(
            final ParsedCommand command,
            final String canonicalName,
            final int defaultValue,
            final String... aliases
    ) {
        final String rawValue = optionalOption(command, canonicalName, aliases);
        if (rawValue == null) {
            return defaultValue;
        }

        final String normalizedValue = normalizeRequiredValue(rawValue, canonicalName);
        try {
            return Integer.parseInt(normalizedValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Invalid integer option for --" + normalizeName(canonicalName) + ": " + rawValue,
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
