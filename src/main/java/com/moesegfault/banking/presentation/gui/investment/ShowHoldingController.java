package com.moesegfault.banking.presentation.gui.investment;

import com.moesegfault.banking.application.investment.query.ListHoldingsHandler;
import com.moesegfault.banking.application.investment.query.ListHoldingsQuery;
import com.moesegfault.banking.application.investment.result.HoldingResult;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 投资持仓页面控制器（Show Holding Page Controller）；
 *        Controller handling holdings query events and updating page model.
 */
public final class ShowHoldingController implements GuiController {

    /**
     * @brief 持仓查询应用服务（List Holdings Application Service）；
     *        Application handler for holdings query.
     */
    private final ListHoldingsHandler listHoldingsHandler;

    /**
     * @brief 页面模型（Page Model）；
     *        Bound page model to update.
     */
    private final ShowHoldingModel model;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper for user-facing error messages.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 构造控制器（Construct Show Holding Controller）；
     *        Construct show-holding controller.
     *
     * @param listHoldingsHandler 持仓查询服务（List-holdings handler）。
     * @param model 页面模型（Page model）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public ShowHoldingController(
            final ListHoldingsHandler listHoldingsHandler,
            final ShowHoldingModel model,
            final GuiExceptionHandler guiExceptionHandler
    ) {
        this.listHoldingsHandler = Objects.requireNonNull(listHoldingsHandler, "listHoldingsHandler must not be null");
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
        if (!InvestmentGuiSchema.EVENT_QUERY.equals(normalizedEvent.type())
                && !InvestmentGuiSchema.EVENT_SUBMIT.equals(normalizedEvent.type())) {
            return;
        }
        handleQuery(normalizedEvent.attributes());
    }

    /**
     * @brief 处理查询事件（Handle Query Event）；
     *        Handle query payload and execute holdings query.
     *
     * @param attributes 事件字段（Event payload attributes）。
     */
    private void handleQuery(final Map<String, Object> attributes) {
        final String investmentAccountId = InvestmentGuiPayloadParser.requiredText(
                attributes,
                InvestmentGuiSchema.FIELD_INVESTMENT_ACCOUNT_ID);
        final boolean includeProductDetails = InvestmentGuiPayloadParser.optionalBoolean(
                attributes,
                InvestmentGuiSchema.FIELD_INCLUDE_PRODUCT_DETAILS,
                false);

        model.updateQueryCondition(investmentAccountId, includeProductDetails);
        model.markLoading();
        try {
            final List<HoldingResult> holdings = listHoldingsHandler.handle(new ListHoldingsQuery(
                    investmentAccountId,
                    includeProductDetails));
            model.markSuccess(holdings, "查询成功，共 " + holdings.size() + " 条持仓");
        } catch (RuntimeException exception) {
            model.markFailure(guiExceptionHandler.toUserMessage(exception));
        }
    }
}
