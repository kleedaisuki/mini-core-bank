package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.application.account.query.FindAccountHandler;
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
 * @brief 账户详情页面工厂（Show Account Page Factory），创建账户详情页 MVC 组合；
 *        Show-account page factory creating MVC bundle for account-detail page.
 */
public final class ShowAccountPageFactory implements GuiPageFactory {

    /**
     * @brief 查询单账户应用服务（Find Account Application Service）;
     *        Application handler for account lookup.
     */
    private final FindAccountHandler applicationHandler;

    /**
     * @brief 查询表单视图提供器（Query Form View Supplier）;
     *        Supplier creating query-form view instance per page.
     */
    private final Supplier<FormView> queryFormViewSupplier;

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
     *        Construct show-account page factory.
     *
     * @param applicationHandler 查询单账户应用服务（Application handler）。
     * @param queryFormViewSupplier 查询表单视图提供器（Query-form view supplier）。
     * @param tableViewSupplier 表格视图提供器（Table-view supplier）。
     * @param emptyStateViewSupplier 空态视图提供器（Empty-state view supplier）。
     */
    public ShowAccountPageFactory(
            final FindAccountHandler applicationHandler,
            final Supplier<FormView> queryFormViewSupplier,
            final Supplier<TableView> tableViewSupplier,
            final Supplier<EmptyStateView> emptyStateViewSupplier
    ) {
        this(
                applicationHandler,
                queryFormViewSupplier,
                tableViewSupplier,
                emptyStateViewSupplier,
                AccountGuiDialogs.NOOP_ERROR_DIALOG,
                new GuiExceptionHandler());
    }

    /**
     * @brief 构造页面工厂（Construct Page Factory）;
     *        Construct show-account page factory with full dependencies.
     *
     * @param applicationHandler 查询单账户应用服务（Application handler）。
     * @param queryFormViewSupplier 查询表单视图提供器（Query-form view supplier）。
     * @param tableViewSupplier 表格视图提供器（Table-view supplier）。
     * @param emptyStateViewSupplier 空态视图提供器（Empty-state view supplier）。
     * @param errorDialogView 错误弹窗（Error dialog）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public ShowAccountPageFactory(
            final FindAccountHandler applicationHandler,
            final Supplier<FormView> queryFormViewSupplier,
            final Supplier<TableView> tableViewSupplier,
            final Supplier<EmptyStateView> emptyStateViewSupplier,
            final ErrorDialogView errorDialogView,
            final GuiExceptionHandler exceptionHandler
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.queryFormViewSupplier = Objects.requireNonNull(queryFormViewSupplier, "queryFormViewSupplier must not be null");
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
        final ShowAccountModel model = new ShowAccountModel();
        normalizedContext.currentCustomerId().ifPresent(customerId -> model.setQueryValues("", ""));
        final ShowAccountController controller = new ShowAccountController(
                applicationHandler,
                model,
                exceptionHandler);
        final ShowAccountView view = new ShowAccountView(
                Objects.requireNonNull(queryFormViewSupplier.get(), "queryFormViewSupplier must not return null"),
                Objects.requireNonNull(tableViewSupplier.get(), "tableViewSupplier must not return null"),
                Objects.requireNonNull(emptyStateViewSupplier.get(), "emptyStateViewSupplier must not return null"),
                errorDialogView,
                controller::onViewEvent);
        view.bindModel(model);
        return new GuiPage(AccountGuiPageIds.SHOW_ACCOUNT, model, view, controller);
    }
}
