package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.application.account.command.OpenFxAccountCommand;
import com.moesegfault.banking.application.account.command.OpenFxAccountHandler;
import com.moesegfault.banking.application.account.result.OpenAccountResult;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 开立外汇账户控制器（Open FX Account Controller），处理 FX 开户提交事件；
 *        Open-FX-account controller handling FX opening submit events.
 */
public final class OpenFxAccountController implements GuiController {

    /**
     * @brief 提交事件类型（Submit Event Type）;
     *        View-event type for form submit.
     */
    public static final String EVENT_SUBMIT = "open_fx_account.submit";

    /**
     * @brief 开外汇账户应用服务（Open FX Account Application Service）;
     *        Application handler for opening FX account.
     */
    private final OpenFxAccountHandler applicationHandler;

    /**
     * @brief 页面模型（Page Model）;
     *        Page model.
     */
    private final OpenFxAccountModel model;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）;
     *        GUI exception handler.
     */
    private final GuiExceptionHandler exceptionHandler;

    /**
     * @brief 构造控制器（Construct Controller）;
     *        Construct open-FX-account controller.
     *
     * @param applicationHandler 开外汇账户应用服务（Application handler）。
     * @param model 页面模型（Page model）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public OpenFxAccountController(
            final OpenFxAccountHandler applicationHandler,
            final OpenFxAccountModel model,
            final GuiExceptionHandler exceptionHandler
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.exceptionHandler = Objects.requireNonNull(exceptionHandler, "exceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        model.setSubmitting(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewEvent(final ViewEvent event) {
        final ViewEvent normalizedEvent = Objects.requireNonNull(event, "event must not be null");
        if (!EVENT_SUBMIT.equals(normalizedEvent.type())) {
            return;
        }
        handleSubmit(normalizedEvent.attributes().get("values"));
    }

    /**
     * @brief 处理提交动作（Handle Submit Action）;
     *        Handle submit event payload and execute FX opening use case.
     *
     * @param rawValues 原始表单值（Raw form values）。
     */
    private void handleSubmit(final Object rawValues) {
        model.setSubmitting(true);
        try {
            final Map<String, String> values = AccountGuiInputReader.asFormValues(rawValues);
            final String customerId = AccountGuiInputReader.required(values, AccountGuiSchema.CUSTOMER_ID);
            final String accountNo = AccountGuiInputReader.required(values, AccountGuiSchema.ACCOUNT_NO);
            final String linkedSavingsAccountId = AccountGuiInputReader.required(values, AccountGuiSchema.LINKED_SAVINGS_ACCOUNT_ID);
            model.setFormValues(customerId, accountNo, linkedSavingsAccountId);
            final OpenAccountResult result = applicationHandler.handle(
                    new OpenFxAccountCommand(customerId, accountNo, linkedSavingsAccountId));
            model.setSuccessResult(result);
        } catch (RuntimeException exception) {
            model.setErrorMessage(exceptionHandler.toUserMessage(exception));
        } finally {
            model.setSubmitting(false);
        }
    }
}
