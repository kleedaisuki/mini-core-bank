package com.moesegfault.banking.presentation.gui.investment;

import com.moesegfault.banking.application.investment.command.CreateInvestmentProductCommand;
import com.moesegfault.banking.application.investment.command.CreateInvestmentProductHandler;
import com.moesegfault.banking.application.investment.result.InvestmentProductResult;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 创建投资产品页面控制器（Create Product Page Controller）；
 *        Controller handling create-product submit events and updating page model.
 */
public final class CreateProductController implements GuiController {

    /**
     * @brief 创建产品应用服务（Create Product Application Service）；
     *        Application handler for create-product command.
     */
    private final CreateInvestmentProductHandler createInvestmentProductHandler;

    /**
     * @brief 页面模型（Page Model）；
     *        Bound page model to update.
     */
    private final CreateProductModel model;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper for user-facing error messages.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 构造控制器（Construct Create Product Controller）；
     *        Construct create-product controller.
     *
     * @param createInvestmentProductHandler 创建产品服务（Create-product handler）。
     * @param model 页面模型（Page model）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public CreateProductController(
            final CreateInvestmentProductHandler createInvestmentProductHandler,
            final CreateProductModel model,
            final GuiExceptionHandler guiExceptionHandler
    ) {
        this.createInvestmentProductHandler = Objects.requireNonNull(
                createInvestmentProductHandler,
                "createInvestmentProductHandler must not be null");
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewEvent(final ViewEvent event) {
        final ViewEvent normalizedEvent = Objects.requireNonNull(event, "event must not be null");
        if (!InvestmentGuiSchema.EVENT_SUBMIT.equals(normalizedEvent.type())) {
            return;
        }
        handleSubmit(normalizedEvent.attributes());
    }

    /**
     * @brief 处理提交事件（Handle Submit Event）；
     *        Handle form submit payload and execute create-product command.
     *
     * @param attributes 事件字段（Event payload attributes）。
     */
    private void handleSubmit(final Map<String, Object> attributes) {
        model.markSubmitting();
        try {
            final CreateInvestmentProductCommand command = new CreateInvestmentProductCommand(
                    InvestmentGuiPayloadParser.requiredText(attributes, InvestmentGuiSchema.FIELD_PRODUCT_CODE),
                    InvestmentGuiPayloadParser.requiredText(attributes, InvestmentGuiSchema.FIELD_PRODUCT_NAME),
                    InvestmentGuiPayloadParser.requiredProductType(attributes, InvestmentGuiSchema.FIELD_PRODUCT_TYPE),
                    CurrencyCode.of(InvestmentGuiPayloadParser.requiredText(attributes, InvestmentGuiSchema.FIELD_CURRENCY_CODE)),
                    InvestmentGuiPayloadParser.requiredRiskLevel(attributes, InvestmentGuiSchema.FIELD_RISK_LEVEL),
                    InvestmentGuiPayloadParser.requiredText(attributes, InvestmentGuiSchema.FIELD_ISSUER));

            final InvestmentProductResult result = createInvestmentProductHandler.handle(command);
            model.markSuccess(result, "投资产品创建成功: " + result.productCode());
        } catch (RuntimeException exception) {
            model.markFailure(guiExceptionHandler.toUserMessage(exception));
        }
    }
}
