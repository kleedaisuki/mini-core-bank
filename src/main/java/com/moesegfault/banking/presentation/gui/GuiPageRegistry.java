package com.moesegfault.banking.presentation.gui;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @brief GUI 页面注册表（GUI Page Registry），维护页面标识到页面工厂映射；
 *        GUI page registry mapping page identifiers to page factories.
 */
public final class GuiPageRegistry {

    /**
     * @brief 页面映射表（Page Mapping Table）；
     *        Mapping table from page identifier to page factory.
     */
    private final Map<GuiPageId, GuiPageFactory> pageMappings = new LinkedHashMap<>();

    /**
     * @brief 注册页面工厂（Register Page Factory）；
     *        Register one page factory by page identifier.
     *
     * @param pageId 页面标识（Page identifier）。
     * @param pageFactory 页面工厂（Page factory）。
     */
    public void register(final GuiPageId pageId, final GuiPageFactory pageFactory) {
        final GuiPageId normalizedPageId = Objects.requireNonNull(pageId, "pageId must not be null");
        final GuiPageFactory normalizedPageFactory = Objects.requireNonNull(pageFactory, "pageFactory must not be null");

        final GuiPageFactory existingFactory = pageMappings.get(normalizedPageId);
        if (existingFactory != null && !existingFactory.equals(normalizedPageFactory)) {
            throw new IllegalStateException("Page id already registered with a different factory: " + normalizedPageId.value());
        }
        pageMappings.putIfAbsent(normalizedPageId, normalizedPageFactory);
    }

    /**
     * @brief 查找页面工厂（Find Page Factory）；
     *        Find page factory by page identifier.
     *
     * @param pageId 页面标识（Page identifier）。
     * @return 页面工厂（Page factory），不存在返回 empty。
     */
    public Optional<GuiPageFactory> findPageFactory(final GuiPageId pageId) {
        final GuiPageId normalizedPageId = Objects.requireNonNull(pageId, "pageId must not be null");
        return Optional.ofNullable(pageMappings.get(normalizedPageId));
    }

    /**
     * @brief 获取注册页面标识集合（Get Registered Page Identifier Set）；
     *        Get registered page identifier set.
     *
     * @return 页面标识集合（Page identifier set）。
     */
    public Set<GuiPageId> registeredPageIds() {
        return Collections.unmodifiableSet(pageMappings.keySet());
    }

    /**
     * @brief 获取页面映射快照（Get Page Mapping Snapshot）；
     *        Get immutable snapshot of page mappings.
     *
     * @return 页面映射快照（Page mapping snapshot）。
     */
    public Map<GuiPageId, GuiPageFactory> mappingsSnapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(pageMappings));
    }
}
