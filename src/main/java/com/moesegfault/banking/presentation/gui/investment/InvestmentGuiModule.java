package com.moesegfault.banking.presentation.gui.investment;

import com.moesegfault.banking.application.investment.command.BuyProductHandler;
import com.moesegfault.banking.application.investment.command.CreateInvestmentProductHandler;
import com.moesegfault.banking.application.investment.command.SellProductHandler;
import com.moesegfault.banking.application.investment.query.ListHoldingsHandler;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiModule;
import com.moesegfault.banking.presentation.gui.GuiPageRegistrar;
import java.util.Objects;

/**
 * @brief 投资 GUI 模块（Investment GUI Module）；
 *        GUI module registering investment page factories into page registrar.
 */
public final class InvestmentGuiModule implements GuiModule {

    /**
     * @brief 创建产品页面工厂（Create Product Page Factory）；
     *        Factory for create-product page.
     */
    private final CreateProductPageFactory createProductPageFactory;

    /**
     * @brief 买入页面工厂（Buy Product Page Factory）；
     *        Factory for buy-product page.
     */
    private final BuyProductPageFactory buyProductPageFactory;

    /**
     * @brief 卖出页面工厂（Sell Product Page Factory）；
     *        Factory for sell-product page.
     */
    private final SellProductPageFactory sellProductPageFactory;

    /**
     * @brief 持仓页面工厂（Show Holding Page Factory）；
     *        Factory for holdings-query page.
     */
    private final ShowHoldingPageFactory showHoldingPageFactory;

    /**
     * @brief 构造模块并自动创建异常处理器（Construct Module with Default Exception Handler）；
     *        Construct module with default GUI exception handler.
     *
     * @param createInvestmentProductHandler 创建产品服务（Create-product handler）。
     * @param buyProductHandler 买入服务（Buy handler）。
     * @param sellProductHandler 卖出服务（Sell handler）。
     * @param listHoldingsHandler 持仓查询服务（List-holdings handler）。
     */
    public InvestmentGuiModule(
            final CreateInvestmentProductHandler createInvestmentProductHandler,
            final BuyProductHandler buyProductHandler,
            final SellProductHandler sellProductHandler,
            final ListHoldingsHandler listHoldingsHandler
    ) {
        this(
                createInvestmentProductHandler,
                buyProductHandler,
                sellProductHandler,
                listHoldingsHandler,
                new GuiExceptionHandler());
    }

    /**
     * @brief 构造模块（Construct Investment GUI Module）；
     *        Construct investment GUI module with injected collaborators.
     *
     * @param createInvestmentProductHandler 创建产品服务（Create-product handler）。
     * @param buyProductHandler 买入服务（Buy handler）。
     * @param sellProductHandler 卖出服务（Sell handler）。
     * @param listHoldingsHandler 持仓查询服务（List-holdings handler）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public InvestmentGuiModule(
            final CreateInvestmentProductHandler createInvestmentProductHandler,
            final BuyProductHandler buyProductHandler,
            final SellProductHandler sellProductHandler,
            final ListHoldingsHandler listHoldingsHandler,
            final GuiExceptionHandler guiExceptionHandler
    ) {
        final GuiExceptionHandler normalizedExceptionHandler = Objects.requireNonNull(
                guiExceptionHandler,
                "guiExceptionHandler must not be null");
        this.createProductPageFactory = new CreateProductPageFactory(
                Objects.requireNonNull(createInvestmentProductHandler, "createInvestmentProductHandler must not be null"),
                normalizedExceptionHandler);
        this.buyProductPageFactory = new BuyProductPageFactory(
                Objects.requireNonNull(buyProductHandler, "buyProductHandler must not be null"),
                normalizedExceptionHandler);
        this.sellProductPageFactory = new SellProductPageFactory(
                Objects.requireNonNull(sellProductHandler, "sellProductHandler must not be null"),
                normalizedExceptionHandler);
        this.showHoldingPageFactory = new ShowHoldingPageFactory(
                Objects.requireNonNull(listHoldingsHandler, "listHoldingsHandler must not be null"),
                normalizedExceptionHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerPages(final GuiPageRegistrar registrar) {
        final GuiPageRegistrar normalizedRegistrar = Objects.requireNonNull(registrar, "registrar must not be null");
        normalizedRegistrar
                .register(InvestmentGuiSchema.PAGE_CREATE_PRODUCT, createProductPageFactory)
                .register(InvestmentGuiSchema.PAGE_BUY_PRODUCT, buyProductPageFactory)
                .register(InvestmentGuiSchema.PAGE_SELL_PRODUCT, sellProductPageFactory)
                .register(InvestmentGuiSchema.PAGE_SHOW_HOLDING, showHoldingPageFactory);
    }
}
