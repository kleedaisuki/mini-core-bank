package com.moesegfault.banking.presentation.gui;

import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.GuiModel;
import com.moesegfault.banking.presentation.gui.mvc.GuiView;
import java.util.Objects;

/**
 * @brief GUI 页面组合（GUI Page Bundle），封装单页 model-view-controller 三件套；
 *        GUI page bundle encapsulating model-view-controller trio for one page.
 */
public final class GuiPage {

    /**
     * @brief 页面标识（Page Identifier）；
     *        Page identifier.
     */
    private final GuiPageId pageId;

    /**
     * @brief 页面模型（Page Model）；
     *        Page model.
     */
    private final GuiModel model;

    /**
     * @brief 页面视图（Page View）；
     *        Page view.
     */
    private final GuiView<? extends GuiModel> view;

    /**
     * @brief 页面控制器（Page Controller）；
     *        Page controller.
     */
    private final GuiController controller;

    /**
     * @brief 构造页面组合（Construct GUI Page Bundle）；
     *        Construct one GUI page bundle.
     *
     * @param pageId 页面标识（Page identifier）。
     * @param model 页面模型（Page model）。
     * @param view 页面视图（Page view）。
     * @param controller 页面控制器（Page controller）。
     */
    public GuiPage(final GuiPageId pageId,
                   final GuiModel model,
                   final GuiView<? extends GuiModel> view,
                   final GuiController controller) {
        this.pageId = Objects.requireNonNull(pageId, "pageId must not be null");
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.view = Objects.requireNonNull(view, "view must not be null");
        this.controller = Objects.requireNonNull(controller, "controller must not be null");
    }

    /**
     * @brief 获取页面标识（Get Page Identifier）；
     *        Get page identifier.
     *
     * @return 页面标识（Page identifier）。
     */
    public GuiPageId pageId() {
        return pageId;
    }

    /**
     * @brief 获取页面模型（Get Page Model）；
     *        Get page model.
     *
     * @return 页面模型（Page model）。
     */
    public GuiModel model() {
        return model;
    }

    /**
     * @brief 获取页面视图（Get Page View）；
     *        Get page view.
     *
     * @return 页面视图（Page view）。
     */
    public GuiView<? extends GuiModel> view() {
        return view;
    }

    /**
     * @brief 获取页面控制器（Get Page Controller）；
     *        Get page controller.
     *
     * @return 页面控制器（Page controller）。
     */
    public GuiController controller() {
        return controller;
    }

    /**
     * @brief 初始化页面（Initialize Page）；
     *        Initialize page controller before mount.
     */
    public void init() {
        controller.init();
    }

    /**
     * @brief 挂载页面（Mount Page）；
     *        Mount and render page view.
     */
    public void mount() {
        view.mount();
        view.render();
    }

    /**
     * @brief 卸载页面（Unmount Page）；
     *        Unmount page view.
     */
    public void unmount() {
        view.unmount();
    }
}
