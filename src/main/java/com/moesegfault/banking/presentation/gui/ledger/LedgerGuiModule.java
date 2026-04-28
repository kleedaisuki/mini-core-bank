package com.moesegfault.banking.presentation.gui.ledger;

import com.moesegfault.banking.presentation.gui.GuiModule;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.GuiPageRegistrar;
import java.util.Objects;

/**
 * @brief Ledger GUI 模块（Ledger GUI Module），负责注册 ledger 领域页面；
 *        Ledger GUI module responsible for registering ledger-domain pages.
 */
public final class LedgerGuiModule implements GuiModule {

    /**
     * @brief 余额查询页面工厂（Show Balance Page Factory）；
     *        Factory for balance-query page.
     */
    private final GuiPageFactory showBalancePageFactory;

    /**
     * @brief 分录查询页面工厂（Show Entries Page Factory）；
     *        Factory for ledger-entry query page.
     */
    private final GuiPageFactory showEntriesPageFactory;

    /**
     * @brief 构造 ledger GUI 模块（Construct Ledger GUI Module）；
     *        Construct ledger GUI module.
     *
     * @param showBalancePageFactory 余额查询页面工厂（Balance-query page factory）。
     * @param showEntriesPageFactory 分录查询页面工厂（Entries-query page factory）。
     */
    public LedgerGuiModule(final GuiPageFactory showBalancePageFactory,
                           final GuiPageFactory showEntriesPageFactory) {
        this.showBalancePageFactory = Objects.requireNonNull(showBalancePageFactory, "showBalancePageFactory must not be null");
        this.showEntriesPageFactory = Objects.requireNonNull(showEntriesPageFactory, "showEntriesPageFactory must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerPages(final GuiPageRegistrar registrar) {
        final GuiPageRegistrar normalizedRegistrar = Objects.requireNonNull(registrar, "registrar must not be null");
        normalizedRegistrar
                .register(LedgerGuiPageIds.SHOW_BALANCE, showBalancePageFactory)
                .register(LedgerGuiPageIds.SHOW_ENTRIES, showEntriesPageFactory);
    }
}
