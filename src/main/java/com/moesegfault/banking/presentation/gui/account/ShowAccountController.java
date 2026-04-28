package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.application.account.query.FindAccountHandler;
import com.moesegfault.banking.application.account.query.FindAccountQuery;
import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 账户详情控制器（Show Account Controller），处理账户查询事件并调用应用层查询服务；
 *        Show-account controller handling lookup events and invoking query use case.
 */
public final class ShowAccountController implements GuiController {

    /**
     * @brief 提交事件类型（Submit Event Type）;
     *        View-event type for query submit.
     */
    public static final String EVENT_SUBMIT = "show_account.submit";

    /**
     * @brief 查询单账户应用服务（Find Account Application Service）;
     *        Application handler for account lookup.
     */
    private final FindAccountHandler applicationHandler;

    /**
     * @brief 页面模型（Page Model）;
     *        Page model.
     */
    private final ShowAccountModel model;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）;
     *        GUI exception handler.
     */
    private final GuiExceptionHandler exceptionHandler;

    /**
     * @brief 构造控制器（Construct Controller）;
     *        Construct show-account controller.
     *
     * @param applicationHandler 查询单账户应用服务（Application handler）。
     * @param model 页面模型（Page model）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public ShowAccountController(
            final FindAccountHandler applicationHandler,
            final ShowAccountModel model,
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
        model.setLoading(false);
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
     * @brief 处理查询提交（Handle Query Submit）;
     *        Handle lookup submit payload and execute account query use case.
     *
     * @param rawValues 原始表单值（Raw form values）。
     */
    private void handleSubmit(final Object rawValues) {
        model.setLoading(true);
        try {
            final Map<String, String> values = AccountGuiInputReader.asFormValues(rawValues);
            final String accountId = AccountGuiInputReader.optional(values, AccountGuiSchema.ACCOUNT_ID);
            final String accountNo = AccountGuiInputReader.optional(values, AccountGuiSchema.ACCOUNT_NO);
            model.setQueryValues(accountId, accountNo);
            final boolean hasAccountId = !accountId.isEmpty();
            final boolean hasAccountNo = !accountNo.isEmpty();
            if (hasAccountId == hasAccountNo) {
                throw new IllegalArgumentException("Exactly one of account_id or account_no must be provided");
            }
            final FindAccountQuery query = hasAccountId
                    ? FindAccountQuery.byAccountId(accountId)
                    : FindAccountQuery.byAccountNo(accountNo);
            final AccountResult result = applicationHandler.handle(query);
            model.setAccountResult(result);
        } catch (RuntimeException exception) {
            model.setErrorMessage(exceptionHandler.toUserMessage(exception));
        } finally {
            model.setLoading(false);
        }
    }
}
