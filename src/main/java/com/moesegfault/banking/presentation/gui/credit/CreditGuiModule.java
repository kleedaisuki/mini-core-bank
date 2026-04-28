package com.moesegfault.banking.presentation.gui.credit;

import com.moesegfault.banking.presentation.gui.GuiModule;
import com.moesegfault.banking.presentation.gui.GuiPageRegistrar;
import java.util.Objects;

/**
 * @brief Credit GUI 模块（Credit GUI Module），集中注册 credit 领域页面工厂；
 *        Credit GUI module registering credit-domain page factories.
 */
public final class CreditGuiModule implements GuiModule {

    /**
     * @brief 生成账单页面工厂（Generate Statement Page Factory）；
     *        Page factory for generate-statement page.
     */
    private final GenerateStatementPageFactory generateStatementPageFactory;

    /**
     * @brief 信用卡还款页面工厂（Repay Credit Card Page Factory）；
     *        Page factory for repay-credit-card page.
     */
    private final RepayCreditCardPageFactory repayCreditCardPageFactory;

    /**
     * @brief 账单查询页面工厂（Show Statement Page Factory）；
     *        Page factory for show-statement page.
     */
    private final ShowStatementPageFactory showStatementPageFactory;

    /**
     * @brief 构造 credit GUI 模块（Construct Credit GUI Module）；
     *        Construct credit GUI module.
     *
     * @param generateStatementPageFactory 生成账单页面工厂（Generate-statement page factory）。
     * @param repayCreditCardPageFactory 信用卡还款页面工厂（Repay-credit-card page factory）。
     * @param showStatementPageFactory 账单查询页面工厂（Show-statement page factory）。
     */
    public CreditGuiModule(
            final GenerateStatementPageFactory generateStatementPageFactory,
            final RepayCreditCardPageFactory repayCreditCardPageFactory,
            final ShowStatementPageFactory showStatementPageFactory
    ) {
        this.generateStatementPageFactory = Objects.requireNonNull(
                generateStatementPageFactory,
                "generateStatementPageFactory must not be null");
        this.repayCreditCardPageFactory = Objects.requireNonNull(
                repayCreditCardPageFactory,
                "repayCreditCardPageFactory must not be null");
        this.showStatementPageFactory = Objects.requireNonNull(
                showStatementPageFactory,
                "showStatementPageFactory must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerPages(final GuiPageRegistrar registrar) {
        final GuiPageRegistrar normalizedRegistrar = Objects.requireNonNull(registrar, "registrar must not be null");
        normalizedRegistrar
                .register(CreditGuiPageIds.GENERATE_STATEMENT, generateStatementPageFactory)
                .register(CreditGuiPageIds.REPAY_CREDIT_CARD, repayCreditCardPageFactory)
                .register(CreditGuiPageIds.SHOW_STATEMENT, showStatementPageFactory);
    }
}
