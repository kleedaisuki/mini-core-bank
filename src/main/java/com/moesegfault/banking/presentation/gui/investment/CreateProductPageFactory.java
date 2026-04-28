package com.moesegfault.banking.presentation.gui.investment;

import com.moesegfault.banking.application.investment.command.CreateInvestmentProductHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.GuiPageId;
import java.util.Objects;

/**
 * @brief 创建投资产品页面工厂（Create Product Page Factory）；
 *        Page factory assembling create-product model/view/controller trio.
 */
public final class CreateProductPageFactory implements GuiPageFactory {

    /**
     * @brief 创建产品应用服务（Create Product Application Service）；
     *        Application handler for create-product command.
     */
    private final CreateInvestmentProductHandler createInvestmentProductHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper shared by page controllers.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 构造页面工厂（Construct Create Product Page Factory）；
     *        Construct create-product page factory.
     *
     * @param createInvestmentProductHandler 创建产品服务（Create-product handler）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public CreateProductPageFactory(
            final CreateInvestmentProductHandler createInvestmentProductHandler,
            final GuiExceptionHandler guiExceptionHandler
    ) {
        this.createInvestmentProductHandler = Objects.requireNonNull(
                createInvestmentProductHandler,
                "createInvestmentProductHandler must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuiPage createPage(final GuiContext context) {
        Objects.requireNonNull(context, "context must not be null");

        final CreateProductModel model = new CreateProductModel();
        final CreateProductView view = new CreateProductView();
        final CreateProductController controller = new CreateProductController(
                createInvestmentProductHandler,
                model,
                guiExceptionHandler);

        view.bindModel(model);
        view.setViewEventListener(controller::onViewEvent);
        return new GuiPage(GuiPageId.of(InvestmentGuiSchema.PAGE_CREATE_PRODUCT), model, view, controller);
    }
}
