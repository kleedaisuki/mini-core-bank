package com.moesegfault.banking.presentation.gui.business;

import com.moesegfault.banking.application.business.query.FindBusinessTransactionHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.GuiPageId;
import java.util.Objects;

/**
 * @brief 业务流水详情页面工厂（Show Business Transaction Page Factory），创建详情页 MVC 三件套；
 *        Factory creating model-view-controller bundle for business-transaction detail page.
 */
public final class ShowBusinessTransactionPageFactory implements GuiPageFactory {

    /**
     * @brief 页面标识（Page Identifier）；
     *        Page identifier constant.
     */
    public static final GuiPageId PAGE_ID = BusinessGuiPageIds.SHOW_BUSINESS_TRANSACTION;

    /**
     * @brief 单笔业务流水查询应用服务（Find Business Transaction Application Service）；
     *        Application service for detail lookup.
     */
    private final FindBusinessTransactionHandler findBusinessTransactionHandler;

    /**
     * @brief 构造页面工厂（Construct Page Factory）；
     *        Construct page factory.
     *
     * @param findBusinessTransactionHandler 单笔业务流水查询应用服务（Find-business-transaction application service）。
     */
    public ShowBusinessTransactionPageFactory(final FindBusinessTransactionHandler findBusinessTransactionHandler) {
        this.findBusinessTransactionHandler = Objects.requireNonNull(
                findBusinessTransactionHandler,
                "findBusinessTransactionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuiPage createPage(final GuiContext context) {
        Objects.requireNonNull(context, "context must not be null");
        final ShowBusinessTransactionModel model = new ShowBusinessTransactionModel();
        final ShowBusinessTransactionController controller = new ShowBusinessTransactionController(
                findBusinessTransactionHandler,
                model);
        final ShowBusinessTransactionView view = new ShowBusinessTransactionView();
        view.bindEventHandler(controller::onViewEvent);
        view.bindModel(model);
        return new GuiPage(PAGE_ID, model, view, controller);
    }
}
