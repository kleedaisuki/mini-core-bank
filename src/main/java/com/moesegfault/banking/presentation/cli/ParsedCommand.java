package com.moesegfault.banking.presentation.cli;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief CLI 解析结果（Parsed CLI Command），封装命令路径、参数与原始输入；
 *        Parsed CLI command that encapsulates command path, options, and raw input.
 */
public record ParsedCommand(
        String rawInput,
        List<String> commandPathSegments,
        Map<String, String> options
) {

    /**
     * @brief 规范化构造（Canonical Constructor），复制输入并保证不可变；
     *        Canonical constructor that copies inputs and guarantees immutability.
     *
     * @param rawInput            原始命令文本（Raw command text）。
     * @param commandPathSegments 命令路径分段（Command path segments）。
     * @param options             命令参数映射（Command options map）。
     */
    public ParsedCommand {
        final String normalizedRawInput = Objects.requireNonNull(rawInput, "rawInput must not be null").trim();
        if (normalizedRawInput.isEmpty()) {
            throw new IllegalArgumentException("rawInput must not be blank");
        }

        final List<String> copiedPathSegments = List.copyOf(Objects.requireNonNull(
                commandPathSegments,
                "commandPathSegments must not be null"));
        if (copiedPathSegments.isEmpty()) {
            throw new IllegalArgumentException("commandPathSegments must not be empty");
        }

        final Map<String, String> copiedOptions = Collections.unmodifiableMap(new LinkedHashMap<>(Objects.requireNonNull(
                options,
                "options must not be null")));

        for (String segment : copiedPathSegments) {
            final String normalizedSegment = Objects.requireNonNull(segment, "command path segment must not be null").trim();
            if (normalizedSegment.isEmpty()) {
                throw new IllegalArgumentException("command path segment must not be blank");
            }
        }

        for (Map.Entry<String, String> optionEntry : copiedOptions.entrySet()) {
            final String optionName = Objects.requireNonNull(optionEntry.getKey(), "option name must not be null").trim();
            if (optionName.isEmpty()) {
                throw new IllegalArgumentException("option name must not be blank");
            }
            Objects.requireNonNull(optionEntry.getValue(), "option value must not be null");
        }

        rawInput = normalizedRawInput;
        commandPathSegments = copiedPathSegments;
        options = copiedOptions;
    }

    /**
     * @brief 获取规范化命令路径（Normalized Command Path）；
     *        Get normalized command path joined by spaces in lowercase.
     *
     * @return 规范化命令路径（Normalized command path）。
     */
    public String commandPath() {
        return commandPathSegments.stream()
                .map(segment -> segment.toLowerCase(Locale.ROOT))
                .reduce((left, right) -> left + " " + right)
                .orElseThrow(() -> new IllegalStateException("commandPathSegments must not be empty"));
    }

    /**
     * @brief 按名称读取可选参数（Read Optional Option by Name）；
     *        Read option value by option name.
     *
     * @param optionName 参数名（Option name）。
     * @return 参数值（Option value），不存在则 empty。
     */
    public Optional<String> option(final String optionName) {
        final String normalizedName = normalizeOptionName(optionName);
        return Optional.ofNullable(options.get(normalizedName));
    }

    /**
     * @brief 按名称读取必填参数（Read Required Option by Name）；
     *        Read required option value by option name.
     *
     * @param optionName 参数名（Option name）。
     * @return 参数值（Option value）。
     * @note 若参数缺失将抛出 IllegalArgumentException；Throws IllegalArgumentException if missing.
     */
    public String requiredOption(final String optionName) {
        final String normalizedName = normalizeOptionName(optionName);
        return option(normalizedName)
                .orElseThrow(() -> new IllegalArgumentException("Missing required option: --" + normalizedName));
    }

    /**
     * @brief 规范化参数名（Normalize Option Name）；
     *        Normalize option name for lookup.
     *
     * @param optionName 参数名（Option name）。
     * @return 规范化参数名（Normalized option name）。
     */
    private static String normalizeOptionName(final String optionName) {
        final String trimmed = Objects.requireNonNull(optionName, "optionName must not be null").trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("optionName must not be blank");
        }
        return trimmed;
    }
}
