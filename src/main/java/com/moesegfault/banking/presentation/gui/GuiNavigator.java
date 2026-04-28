package com.moesegfault.banking.presentation.gui;

import java.util.Objects;
import java.util.Optional;

/**
 * @brief GUI 页面导航器（GUI Navigator），负责页面创建、切换和上下文状态同步；
 *        GUI navigator responsible for page creation, switching, and context state synchronization.
 */
public final class GuiNavigator {

    /**
     * @brief 会话上下文（Session Context）；
     *        Session context.
     */
    private final GuiContext context;

    /**
     * @brief 页面注册表（Page Registry）；
     *        Page registry.
     */
    private final GuiPageRegistry pageRegistry;

    /**
     * @brief 运行时对象（Runtime Object）；
     *        Runtime object.
     */
    private final GuiRuntime runtime;

    /**
     * @brief 当前已挂载页面（Current Mounted Page）；
     *        Currently mounted page.
     */
    private GuiPage currentPage;

    /**
     * @brief 构造导航器（Construct Navigator）；
     *        Construct GUI navigator.
     *
     * @param context 会话上下文（Session context）。
     * @param pageRegistry 页面注册表（Page registry）。
     * @param runtime 运行时对象（Runtime object）。
     */
    public GuiNavigator(final GuiContext context,
                        final GuiPageRegistry pageRegistry,
                        final GuiRuntime runtime) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.pageRegistry = Objects.requireNonNull(pageRegistry, "pageRegistry must not be null");
        this.runtime = Objects.requireNonNull(runtime, "runtime must not be null");
    }

    /**
     * @brief 导航到目标页面（Navigate To Target Page）；
     *        Navigate to target page by identifier.
     *
     * @param pageId 页面标识（Page identifier）。
     * @return 已创建并挂载的页面（Created and mounted page）。
     */
    public GuiPage navigateTo(final GuiPageId pageId) {
        final GuiPageId normalizedPageId = Objects.requireNonNull(pageId, "pageId must not be null");
        final GuiPageFactory pageFactory = pageRegistry.findPageFactory(normalizedPageId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown gui page id: " + normalizedPageId.value()));

        final GuiPage nextPage = Objects.requireNonNull(
                pageFactory.createPage(context),
                "pageFactory must not create null page");

        if (!nextPage.pageId().equals(normalizedPageId)) {
            throw new IllegalStateException(
                    "Created page id does not match requested id: requested="
                            + normalizedPageId.value() + ", actual=" + nextPage.pageId().value());
        }

        nextPage.init();
        runtime.showPage(nextPage);
        context.setCurrentPageId(normalizedPageId);
        currentPage = nextPage;
        return nextPage;
    }

    /**
     * @brief 获取当前页面（Get Current Page）；
     *        Get current mounted page.
     *
     * @return 当前页面（Current page）。
     */
    public Optional<GuiPage> currentPage() {
        return Optional.ofNullable(currentPage);
    }
}
