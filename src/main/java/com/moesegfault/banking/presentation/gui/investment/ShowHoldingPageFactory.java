package com.moesegfault.banking.presentation.gui.investment;

import com.moesegfault.banking.application.investment.query.ListHoldingsHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.GuiPageId;
import java.util.Objects;

/**
 * @brief 投资持仓页面工厂（Show Holding Page Factory）；
 *        Page factory assembling holdings model/view/controller trio.
 */
public final class ShowHoldingPageFactory implements GuiPageFactory {

    /**
     * @brief 持仓查询应用服务（List Holdings Application Service）；
     *        Application handler for holdings query.
     */
    private final ListHoldingsHandler listHoldingsHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper shared by page controllers.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 构造页面工厂（Construct Show Holding Page Factory）；
     *        Construct holdings page factory.
     *
     * @param listHoldingsHandler 持仓查询服务（List-holdings handler）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public ShowHoldingPageFactory(
            final ListHoldingsHandler listHoldingsHandler,
            final GuiExceptionHandler guiExceptionHandler
    ) {
        this.listHoldingsHandler = Objects.requireNonNull(listHoldingsHandler, "listHoldingsHandler must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuiPage createPage(final GuiContext context) {
        Objects.requireNonNull(context, "context must not be null");

        final ShowHoldingModel model = new ShowHoldingModel();
        final ShowHoldingView view = new ShowHoldingView();
        final ShowHoldingController controller = new ShowHoldingController(
                listHoldingsHandler,
                model,
                guiExceptionHandler);

        view.bindModel(model);
        view.setViewEventListener(controller::onViewEvent);
        return new GuiPage(GuiPageId.of(InvestmentGuiSchema.PAGE_SHOW_HOLDING), model, view, controller);
    }
}
