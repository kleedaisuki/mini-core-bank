package com.moesegfault.banking.presentation.cli.style;

import java.util.Objects;

/**
 * @brief CLI 文本样式工具（CLI Text Style Utility），集中管理 ANSI 颜色与层级样式；
 *        CLI text style utility that centralizes ANSI colors and hierarchy styles.
 *
 * @note 该工具只用于 CLI 元信息（CLI metadata），避免污染业务数据与 Bash 原始输出；
 *       This utility is intended for CLI metadata only, avoiding pollution of business data and raw bash output.
 */
public final class CliStyle {

    /**
     * @brief ANSI 重置序列（ANSI Reset Sequence）；
     *        ANSI reset sequence.
     */
    private static final String RESET = "\u001B[0m";

    /**
     * @brief ANSI 粗体序列（ANSI Bold Sequence）；
     *        ANSI bold sequence.
     */
    private static final String BOLD = "\u001B[1m";

    /**
     * @brief ANSI 弱化序列（ANSI Dim Sequence）；
     *        ANSI dim sequence.
     */
    private static final String DIM = "\u001B[2m";

    /**
     * @brief ANSI 红色序列（ANSI Red Sequence）；
     *        ANSI red sequence.
     */
    private static final String RED = "\u001B[31m";

    /**
     * @brief ANSI 绿色序列（ANSI Green Sequence）；
     *        ANSI green sequence.
     */
    private static final String GREEN = "\u001B[32m";

    /**
     * @brief ANSI 黄色序列（ANSI Yellow Sequence）；
     *        ANSI yellow sequence.
     */
    private static final String YELLOW = "\u001B[33m";

    /**
     * @brief ANSI 蓝色序列（ANSI Blue Sequence）；
     *        ANSI blue sequence.
     */
    private static final String BLUE = "\u001B[34m";

    /**
     * @brief ANSI 品红色序列（ANSI Magenta Sequence）；
     *        ANSI magenta sequence.
     */
    private static final String MAGENTA = "\u001B[35m";

    /**
     * @brief ANSI 青色序列（ANSI Cyan Sequence）；
     *        ANSI cyan sequence.
     */
    private static final String CYAN = "\u001B[36m";

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private CliStyle() {
    }

    /**
     * @brief 标题样式（Title Style）；
     *        Style a top-level title.
     *
     * @param text 文本（Text）。
     * @return 带样式文本（Styled text）。
     */
    public static String title(final String text) {
        return apply(BOLD + CYAN, text);
    }

    /**
     * @brief 段落标题样式（Section Heading Style）；
     *        Style a section heading.
     *
     * @param text 文本（Text）。
     * @return 带样式文本（Styled text）。
     */
    public static String section(final String text) {
        return apply(BOLD + BLUE, text);
    }

    /**
     * @brief 命令样式（Command Style）；
     *        Style a command path.
     *
     * @param text 文本（Text）。
     * @return 带样式文本（Styled text）。
     */
    public static String command(final String text) {
        return apply(GREEN, text);
    }

    /**
     * @brief 参数样式（Option Style）；
     *        Style an option or argument.
     *
     * @param text 文本（Text）。
     * @return 带样式文本（Styled text）。
     */
    public static String option(final String text) {
        return apply(YELLOW, text);
    }

    /**
     * @brief 标签样式（Label Style）；
     *        Style a label.
     *
     * @param text 文本（Text）。
     * @return 带样式文本（Styled text）。
     */
    public static String label(final String text) {
        return apply(MAGENTA, text);
    }

    /**
     * @brief 弱化样式（Muted Style）；
     *        Style less-important explanatory text.
     *
     * @param text 文本（Text）。
     * @return 带样式文本（Styled text）。
     */
    public static String muted(final String text) {
        return apply(DIM, text);
    }

    /**
     * @brief 错误样式（Error Style）；
     *        Style an error label or message.
     *
     * @param text 文本（Text）。
     * @return 带样式文本（Styled text）。
     */
    public static String error(final String text) {
        return apply(BOLD + RED, text);
    }

    /**
     * @brief 提示样式（Hint Style）；
     *        Style an actionable hint.
     *
     * @param text 文本（Text）。
     * @return 带样式文本（Styled text）。
     */
    public static String hint(final String text) {
        return apply(CYAN, text);
    }

    /**
     * @brief 提示符样式（Prompt Style）；
     *        Style the interactive shell prompt.
     *
     * @param text 文本（Text）。
     * @return 带样式文本（Styled text）。
     */
    public static String prompt(final String text) {
        return apply(BOLD + GREEN, text);
    }

    /**
     * @brief 应用 ANSI 样式（Apply ANSI Style）；
     *        Apply one ANSI style prefix to text.
     *
     * @param prefix ANSI 前缀（ANSI prefix）。
     * @param text   文本（Text）。
     * @return 带样式文本（Styled text）。
     */
    private static String apply(final String prefix, final String text) {
        return prefix + Objects.requireNonNull(text, "text must not be null") + RESET;
    }
}
