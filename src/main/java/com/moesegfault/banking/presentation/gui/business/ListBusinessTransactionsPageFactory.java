package com.moesegfault.banking.presentation.gui.business;

import com.moesegfault.banking.application.business.query.ListBusinessTransactionsHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.GuiPageId;
import java.util.Objects;

/**
 * @brief 业务流水列表页面工厂（List Business Transactions Page Factory），创建列表页 MVC 三件套；
 *        Factory creating model-view-controller bundle for business-transaction list page.
 */
public final class ListBusinessTransactionsPageFactory implements GuiPageFactory {

    /**
     * @brief 页面标识（Page Identifier）；
     *        Page identifier constant.
     */
    public static final GuiPageId PAGE_ID = BusinessGuiPageIds.LIST_BUSINESS_TRANSACTIONS;

    /**
     * @brief 业务流水列表查询应用服务（List Business Transactions Application Service）；
     *        Application service for list lookup.
     */
    private final ListBusinessTransactionsHandler listBusinessTransactionsHandler;

    /**
     * @brief 构造页面工厂（Construct Page Factory）；
     *        Construct page factory.
     *
     * @param listBusinessTransactionsHandler 业务流水列表查询应用服务（List-business-transactions application service）。
     */
    public ListBusinessTransactionsPageFactory(final ListBusinessTransactionsHandler listBusinessTransactionsHandler) {
        this.listBusinessTransactionsHandler = Objects.requireNonNull(
                listBusinessTransactionsHandler,
                "listBusinessTransactionsHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuiPage createPage(final GuiContext context) {
        Objects.requireNonNull(context, "context must not be null");
        final ListBusinessTransactionsModel model = new ListBusinessTransactionsModel();
        final ListBusinessTransactionsController controller = new ListBusinessTransactionsController(
                listBusinessTransactionsHandler,
                model);
        final ListBusinessTransactionsView view = new ListBusinessTransactionsView();
        view.bindEventHandler(controller::onViewEvent);
        view.bindModel(model);
        return new GuiPage(PAGE_ID, model, view, controller);
    }
}
