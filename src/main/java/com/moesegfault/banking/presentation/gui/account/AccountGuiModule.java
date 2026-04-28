package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.presentation.gui.GuiModule;
import com.moesegfault.banking.presentation.gui.GuiPageRegistrar;
import java.util.Objects;

/**
 * @brief Account GUI 模块（Account GUI Module），集中注册账户域页面工厂；
 *        Account GUI module that registers account-domain page factories.
 */
public final class AccountGuiModule implements GuiModule {

    /**
     * @brief 开立储蓄账户页面工厂（Open Savings Account Page Factory）;
     *        Page factory for open-savings-account page.
     */
    private final OpenSavingsAccountPageFactory openSavingsAccountPageFactory;

    /**
     * @brief 开立外汇账户页面工厂（Open FX Account Page Factory）;
     *        Page factory for open-FX-account page.
     */
    private final OpenFxAccountPageFactory openFxAccountPageFactory;

    /**
     * @brief 开立投资账户页面工厂（Open Investment Account Page Factory）;
     *        Page factory for open-investment-account page.
     */
    private final OpenInvestmentAccountPageFactory openInvestmentAccountPageFactory;

    /**
     * @brief 账户详情页面工厂（Show Account Page Factory）;
     *        Page factory for show-account page.
     */
    private final ShowAccountPageFactory showAccountPageFactory;

    /**
     * @brief 账户列表页面工厂（List Accounts Page Factory）;
     *        Page factory for list-accounts page.
     */
    private final ListAccountsPageFactory listAccountsPageFactory;

    /**
     * @brief 构造模块（Construct Module）;
     *        Construct account GUI module.
     *
     * @param openSavingsAccountPageFactory 开立储蓄账户页面工厂（Open savings-account page factory）。
     * @param openFxAccountPageFactory 开立外汇账户页面工厂（Open FX-account page factory）。
     * @param openInvestmentAccountPageFactory 开立投资账户页面工厂（Open investment-account page factory）。
     * @param showAccountPageFactory 账户详情页面工厂（Show-account page factory）。
     * @param listAccountsPageFactory 账户列表页面工厂（List-accounts page factory）。
     */
    public AccountGuiModule(
            final OpenSavingsAccountPageFactory openSavingsAccountPageFactory,
            final OpenFxAccountPageFactory openFxAccountPageFactory,
            final OpenInvestmentAccountPageFactory openInvestmentAccountPageFactory,
            final ShowAccountPageFactory showAccountPageFactory,
            final ListAccountsPageFactory listAccountsPageFactory
    ) {
        this.openSavingsAccountPageFactory = Objects.requireNonNull(
                openSavingsAccountPageFactory,
                "openSavingsAccountPageFactory must not be null");
        this.openFxAccountPageFactory = Objects.requireNonNull(
                openFxAccountPageFactory,
                "openFxAccountPageFactory must not be null");
        this.openInvestmentAccountPageFactory = Objects.requireNonNull(
                openInvestmentAccountPageFactory,
                "openInvestmentAccountPageFactory must not be null");
        this.showAccountPageFactory = Objects.requireNonNull(
                showAccountPageFactory,
                "showAccountPageFactory must not be null");
        this.listAccountsPageFactory = Objects.requireNonNull(
                listAccountsPageFactory,
                "listAccountsPageFactory must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerPages(final GuiPageRegistrar registrar) {
        final GuiPageRegistrar normalizedRegistrar = Objects.requireNonNull(registrar, "registrar must not be null");
        normalizedRegistrar
                .register(AccountGuiPageIds.OPEN_SAVINGS_ACCOUNT, openSavingsAccountPageFactory)
                .register(AccountGuiPageIds.OPEN_FX_ACCOUNT, openFxAccountPageFactory)
                .register(AccountGuiPageIds.OPEN_INVESTMENT_ACCOUNT, openInvestmentAccountPageFactory)
                .register(AccountGuiPageIds.SHOW_ACCOUNT, showAccountPageFactory)
                .register(AccountGuiPageIds.LIST_ACCOUNTS, listAccountsPageFactory);
    }
}
