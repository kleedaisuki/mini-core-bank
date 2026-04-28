package com.moesegfault.banking.presentation.gui;

import java.util.Map;
import java.util.Objects;

/**
 * @brief GUI 页面注册器（GUI Page Registrar），提供链式 API 简化批量页面注册；
 *        GUI page registrar offering fluent APIs to simplify bulk page registration.
 */
public final class GuiPageRegistrar {

    /**
     * @brief 页面注册表（Page Registry）；
     *        Underlying page registry.
     */
    private final GuiPageRegistry pageRegistry;

    /**
     * @brief 构造页面注册器（Construct Page Registrar）；
     *        Construct page registrar with one page registry.
     *
     * @param pageRegistry 页面注册表（Page registry）。
     */
    public GuiPageRegistrar(final GuiPageRegistry pageRegistry) {
        this.pageRegistry = Objects.requireNonNull(pageRegistry, "pageRegistry must not be null");
    }

    /**
     * @brief 按页面标识注册页面工厂（Register Page Factory By Page Id）；
     *        Register page factory by page identifier.
     *
     * @param pageId 页面标识（Page identifier）。
     * @param pageFactory 页面工厂（Page factory）。
     * @return 当前注册器（Current registrar）。
     */
    public GuiPageRegistrar register(final GuiPageId pageId, final GuiPageFactory pageFactory) {
        pageRegistry.register(pageId, pageFactory);
        return this;
    }

    /**
     * @brief 按字符串标识注册页面工厂（Register Page Factory By String Id）；
     *        Register page factory by string page identifier.
     *
     * @param pageIdValue 页面标识字符串（Page identifier text）。
     * @param pageFactory 页面工厂（Page factory）。
     * @return 当前注册器（Current registrar）。
     */
    public GuiPageRegistrar register(final String pageIdValue, final GuiPageFactory pageFactory) {
        return register(GuiPageId.of(pageIdValue), pageFactory);
    }

    /**
     * @brief 批量注册页面工厂（Register Multiple Page Factories）；
     *        Register multiple page factories from map.
     *
     * @param pageMappings 页面映射（Page mappings）。
     * @return 当前注册器（Current registrar）。
     */
    public GuiPageRegistrar registerAll(final Map<GuiPageId, GuiPageFactory> pageMappings) {
        final Map<GuiPageId, GuiPageFactory> normalizedMappings =
                Map.copyOf(Objects.requireNonNull(pageMappings, "pageMappings must not be null"));
        normalizedMappings.forEach(this::register);
        return this;
    }

    /**
     * @brief 获取底层页面注册表（Get Underlying Page Registry）；
     *        Get underlying page registry.
     *
     * @return 页面注册表（Page registry）。
     */
    public GuiPageRegistry pageRegistry() {
        return pageRegistry;
    }
}
