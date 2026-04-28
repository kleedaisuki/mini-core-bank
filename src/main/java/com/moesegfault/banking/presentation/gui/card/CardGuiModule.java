package com.moesegfault.banking.presentation.gui.card;

import com.moesegfault.banking.presentation.gui.GuiModule;
import com.moesegfault.banking.presentation.gui.GuiPageRegistrar;
import java.util.Objects;

/**
 * @brief 卡模块 GUI 页面注册器（Card GUI Module），集中注册 card 领域页面工厂；
 *        Card GUI module that registers card-domain page factories in one place.
 */
public final class CardGuiModule implements GuiModule {

    /**
     * @brief 主借记卡页面工厂（Issue Debit Card Page Factory）；
     *        Factory for `card.issue-debit` page.
     */
    private final IssueDebitCardPageFactory issueDebitCardPageFactory;

    /**
     * @brief 借记附属卡页面工厂（Issue Supplementary Debit Card Page Factory）；
     *        Factory for `card.issue-supplementary-debit` page.
     */
    private final IssueSupplementaryDebitCardPageFactory issueSupplementaryDebitCardPageFactory;

    /**
     * @brief 主信用卡页面工厂（Issue Credit Card Page Factory）；
     *        Factory for `card.issue-credit` page.
     */
    private final IssueCreditCardPageFactory issueCreditCardPageFactory;

    /**
     * @brief 信用附属卡页面工厂（Issue Supplementary Credit Card Page Factory）；
     *        Factory for `card.issue-supplementary-credit` page.
     */
    private final IssueSupplementaryCreditCardPageFactory issueSupplementaryCreditCardPageFactory;

    /**
     * @brief 卡详情页面工厂（Show Card Page Factory）；
     *        Factory for `card.show` page.
     */
    private final ShowCardPageFactory showCardPageFactory;

    /**
     * @brief 构造卡模块（Construct Card GUI Module）；
     *        Construct card module with page factories.
     *
     * @param issueDebitCardPageFactory 主借记卡页面工厂（Issue debit-card page factory）。
     * @param issueSupplementaryDebitCardPageFactory 借记附属卡页面工厂（Issue supplementary debit-card page factory）。
     * @param issueCreditCardPageFactory 主信用卡页面工厂（Issue credit-card page factory）。
     * @param issueSupplementaryCreditCardPageFactory 信用附属卡页面工厂（Issue supplementary credit-card page factory）。
     * @param showCardPageFactory 卡详情页面工厂（Show-card page factory）。
     */
    public CardGuiModule(
            final IssueDebitCardPageFactory issueDebitCardPageFactory,
            final IssueSupplementaryDebitCardPageFactory issueSupplementaryDebitCardPageFactory,
            final IssueCreditCardPageFactory issueCreditCardPageFactory,
            final IssueSupplementaryCreditCardPageFactory issueSupplementaryCreditCardPageFactory,
            final ShowCardPageFactory showCardPageFactory
    ) {
        this.issueDebitCardPageFactory = Objects.requireNonNull(issueDebitCardPageFactory, "issueDebitCardPageFactory must not be null");
        this.issueSupplementaryDebitCardPageFactory = Objects.requireNonNull(
                issueSupplementaryDebitCardPageFactory,
                "issueSupplementaryDebitCardPageFactory must not be null");
        this.issueCreditCardPageFactory = Objects.requireNonNull(issueCreditCardPageFactory, "issueCreditCardPageFactory must not be null");
        this.issueSupplementaryCreditCardPageFactory = Objects.requireNonNull(
                issueSupplementaryCreditCardPageFactory,
                "issueSupplementaryCreditCardPageFactory must not be null");
        this.showCardPageFactory = Objects.requireNonNull(showCardPageFactory, "showCardPageFactory must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerPages(final GuiPageRegistrar registrar) {
        final GuiPageRegistrar normalizedRegistrar = Objects.requireNonNull(registrar, "registrar must not be null");
        normalizedRegistrar
                .register(CardGuiPageIds.ISSUE_DEBIT_CARD, issueDebitCardPageFactory)
                .register(CardGuiPageIds.ISSUE_SUPPLEMENTARY_DEBIT_CARD, issueSupplementaryDebitCardPageFactory)
                .register(CardGuiPageIds.ISSUE_CREDIT_CARD, issueCreditCardPageFactory)
                .register(CardGuiPageIds.ISSUE_SUPPLEMENTARY_CREDIT_CARD, issueSupplementaryCreditCardPageFactory)
                .register(CardGuiPageIds.SHOW_CARD, showCardPageFactory);
    }
}
