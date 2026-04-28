package com.moesegfault.banking.presentation.gui.credit;

import com.moesegfault.banking.application.credit.command.RepayCreditCardHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @brief 信用卡还款页面工厂（Repay Credit Card Page Factory），负责创建还款页面 MVC 组合；
 *        Page factory for repay-credit-card page creating MVC bundle.
 */
public final class RepayCreditCardPageFactory implements GuiPageFactory {

    /**
     * @brief 信用卡还款应用服务（Repay Credit Card Application Service）；
     *        Application handler for credit-card repayment.
     */
    private final RepayCreditCardHandler applicationHandler;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        Exception mapper for controller failures.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 表单视图提供器（Form View Supplier）；
     *        Supplier creating form view instance.
     */
    private final Supplier<FormView> formViewSupplier;

    /**
     * @brief 表格视图提供器（Table View Supplier）；
     *        Supplier creating table view instance.
     */
    private final Supplier<TableView> tableViewSupplier;

    /**
     * @brief 构造页面工厂（Construct Page Factory）；
     *        Construct repay-credit-card page factory.
     *
     * @param applicationHandler 信用卡还款应用服务（Application handler）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     * @param formViewSupplier 表单视图提供器（Form view supplier）。
     * @param tableViewSupplier 表格视图提供器（Table view supplier）。
     */
    public RepayCreditCardPageFactory(
            final RepayCreditCardHandler applicationHandler,
            final GuiExceptionHandler guiExceptionHandler,
            final Supplier<FormView> formViewSupplier,
            final Supplier<TableView> tableViewSupplier
    ) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
        this.formViewSupplier = Objects.requireNonNull(formViewSupplier, "formViewSupplier must not be null");
        this.tableViewSupplier = Objects.requireNonNull(tableViewSupplier, "tableViewSupplier must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuiPage createPage(final GuiContext context) {
        Objects.requireNonNull(context, "context must not be null");
        final RepayCreditCardModel model = new RepayCreditCardModel();
        final RepayCreditCardView view = new RepayCreditCardView(
                Objects.requireNonNull(formViewSupplier.get(), "formViewSupplier must not return null"),
                Objects.requireNonNull(tableViewSupplier.get(), "tableViewSupplier must not return null"));
        final RepayCreditCardController controller = new RepayCreditCardController(
                applicationHandler,
                guiExceptionHandler,
                model);
        view.setViewEventHandler(controller::onViewEvent);
        view.bindModel(model);
        return new GuiPage(CreditGuiPageIds.REPAY_CREDIT_CARD, model, view, controller);
    }
}
