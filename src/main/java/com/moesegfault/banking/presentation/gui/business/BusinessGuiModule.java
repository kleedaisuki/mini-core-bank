package com.moesegfault.banking.presentation.gui.business;

import com.moesegfault.banking.application.business.query.FindBusinessTransactionHandler;
import com.moesegfault.banking.application.business.query.ListBusinessTransactionsHandler;
import com.moesegfault.banking.presentation.gui.GuiModule;
import com.moesegfault.banking.presentation.gui.GuiPageRegistrar;
import java.util.Objects;

/**
 * @brief 业务流水 GUI 模块（Business GUI Module），注册业务流水详情页与列表页；
 *        Business GUI module registering transaction detail and list pages.
 */
public final class BusinessGuiModule implements GuiModule {

    /**
     * @brief 单笔业务流水查询应用服务（Find Business Transaction Application Service）；
     *        Application service for detail lookup.
     */
    private final FindBusinessTransactionHandler findBusinessTransactionHandler;

    /**
     * @brief 业务流水列表查询应用服务（List Business Transactions Application Service）；
     *        Application service for list lookup.
     */
    private final ListBusinessTransactionsHandler listBusinessTransactionsHandler;

    /**
     * @brief 构造 GUI 模块（Construct GUI Module）；
     *        Construct GUI module.
     *
     * @param findBusinessTransactionHandler 单笔业务流水查询应用服务（Find-business-transaction application service）。
     * @param listBusinessTransactionsHandler 业务流水列表查询应用服务（List-business-transactions application service）。
     */
    public BusinessGuiModule(
            final FindBusinessTransactionHandler findBusinessTransactionHandler,
            final ListBusinessTransactionsHandler listBusinessTransactionsHandler
    ) {
        this.findBusinessTransactionHandler = Objects.requireNonNull(
                findBusinessTransactionHandler,
                "findBusinessTransactionHandler must not be null");
        this.listBusinessTransactionsHandler = Objects.requireNonNull(
                listBusinessTransactionsHandler,
                "listBusinessTransactionsHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerPages(final GuiPageRegistrar registrar) {
        final GuiPageRegistrar normalizedRegistrar = Objects.requireNonNull(registrar, "registrar must not be null");
        normalizedRegistrar.register(
                ShowBusinessTransactionPageFactory.PAGE_ID,
                new ShowBusinessTransactionPageFactory(findBusinessTransactionHandler));
        normalizedRegistrar.register(
                ListBusinessTransactionsPageFactory.PAGE_ID,
                new ListBusinessTransactionsPageFactory(listBusinessTransactionsHandler));
    }
}
