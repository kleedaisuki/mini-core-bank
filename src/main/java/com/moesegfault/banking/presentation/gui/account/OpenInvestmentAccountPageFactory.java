package com.moesegfault.banking.presentation.gui.account;

import com.moesegfault.banking.application.account.command.OpenInvestmentAccountHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.view.ErrorDialogView;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.SuccessDialogView;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @brief 开立投资账户页面工厂（Open Investment Account Page Factory），创建投资开户页 MVC 组合；
 *        Open-investment-account page factory creating MVC bundle for investment opening page.
 */
public final class OpenInvestmentAccountPageFactory implements GuiPageFactory {

    /**
     * @brief 开投资账户应用服务（Open Investment Account Application Service）;
     *        Application handler for opening investment account.
     */
    private final OpenInvestmentAccountHandler applicationHandler;

    /**
     * @brief 表单视图提供器（Form View Supplier）;
     *        Supplier creating form-view instance per page.
     */
    private final Supplier<FormView> formViewSupplier;

    /**
     * @brief 错误弹窗端口（Error Dialog Port）;
     *        Error-dialog port.
     */
    private final ErrorDialogView errorDialogView;

    /**
     * @brief 成功弹窗端口（Success Dialog Port）;
     *        Success-dialog port.
     */
    private final SuccessDialogView successDialogView;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）;
     *        GUI exception handler.
     */
    private final GuiExceptionHandler exceptionHandler;

    /**
     * @brief 构造页面工厂（Construct Page Factory）;
     *        Construct open-investment-account page factory.
     *
     * @param applicationHandler 开投资账户应用服务（Application handler）。
     * @param formViewSupplier 表单视图提供器（Form-view supplier）。
     */
    public OpenInvestmentAccountPageFactory(
            final OpenInvestmentAccountHandler applicationHandler,
            final Supplier<FormView> formViewSupplier
    ) {
        this(
                applicationHandler,
                formViewSupplier,
                AccountGuiDialogs.NOOP_ERROR_DIALOG,
                AccountGuiDialogs.NOOP_SUCCESS_DIALOG,
                new GuiExceptionHandler());
    }

    /**
     * @brief 构造页面工厂（Construct Page Factory）;
     *        Construct open-investment-account page factory with full dependencies.
     *
     * @param applicationHandler 开投资账户应用服务（Application handler）。
     * @param formViewSupplier 表单视图提供器（Form-view supplier）。
     * @param errorDialogView 错误弹窗（Error dialog）。
     * @param successDialogView 成功弹窗（Success dialog）。
     * @param exceptionHandler GUI 异常处理器（GUI exception handler）。
     */
    public OpenInvestmentAccountPageFactory(
            final OpenInvestmentAccountHandler applicationHandler,
            final Supplier<FormView> formViewSupplier,
            final ErrorDialogView errorDialogView,
            final SuccessDialogView successDialogView,
            final GuiExceptionHandler exceptionHandler
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.formViewSupplier = Objects.requireNonNull(formViewSupplier, "formViewSupplier must not be null");
        this.errorDialogView = Objects.requireNonNull(errorDialogView, "errorDialogView must not be null");
        this.successDialogView = Objects.requireNonNull(successDialogView, "successDialogView must not be null");
        this.exceptionHandler = Objects.requireNonNull(exceptionHandler, "exceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuiPage createPage(final GuiContext context) {
        final GuiContext normalizedContext = Objects.requireNonNull(context, "context must not be null");
        final OpenInvestmentAccountModel model = new OpenInvestmentAccountModel();
        normalizedContext.currentCustomerId().ifPresent(customerId -> model.setFormValues(customerId, ""));
        final OpenInvestmentAccountController controller = new OpenInvestmentAccountController(
                applicationHandler,
                model,
                exceptionHandler);
        final OpenInvestmentAccountView view = new OpenInvestmentAccountView(
                Objects.requireNonNull(formViewSupplier.get(), "formViewSupplier must not return null"),
                errorDialogView,
                successDialogView,
                controller::onViewEvent);
        view.bindModel(model);
        return new GuiPage(AccountGuiPageIds.OPEN_INVESTMENT_ACCOUNT, model, view, controller);
    }
}
