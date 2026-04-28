package com.moesegfault.banking.presentation.gui.investment;

import com.moesegfault.banking.application.investment.command.SellProductHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.GuiPageId;
import java.util.Objects;

/**
 * @brief 卖出投资产品页面工厂（Sell Product Page Factory）；
 *        Page factory assembling sell-product model/view/controller trio.
 */
public final class SellProductPageFactory implements GuiPageFactory {

    /**
     * @brief 卖出应用服务（Sell Product Application Service）；
     *        Application handler for sell command.
     */
    private final SellProductHandler sellProductHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper shared by page controllers.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 构造页面工厂（Construct Sell Product Page Factory）；
     *        Construct sell-product page factory.
     *
     * @param sellProductHandler 卖出服务（Sell handler）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public SellProductPageFactory(
            final SellProductHandler sellProductHandler,
            final GuiExceptionHandler guiExceptionHandler
    ) {
        this.sellProductHandler = Objects.requireNonNull(sellProductHandler, "sellProductHandler must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuiPage createPage(final GuiContext context) {
        final GuiContext normalizedContext = Objects.requireNonNull(context, "context must not be null");

        final SellProductModel model = new SellProductModel();
        final SellProductView view = new SellProductView();
        final SellProductController controller = new SellProductController(
                sellProductHandler,
                normalizedContext,
                model,
                guiExceptionHandler);

        view.bindModel(model);
        view.setViewEventListener(controller::onViewEvent);
        return new GuiPage(GuiPageId.of(InvestmentGuiSchema.PAGE_SELL_PRODUCT), model, view, controller);
    }
}
