package com.moesegfault.banking.presentation.cli.builtin;

import java.util.Locale;
import java.util.Objects;

/**
 * @brief 内建 CLI 命令常量（Built-in CLI Command Constants），集中定义内建命令路径；
 *        Built-in CLI command constants that centralize built-in command paths.
 */
public final class BuiltinCliCommands {

    /**
     * @brief Bash 命令路径（Bash Command Path）；
     *        Bash command path.
     */
    public static final String BASH = ":bash";

    /**
     * @brief 退出命令路径（Exit Command Path）；
     *        Exit command path.
     */
    public static final String EXIT = ":exit";

    /**
     * @brief 退出别名命令路径（Quit Command Path）；
     *        Quit command path.
     */
    public static final String QUIT = ":quit";

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for constants utility.
     */
    private BuiltinCliCommands() {
    }

    /**
     * @brief 判断是否 Bash 命令（Check Bash Command）；
     *        Check whether a token is the built-in bash command path.
     *
     * @param token 命令令牌（Command token）。
     * @return true 表示 Bash 命令（true when token is bash command）。
     */
    public static boolean isBash(final String token) {
        return BASH.equals(normalize(token));
    }

    /**
     * @brief 判断是否退出命令（Check Exit Command）；
     *        Check whether a token is a built-in exit command path.
     *
     * @param token 命令令牌（Command token）。
     * @return true 表示退出命令（true when token is an exit command）。
     */
    public static boolean isExit(final String token) {
        final String normalized = normalize(token);
        return EXIT.equals(normalized) || QUIT.equals(normalized);
    }

    /**
     * @brief 规范化命令令牌（Normalize Command Token）；
     *        Normalize command token for built-in command matching.
     *
     * @param token 命令令牌（Command token）。
     * @return 规范化命令令牌（Normalized command token）。
     */
    private static String normalize(final String token) {
        return Objects.requireNonNull(token, "token must not be null").toLowerCase(Locale.ROOT);
    }
}
