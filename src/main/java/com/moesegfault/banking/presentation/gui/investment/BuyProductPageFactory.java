package com.moesegfault.banking.presentation.gui.investment;

import com.moesegfault.banking.application.investment.command.BuyProductHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.GuiPageId;
import java.util.Objects;

/**
 * @brief 买入投资产品页面工厂（Buy Product Page Factory）；
 *        Page factory assembling buy-product model/view/controller trio.
 */
public final class BuyProductPageFactory implements GuiPageFactory {

    /**
     * @brief 买入应用服务（Buy Product Application Service）；
     *        Application handler for buy command.
     */
    private final BuyProductHandler buyProductHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper shared by page controllers.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 构造页面工厂（Construct Buy Product Page Factory）；
     *        Construct buy-product page factory.
     *
     * @param buyProductHandler 买入服务（Buy handler）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public BuyProductPageFactory(
            final BuyProductHandler buyProductHandler,
            final GuiExceptionHandler guiExceptionHandler
    ) {
        this.buyProductHandler = Objects.requireNonNull(buyProductHandler, "buyProductHandler must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuiPage createPage(final GuiContext context) {
        final GuiContext normalizedContext = Objects.requireNonNull(context, "context must not be null");

        final BuyProductModel model = new BuyProductModel();
        final BuyProductView view = new BuyProductView();
        final BuyProductController controller = new BuyProductController(
                buyProductHandler,
                normalizedContext,
                model,
                guiExceptionHandler);

        view.bindModel(model);
        view.setViewEventListener(controller::onViewEvent);
        return new GuiPage(GuiPageId.of(InvestmentGuiSchema.PAGE_BUY_PRODUCT), model, view, controller);
    }
}
