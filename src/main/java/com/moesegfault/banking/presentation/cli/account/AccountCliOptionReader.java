package com.moesegfault.banking.presentation.cli.account;

import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief Account CLI 参数读取器（Account CLI Option Reader），统一 canonical schema 参数读取；
 *        Account CLI option reader that centralizes canonical-schema option extraction.
 */
final class AccountCliOptionReader {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private AccountCliOptionReader() {
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
        final String value = resolveOption(command, canonicalName, aliases).orElseThrow(
                () -> new IllegalArgumentException("Missing required option: --" + normalizeName(canonicalName)));
        return value;
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
     * @brief 读取布尔参数（Read Boolean Option）；
     *        Read one boolean option with default value fallback.
     *
     * @param command       已解析命令（Parsed command）。
     * @param canonicalName 规范参数名（Canonical option name）。
     * @param defaultValue  默认值（Default value）。
     * @param aliases       参数别名（Option aliases）。
     * @return 布尔参数值（Boolean option value）。
     */
    public static boolean optionalBoolean(
            final ParsedCommand command,
            final String canonicalName,
            final boolean defaultValue,
            final String... aliases
    ) {
        final String raw = optionalOption(command, canonicalName, aliases);
        if (raw == null) {
            return defaultValue;
        }
        return parseBoolean(raw, canonicalName);
    }

    /**
     * @brief 解析布尔文本（Parse Boolean Text）；
     *        Parse boolean text value.
     *
     * @param rawValue      原始值（Raw value）。
     * @param canonicalName 规范参数名（Canonical option name）。
     * @return 解析后的布尔值（Parsed boolean value）。
     */
    private static boolean parseBoolean(final String rawValue, final String canonicalName) {
        final String normalizedValue = normalizeRequiredValue(rawValue, canonicalName).toLowerCase(Locale.ROOT);
        return switch (normalizedValue) {
            case "true", "1", "yes", "y", "on" -> true;
            case "false", "0", "no", "n", "off" -> false;
            default -> throw new IllegalArgumentException(
                    "Invalid boolean option for --"
                            + normalizeName(canonicalName)
                            + ": "
                            + rawValue
                            + ", expected one of true/false/1/0/yes/no");
        };
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
