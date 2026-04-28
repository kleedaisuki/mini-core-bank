package com.moesegfault.banking.presentation.gui;

import java.util.Locale;
import java.util.Objects;

/**
 * @brief GUI 技术栈类型枚举（GUI Toolkit Type Enum），用于表示可选 GUI 运行时；
 *        GUI toolkit type enum used to represent selectable GUI runtimes.
 */
public enum GuiToolkitType {
    /**
     * @brief Swing 技术栈（Swing Toolkit）；
     *        Swing toolkit.
     */
    SWING,

    /**
     * @brief JavaFX 技术栈（JavaFX Toolkit）；
     *        JavaFX toolkit.
     */
    JAVAFX;

    /**
     * @brief 从字符串解析技术栈（Parse Toolkit Type From Text）；
     *        Parse toolkit type from textual input.
     *
     * @param text 输入文本（Input text）。
     * @return 技术栈类型（Toolkit type）。
     */
    public static GuiToolkitType from(final String text) {
        final String normalizedText = Objects.requireNonNull(text, "text must not be null")
                .trim()
                .toUpperCase(Locale.ROOT);
        if (normalizedText.isEmpty()) {
            throw new IllegalArgumentException("toolkit text must not be blank");
        }
        return GuiToolkitType.valueOf(normalizedText);
    }
}
