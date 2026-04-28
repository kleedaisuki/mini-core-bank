package com.moesegfault.banking.presentation.cli;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @brief CLI 命令解析器（CLI Command Parser），将原始输入解析为 ParsedCommand；
 *        CLI command parser that converts raw input text into ParsedCommand.
 */
public final class CommandParser {

    /**
     * @brief 无显式值参数默认值（Default Value for Flag Option）；
     *        Default string value used for flag options without explicit value.
     */
    private static final String DEFAULT_FLAG_VALUE = "true";

    /**
     * @brief 解析命令文本（Parse Raw Command Text）；
     *        Parse raw command text into command path and options.
     *
     * @param rawInput 原始命令文本（Raw command text）。
     * @return 解析结果（Parsed command）。
     */
    public ParsedCommand parse(final String rawInput) {
        final String normalizedInput = Objects.requireNonNull(rawInput, "rawInput must not be null").trim();
        if (normalizedInput.isEmpty()) {
            throw new IllegalArgumentException("Command input must not be blank");
        }

        final List<String> tokens = tokenize(normalizedInput);
        final List<String> commandPathSegments = new ArrayList<>();
        final Map<String, String> options = new LinkedHashMap<>();

        boolean optionSectionStarted = false;
        for (int index = 0; index < tokens.size(); index++) {
            final String token = tokens.get(index);
            if (isOptionToken(token)) {
                optionSectionStarted = true;
                index = parseOption(tokens, index, options);
                continue;
            }

            if (optionSectionStarted) {
                throw new IllegalArgumentException("Positional token after options is not supported: " + token);
            }
            commandPathSegments.add(token);
        }

        if (commandPathSegments.isEmpty()) {
            throw new IllegalArgumentException("Command path must not be empty");
        }

        return new ParsedCommand(normalizedInput, commandPathSegments, options);
    }

    /**
     * @brief 解析单个参数（Parse One Option Token）；
     *        Parse one option token in `--name=value` or `--name value` style.
     *
     * @param tokens  令牌序列（Token sequence）。
     * @param index   当前索引（Current token index）。
     * @param options 参数映射（Options map）。
     * @return 解析后索引（Index after option parse）。
     */
    private int parseOption(final List<String> tokens, final int index, final Map<String, String> options) {
        final String optionToken = tokens.get(index);
        final String body = optionToken.substring(2);

        if (body.isBlank()) {
            throw new IllegalArgumentException("Option name must not be blank");
        }

        final int equalsIndex = body.indexOf('=');
        if (equalsIndex >= 0) {
            final String optionName = normalizeOptionName(body.substring(0, equalsIndex));
            final String optionValue = body.substring(equalsIndex + 1);
            putOption(options, optionName, optionValue);
            return index;
        }

        final String optionName = normalizeOptionName(body);
        if (index + 1 < tokens.size() && !isOptionToken(tokens.get(index + 1))) {
            putOption(options, optionName, tokens.get(index + 1));
            return index + 1;
        }

        putOption(options, optionName, DEFAULT_FLAG_VALUE);
        return index;
    }

    /**
     * @brief 写入参数映射（Put Option into Map）；
     *        Put option into map and reject duplicates.
     *
     * @param options    参数映射（Options map）。
     * @param optionName 参数名（Option name）。
     * @param optionValue 参数值（Option value）。
     */
    private void putOption(final Map<String, String> options,
                           final String optionName,
                           final String optionValue) {
        if (options.containsKey(optionName)) {
            throw new IllegalArgumentException("Duplicate option: --" + optionName);
        }
        options.put(optionName, optionValue);
    }

    /**
     * @brief 判断是否参数令牌（Is Option Token）；
     *        Determine whether token is an option token.
     *
     * @param token 令牌文本（Token text）。
     * @return true 表示参数令牌（true means option token）。
     */
    private static boolean isOptionToken(final String token) {
        return token.startsWith("--");
    }

    /**
     * @brief 参数名规范化（Normalize Option Name）；
     *        Normalize and validate option name.
     *
     * @param optionName 参数名（Option name）。
     * @return 规范化参数名（Normalized option name）。
     */
    private static String normalizeOptionName(final String optionName) {
        final String normalized = optionName.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Option name must not be blank");
        }
        return normalized;
    }

    /**
     * @brief 词法切分（Tokenize Command Text）；
     *        Tokenize command text with quote and escape support.
     *
     * @param input 原始文本（Raw text）。
     * @return 令牌列表（Tokens）。
     */
    private static List<String> tokenize(final String input) {
        final List<String> tokens = new ArrayList<>();
        final StringBuilder current = new StringBuilder();

        Character quote = null;
        boolean escaped = false;
        for (int index = 0; index < input.length(); index++) {
            final char currentChar = input.charAt(index);

            if (escaped) {
                current.append(currentChar);
                escaped = false;
                continue;
            }

            if (currentChar == '\\') {
                escaped = true;
                continue;
            }

            if (quote != null) {
                if (currentChar == quote.charValue()) {
                    quote = null;
                } else {
                    current.append(currentChar);
                }
                continue;
            }

            if (currentChar == '\'' || currentChar == '"') {
                quote = currentChar;
                continue;
            }

            if (Character.isWhitespace(currentChar)) {
                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }

            current.append(currentChar);
        }

        if (escaped) {
            throw new IllegalArgumentException("Trailing escape character is not allowed");
        }
        if (quote != null) {
            throw new IllegalArgumentException("Unclosed quote is not allowed");
        }
        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }
        return tokens;
    }
}
