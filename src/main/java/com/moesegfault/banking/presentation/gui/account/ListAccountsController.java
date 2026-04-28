package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.application.account.query.ListCustomerAccountsHandler;
import com.moesegfault.banking.application.account.query.ListCustomerAccountsQuery;
import com.moesegfault.banking.application.account.result.AccountResult;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.mvc.GuiController;
import com.moesegfault.banking.presentation.gui.mvc.ViewEvent;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @brief 账户列表控制器（List Accounts Controller），处理筛选和选择事件并调用账户列表查询服务；
 *        List-accounts controller handling filter/select events and invoking list-accounts query use case.
 */
public final class ListAccountsController implements GuiController {

    /**
     * @brief 提交事件类型（Submit Event Type）;
     *        View-event type for list query submit.
     */
    public static final String EVENT_SUBMIT = "list_accounts.submit";

    /**
     * @brief 行选中事件类型（Row-selected Event Type）;
     *        View-event type for row selection.
     */
    public static final String EVENT_ROW_SELECTED = "list_accounts.row_selected";

    /**
     * @brief 客户账户列表应用服务（List Customer Accounts Application Service）;
     *        Application handler for listing customer accounts.
     */
    private final ListCustomerAccountsHandler applicationHandler;

    /**
     * @brief 页面模型（Page Model）;
     *        Page model.
     */
    private final ListAccountsModel model;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）;
     *        GUI exception handler.
     */
    private final GuiExceptionHandler exceptionHandler;

    /**
     * @brief 构造控制器（Construct Controller）;
     *        Construct list-accounts controller.
     *
     * @param applicationHandler 客户账户列表应用服务（Application handler）。
     * @param model 页面模型（Page model）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public ListAccountsController(
            final ListCustomerAccountsHandler applicationHandler,
            final ListAccountsModel model,
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
        if (EVENT_SUBMIT.equals(normalizedEvent.type())) {
            handleSubmit(normalizedEvent.attributes().get("values"));
            return;
        }
        if (EVENT_ROW_SELECTED.equals(normalizedEvent.type())) {
            handleRowSelected(normalizedEvent.attributes().get("selected_row_index"));
        }
    }

    /**
     * @brief 处理筛选提交（Handle Filter Submit）;
     *        Handle filter-submit payload and execute list query use case.
     *
     * @param rawValues 原始表单值（Raw form values）。
     */
    private void handleSubmit(final Object rawValues) {
        model.setLoading(true);
        try {
            final Map<String, String> values = AccountGuiInputReader.asFormValues(rawValues);
            final String customerId = AccountGuiInputReader.required(values, AccountGuiSchema.CUSTOMER_ID);
            final boolean includeClosedAccounts = AccountGuiInputReader.optionalBoolean(
                    values,
                    AccountGuiSchema.INCLUDE_CLOSED_ACCOUNTS,
                    false);
            model.setFilterValues(customerId, includeClosedAccounts);
            final List<AccountResult> accountResults = applicationHandler.handle(
                    new ListCustomerAccountsQuery(customerId, includeClosedAccounts));
            model.setAccountResults(accountResults);
        } catch (RuntimeException exception) {
            model.setErrorMessage(exceptionHandler.toUserMessage(exception));
        } finally {
            model.setLoading(false);
        }
    }

    /**
     * @brief 处理行选择事件（Handle Row-selection Event）;
     *        Handle row-selection payload.
     *
     * @param rawSelectedRowIndex 原始行下标（Raw selected-row index）。
     */
    private void handleRowSelected(final Object rawSelectedRowIndex) {
        if (!(rawSelectedRowIndex instanceof Integer selectedRowIndex)) {
            throw new IllegalArgumentException("selected_row_index must be Integer");
        }
        model.setSelectedRowIndex(selectedRowIndex);
    }
}
