package com.moesegfault.banking.presentation.gui;

import java.util.Locale;
import java.util.Objects;

/**
 * @brief GUI 页面标识（GUI Page Identifier），用于唯一定位一个页面；
 *        GUI page identifier used to uniquely locate one page.
 *
 * @param value 页面标识文本（Page identifier text）。
 */
public record GuiPageId(String value) {

    /**
     * @brief 构造并规范化页面标识（Construct And Normalize Page Identifier）；
     *        Construct and normalize page identifier text.
     *
     * @param value 页面标识文本（Page identifier text）。
     */
    public GuiPageId {
        final String normalizedValue = Objects.requireNonNull(value, "value must not be null")
                .trim()
                .toLowerCase(Locale.ROOT);
        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException("page id must not be blank");
        }
        value = normalizedValue;
    }

    /**
     * @brief 工厂方法创建页面标识（Factory Method For Page Identifier）；
     *        Factory method creating one page identifier.
     *
     * @param value 页面标识文本（Page identifier text）。
     * @return 页面标识对象（Page identifier object）。
     */
    public static GuiPageId of(final String value) {
        return new GuiPageId(value);
    }
}
