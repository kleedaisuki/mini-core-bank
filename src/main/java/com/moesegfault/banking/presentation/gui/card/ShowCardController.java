package com.moesegfault.banking.presentation.gui.card;

import com.moesegfault.banking.application.card.query.FindCardHandler;
import com.moesegfault.banking.application.card.query.FindCardQuery;
import com.moesegfault.banking.application.card.result.CardResult;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 卡详情控制器（Show Card Controller），处理查询事件并调用应用层读服务；
 *        Controller for card-detail page that handles query events and invokes application read handler.
 */
public final class ShowCardController implements GuiController {

    /**
     * @brief 页面模型（Page Model）；
     *        Bound page model.
     */
    private final ShowCardModel model;

    /**
     * @brief 应用层处理器（Application Handler）；
     *        Application handler for card query.
     */
    private final FindCardHandler applicationHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper for user-facing error messages.
     */
    private final GuiExceptionHandler exceptionHandler;

    /**
     * @brief 构造控制器（Construct Controller）；
     *        Construct show-card controller.
     *
     * @param model 页面模型（Page model）。
     * @param applicationHandler 应用层处理器（Application handler）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public ShowCardController(
            final ShowCardModel model,
            final FindCardHandler applicationHandler,
            final GuiExceptionHandler exceptionHandler
    ) {
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.exceptionHandler = Objects.requireNonNull(exceptionHandler, "exceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        model.setLoading(false);
        model.setCardId("");
        model.clearFeedback();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewEvent(final ViewEvent event) {
        final ViewEvent normalizedEvent = Objects.requireNonNull(event, "event must not be null");
        if (CardGuiSchema.EVENT_FIELD_CHANGED.equals(normalizedEvent.type())) {
            applyFieldChange(normalizedEvent);
            return;
        }
        if (CardGuiSchema.EVENT_SUBMIT.equals(normalizedEvent.type())) {
            queryCard(normalizedEvent.attributes());
        }
    }

    /**
     * @brief 处理字段变更事件（Handle Field-change Event）；
     *        Apply card-id field change from view event.
     *
     * @param event 视图事件（View event）。
     */
    private void applyFieldChange(final ViewEvent event) {
        final String fieldName = CardGuiControllerSupport.optionalStringAttribute(event, CardGuiSchema.FIELD_NAME);
        if (!CardGuiSchema.CARD_ID.equals(fieldName)) {
            return;
        }
        final String fieldValue = CardGuiControllerSupport.optionalStringAttribute(event, CardGuiSchema.FIELD_VALUE);
        model.setCardId(fieldValue);
    }

    /**
     * @brief 查询卡片详情（Query Card Detail）；
     *        Query card detail by `card_id`.
     *
     * @param attributes 事件属性（Event attributes）。
     */
    private void queryCard(final Map<String, Object> attributes) {
        model.clearFeedback();
        final Map<String, Object> normalizedAttributes = Objects.requireNonNull(attributes, "attributes must not be null");
        final String submittedCardId = normalizedAttributes.get(CardGuiSchema.CARD_ID) == null
                ? model.cardId()
                : normalizedAttributes.get(CardGuiSchema.CARD_ID).toString();
        model.setCardId(submittedCardId);
        model.setLoading(true);
        try {
            final CardResult result = applicationHandler.handle(new FindCardQuery(model.cardId()));
            model.setCardResult(result);
        } catch (RuntimeException exception) {
            model.setErrorMessage(exceptionHandler.toUserMessage(exception));
        } finally {
            model.setLoading(false);
        }
    }
}
