package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.application.account.command.OpenSavingsAccountCommand;
import com.moesegfault.banking.application.account.command.OpenSavingsAccountHandler;
import com.moesegfault.banking.application.account.result.OpenAccountResult;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 开立储蓄账户控制器（Open Savings Account Controller），处理提交事件并调用应用层开户服务；
 *        Open-savings-account controller handling submit events and invoking application use case.
 */
public final class OpenSavingsAccountController implements GuiController {

    /**
     * @brief 提交事件类型（Submit Event Type）;
     *        View-event type for form submit.
     */
    public static final String EVENT_SUBMIT = "open_savings_account.submit";

    /**
     * @brief 开储蓄账户应用服务（Open Savings Account Application Service）;
     *        Application handler for opening savings account.
     */
    private final OpenSavingsAccountHandler applicationHandler;

    /**
     * @brief 页面模型（Page Model）;
     *        Page model.
     */
    private final OpenSavingsAccountModel model;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）;
     *        GUI exception handler.
     */
    private final GuiExceptionHandler exceptionHandler;

    /**
     * @brief 构造控制器（Construct Controller）;
     *        Construct open-savings-account controller.
     *
     * @param applicationHandler 开储蓄账户应用服务（Application handler）。
     * @param model 页面模型（Page model）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public OpenSavingsAccountController(
            final OpenSavingsAccountHandler applicationHandler,
            final OpenSavingsAccountModel model,
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
     *        Handle submit event payload and execute account-opening use case.
     *
     * @param rawValues 原始表单值（Raw form values）。
     */
    private void handleSubmit(final Object rawValues) {
        model.setSubmitting(true);
        try {
            final Map<String, String> values = AccountGuiInputReader.asFormValues(rawValues);
            final String customerId = AccountGuiInputReader.required(values, AccountGuiSchema.CUSTOMER_ID);
            final String accountNo = AccountGuiInputReader.required(values, AccountGuiSchema.ACCOUNT_NO);
            model.setFormValues(customerId, accountNo);
            final OpenAccountResult result = applicationHandler.handle(new OpenSavingsAccountCommand(customerId, accountNo));
            model.setSuccessResult(result);
        } catch (RuntimeException exception) {
            model.setErrorMessage(exceptionHandler.toUserMessage(exception));
        } finally {
            model.setSubmitting(false);
        }
    }
}
