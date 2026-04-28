package com.moesegfault.banking.presentation.gui.card;

import com.moesegfault.banking.application.card.command.IssueCreditCardCommand;
import com.moesegfault.banking.application.card.command.IssueCreditCardHandler;
import com.moesegfault.banking.application.card.result.IssueCardResult;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 主信用卡发卡控制器（Issue Credit Card Controller），处理事件并调用应用层发卡服务；
 *        Controller for primary credit-card issuance that handles view events and invokes application handler.
 */
public final class IssueCreditCardController implements GuiController {

    /**
     * @brief 页面模型（Page Model）；
     *        Bound page model.
     */
    private final IssueCreditCardModel model;

    /**
     * @brief 应用层处理器（Application Handler）；
     *        Application handler for primary credit-card issuance.
     */
    private final IssueCreditCardHandler applicationHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper for user-facing error messages.
     */
    private final GuiExceptionHandler exceptionHandler;

    /**
     * @brief 构造控制器（Construct Controller）；
     *        Construct issue-credit-card controller.
     *
     * @param model 页面模型（Page model）。
     * @param applicationHandler 应用层处理器（Application handler）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public IssueCreditCardController(
            final IssueCreditCardModel model,
            final IssueCreditCardHandler applicationHandler,
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
        model.clearMessages();
        model.clearFieldErrors();
        model.setSubmitting(false);
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
            submit(normalizedEvent.attributes());
        }
    }

    /**
     * @brief 处理字段变更事件（Handle Field-change Event）；
     *        Apply single field change from view event.
     *
     * @param event 视图事件（View event）。
     */
    private void applyFieldChange(final ViewEvent event) {
        final String fieldName = CardGuiControllerSupport.optionalStringAttribute(event, CardGuiSchema.FIELD_NAME);
        final String fieldValue = CardGuiControllerSupport.optionalStringAttribute(event, CardGuiSchema.FIELD_VALUE);
        if (!fieldName.isEmpty()) {
            model.setFieldValue(fieldName, fieldValue);
        }
    }

    /**
     * @brief 提交发卡请求（Submit Issue Request）；
     *        Submit primary credit-card issuance request.
     *
     * @param attributes 事件属性（Event attributes）。
     */
    private void submit(final Map<String, Object> attributes) {
        model.clearMessages();
        model.clearFieldErrors();
        model.setFieldValues(extractValues(attributes));
        model.setSubmitting(true);
        try {
            final IssueCardResult result = applicationHandler.handle(new IssueCreditCardCommand(
                    model.fieldValue(CardGuiSchema.HOLDER_CUSTOMER_ID),
                    model.fieldValue(CardGuiSchema.CREDIT_CARD_ACCOUNT_ID),
                    model.fieldValue(CardGuiSchema.CARD_NO)));
            model.setIssueResult(result);
            model.setSuccessMessage(CardGuiControllerSupport.buildIssueSuccessMessage(result.cardId(), result.cardKind().name()));
        } catch (RuntimeException exception) {
            model.setErrorMessage(exceptionHandler.toUserMessage(exception));
        } finally {
            model.setSubmitting(false);
        }
    }

    /**
     * @brief 合并事件输入与模型快照（Merge Event Input With Model Snapshot）；
     *        Merge submit-event attributes with current model field values.
     *
     * @param attributes 事件属性（Event attributes）。
     * @return 合并字段值（Merged field values）。
     */
    private Map<String, String> extractValues(final Map<String, Object> attributes) {
        final Map<String, Object> normalizedAttributes = Objects.requireNonNull(attributes, "attributes must not be null");
        return Map.of(
                CardGuiSchema.HOLDER_CUSTOMER_ID,
                valueOrModel(normalizedAttributes, CardGuiSchema.HOLDER_CUSTOMER_ID),
                CardGuiSchema.CREDIT_CARD_ACCOUNT_ID,
                valueOrModel(normalizedAttributes, CardGuiSchema.CREDIT_CARD_ACCOUNT_ID),
                CardGuiSchema.CARD_NO,
                valueOrModel(normalizedAttributes, CardGuiSchema.CARD_NO));
    }

    /**
     * @brief 获取字段值（Get Field Value）；
     *        Get one field value from attributes or fall back to model snapshot.
     *
     * @param attributes 事件属性（Event attributes）。
     * @param fieldName 字段名（Field name）。
     * @return 字段值（Field value）。
     */
    private String valueOrModel(final Map<String, Object> attributes, final String fieldName) {
        final Object rawValue = attributes.get(fieldName);
        if (rawValue == null) {
            return model.fieldValue(fieldName);
        }
        return rawValue.toString();
    }
}
