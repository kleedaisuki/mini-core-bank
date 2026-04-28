package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.application.account.query.ListCustomerAccountsHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.view.EmptyStateView;
import com.moesegfault.banking.presentation.gui.view.ErrorDialogView;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @brief 账户列表页面工厂（List Accounts Page Factory），创建账户列表页 MVC 组合；
 *        List-accounts page factory creating MVC bundle for account-list page.
 */
public final class ListAccountsPageFactory implements GuiPageFactory {

    /**
     * @brief 客户账户列表应用服务（List Customer Accounts Application Service）;
     *        Application handler for listing customer accounts.
     */
    private final ListCustomerAccountsHandler applicationHandler;

    /**
     * @brief 筛选表单视图提供器（Filter Form View Supplier）;
     *        Supplier creating filter-form view instance per page.
     */
    private final Supplier<FormView> filterFormViewSupplier;

    /**
     * @brief 表格视图提供器（Table View Supplier）;
     *        Supplier creating table-view instance per page.
     */
    private final Supplier<TableView> tableViewSupplier;

    /**
     * @brief 空态视图提供器（Empty-state View Supplier）;
     *        Supplier creating empty-state view instance per page.
     */
    private final Supplier<EmptyStateView> emptyStateViewSupplier;

    /**
     * @brief 错误弹窗端口（Error Dialog Port）;
     *        Error-dialog port.
     */
    private final ErrorDialogView errorDialogView;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）;
     *        GUI exception handler.
     */
    private final GuiExceptionHandler exceptionHandler;

    /**
     * @brief 构造页面工厂（Construct Page Factory）;
     *        Construct list-accounts page factory.
     *
     * @param applicationHandler 客户账户列表应用服务（Application handler）。
     * @param filterFormViewSupplier 筛选表单视图提供器（Filter-form view supplier）。
     * @param tableViewSupplier 表格视图提供器（Table-view supplier）。
     * @param emptyStateViewSupplier 空态视图提供器（Empty-state view supplier）。
     */
    public ListAccountsPageFactory(
            final ListCustomerAccountsHandler applicationHandler,
            final Supplier<FormView> filterFormViewSupplier,
            final Supplier<TableView> tableViewSupplier,
            final Supplier<EmptyStateView> emptyStateViewSupplier
    ) {
        this(
                applicationHandler,
                filterFormViewSupplier,
                tableViewSupplier,
                emptyStateViewSupplier,
                AccountGuiDialogs.NOOP_ERROR_DIALOG,
                new GuiExceptionHandler());
    }

    /**
     * @brief 构造页面工厂（Construct Page Factory）;
     *        Construct list-accounts page factory with full dependencies.
     *
     * @param applicationHandler 客户账户列表应用服务（Application handler）。
     * @param filterFormViewSupplier 筛选表单视图提供器（Filter-form view supplier）。
     * @param tableViewSupplier 表格视图提供器（Table-view supplier）。
     * @param emptyStateViewSupplier 空态视图提供器（Empty-state view supplier）。
     * @param errorDialogView 错误弹窗（Error dialog）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public ListAccountsPageFactory(
            final ListCustomerAccountsHandler applicationHandler,
            final Supplier<FormView> filterFormViewSupplier,
            final Supplier<TableView> tableViewSupplier,
            final Supplier<EmptyStateView> emptyStateViewSupplier,
            final ErrorDialogView errorDialogView,
            final GuiExceptionHandler exceptionHandler
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.filterFormViewSupplier = Objects.requireNonNull(filterFormViewSupplier, "filterFormViewSupplier must not be null");
        this.tableViewSupplier = Objects.requireNonNull(tableViewSupplier, "tableViewSupplier must not be null");
        this.emptyStateViewSupplier = Objects.requireNonNull(emptyStateViewSupplier, "emptyStateViewSupplier must not be null");
        this.errorDialogView = Objects.requireNonNull(errorDialogView, "errorDialogView must not be null");
        this.exceptionHandler = Objects.requireNonNull(exceptionHandler, "exceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuiPage createPage(final GuiContext context) {
        final GuiContext normalizedContext = Objects.requireNonNull(context, "context must not be null");
        final ListAccountsModel model = new ListAccountsModel();
        normalizedContext.currentCustomerId().ifPresent(customerId -> model.setFilterValues(customerId, false));
        final ListAccountsController controller = new ListAccountsController(
                applicationHandler,
                model,
                exceptionHandler);
        final ListAccountsView view = new ListAccountsView(
                Objects.requireNonNull(filterFormViewSupplier.get(), "filterFormViewSupplier must not return null"),
                Objects.requireNonNull(tableViewSupplier.get(), "tableViewSupplier must not return null"),
                Objects.requireNonNull(emptyStateViewSupplier.get(), "emptyStateViewSupplier must not return null"),
                errorDialogView,
                controller::onViewEvent);
        view.bindModel(model);
        return new GuiPage(AccountGuiPageIds.LIST_ACCOUNTS, model, view, controller);
    }
}
