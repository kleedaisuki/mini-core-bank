package com.moesegfault.banking.presentation.gui;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief GUI 会话上下文（GUI Session Context），保存当前页面、用户与界面偏好；
 *        GUI session context storing current page, signed-in user, and UI preferences.
 */
public final class GuiContext {

    /**
     * @brief 当前页面标识（Current Page Identifier）；
     *        Current page identifier.
     */
    private GuiPageId currentPageId;

    /**
     * @brief 当前客户编号（Current Customer Identifier）；
     *        Current customer identifier.
     */
    private String currentCustomerId;

    /**
     * @brief 当前界面语言环境（Current Locale）；
     *        Current UI locale.
     */
    private Locale locale;

    /**
     * @brief 当前主题名（Current Theme Name）；
     *        Current theme name.
     */
    private String theme;

    /**
     * @brief 使用默认设置构造上下文（Construct Context With Defaults）；
     *        Construct context with default locale and theme.
     */
    public GuiContext() {
        this.locale = Locale.getDefault();
        this.theme = "default";
    }

    /**
     * @brief 获取当前页面标识（Get Current Page Identifier）；
     *        Get current page identifier.
     *
     * @return 当前页面标识（Current page identifier）。
     */
    public Optional<GuiPageId> currentPageId() {
        return Optional.ofNullable(currentPageId);
    }

    /**
     * @brief 设置当前页面标识（Set Current Page Identifier）；
     *        Set current page identifier.
     *
     * @param pageId 页面标识（Page identifier）。
     */
    public void setCurrentPageId(final GuiPageId pageId) {
        this.currentPageId = Objects.requireNonNull(pageId, "pageId must not be null");
    }

    /**
     * @brief 获取当前客户编号（Get Current Customer Identifier）；
     *        Get current customer identifier.
     *
     * @return 当前客户编号（Current customer identifier）。
     */
    public Optional<String> currentCustomerId() {
        return Optional.ofNullable(currentCustomerId);
    }

    /**
     * @brief 设置当前客户编号（Set Current Customer Identifier）；
     *        Set current customer identifier.
     *
     * @param customerId 客户编号（Customer identifier）。
     */
    public void setCurrentCustomerId(final String customerId) {
        final String normalizedCustomerId = Objects.requireNonNull(customerId, "customerId must not be null").trim();
        if (normalizedCustomerId.isEmpty()) {
            throw new IllegalArgumentException("customerId must not be blank");
        }
        this.currentCustomerId = normalizedCustomerId;
    }

    /**
     * @brief 清除当前客户编号（Clear Current Customer Identifier）；
     *        Clear current customer identifier.
     */
    public void clearCurrentCustomerId() {
        this.currentCustomerId = null;
    }

    /**
     * @brief 获取语言环境（Get Locale）；
     *        Get locale.
     *
     * @return 当前语言环境（Current locale）。
     */
    public Locale locale() {
        return locale;
    }

    /**
     * @brief 设置语言环境（Set Locale）；
     *        Set locale.
     *
     * @param locale 语言环境（Locale）。
     */
    public void setLocale(final Locale locale) {
        this.locale = Objects.requireNonNull(locale, "locale must not be null");
    }

    /**
     * @brief 获取主题名（Get Theme Name）；
     *        Get theme name.
     *
     * @return 当前主题名（Current theme name）。
     */
    public String theme() {
        return theme;
    }

    /**
     * @brief 设置主题名（Set Theme Name）；
     *        Set theme name.
     *
     * @param theme 主题名（Theme name）。
     */
    public void setTheme(final String theme) {
        final String normalizedTheme = Objects.requireNonNull(theme, "theme must not be null").trim();
        if (normalizedTheme.isEmpty()) {
            throw new IllegalArgumentException("theme must not be blank");
        }
        this.theme = normalizedTheme;
    }
}
