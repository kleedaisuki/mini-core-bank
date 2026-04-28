package com.moesegfault.banking.presentation.gui.investment;

import com.moesegfault.banking.application.investment.command.BuyProductCommand;
import com.moesegfault.banking.application.investment.command.BuyProductHandler;
import com.moesegfault.banking.application.investment.result.InvestmentOrderResult;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 买入投资产品页面控制器（Buy Product Page Controller）；
 *        Controller handling buy-order submit events and updating page model.
 */
public final class BuyProductController implements GuiController {

    /**
     * @brief 买入应用服务（Buy Product Application Service）；
     *        Application handler for buy command.
     */
    private final BuyProductHandler buyProductHandler;

    /**
     * @brief GUI 会话上下文（GUI Session Context）；
     *        GUI context used for default initiator customer propagation.
     */
    private final GuiContext guiContext;

    /**
     * @brief 页面模型（Page Model）；
     *        Bound page model to update.
     */
    private final BuyProductModel model;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper for user-facing error messages.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 构造控制器（Construct Buy Product Controller）；
     *        Construct buy-product controller.
     *
     * @param buyProductHandler 买入服务（Buy handler）。
     * @param guiContext GUI 上下文（GUI context）。
     * @param model 页面模型（Page model）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public BuyProductController(
            final BuyProductHandler buyProductHandler,
            final GuiContext guiContext,
            final BuyProductModel model,
            final GuiExceptionHandler guiExceptionHandler
    ) {
        this.buyProductHandler = Objects.requireNonNull(buyProductHandler, "buyProductHandler must not be null");
        this.guiContext = Objects.requireNonNull(guiContext, "guiContext must not be null");
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        guiContext.currentCustomerId().ifPresent(model::fillInitiatorCustomerIdIfAbsent);
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
     *        Handle form submit payload and execute buy command.
     *
     * @param attributes 事件字段（Event payload attributes）。
     */
    private void handleSubmit(final Map<String, Object> attributes) {
        model.markSubmitting();
        try {
            final BuyProductCommand command = new BuyProductCommand(
                    InvestmentGuiPayloadParser.requiredText(attributes, InvestmentGuiSchema.FIELD_INVESTMENT_ACCOUNT_ID),
                    InvestmentGuiPayloadParser.requiredText(attributes, InvestmentGuiSchema.FIELD_PRODUCT_CODE),
                    InvestmentGuiPayloadParser.requiredBigDecimal(attributes, InvestmentGuiSchema.FIELD_QUANTITY),
                    InvestmentGuiPayloadParser.requiredBigDecimal(attributes, InvestmentGuiSchema.FIELD_PRICE),
                    InvestmentGuiPayloadParser.optionalBigDecimal(attributes, InvestmentGuiSchema.FIELD_FEE_AMOUNT),
                    resolveInitiatorCustomerId(attributes),
                    InvestmentGuiPayloadParser.optionalBusinessChannel(attributes, InvestmentGuiSchema.FIELD_CHANNEL),
                    InvestmentGuiPayloadParser.optionalText(attributes, InvestmentGuiSchema.FIELD_REFERENCE_NO),
                    InvestmentGuiPayloadParser.optionalRiskLevel(attributes, InvestmentGuiSchema.FIELD_CUSTOMER_RISK_TOLERANCE),
                    InvestmentGuiPayloadParser.optionalInstant(attributes, InvestmentGuiSchema.FIELD_TRADE_AT));

            final InvestmentOrderResult result = buyProductHandler.handle(command);
            model.markSuccess(result, "买入成功: order_id=" + result.orderId());
        } catch (RuntimeException exception) {
            model.markFailure(guiExceptionHandler.toUserMessage(exception));
        }
    }

    /**
     * @brief 解析发起客户 ID（Resolve Initiator Customer ID）；
     *        Resolve initiator customer id from payload first, fallback to GUI context.
     *
     * @param attributes 事件字段（Event payload attributes）。
     * @return 发起客户 ID 或 null（Initiator customer id or null）。
     */
    private String resolveInitiatorCustomerId(final Map<String, Object> attributes) {
        final String fromPayload = InvestmentGuiPayloadParser.optionalText(
                attributes,
                InvestmentGuiSchema.FIELD_INITIATOR_CUSTOMER_ID);
        if (fromPayload != null) {
            return fromPayload;
        }
        return guiContext.currentCustomerId().orElse(null);
    }
}
