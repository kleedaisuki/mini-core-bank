package com.moesegfault.banking.presentation.gui.credit;

import com.moesegfault.banking.application.credit.query.FindStatementHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @brief 信用卡账单查询页面工厂（Show Statement Page Factory），负责创建账单查询页面 MVC 组合；
 *        Page factory for show-statement page creating MVC bundle.
 */
public final class ShowStatementPageFactory implements GuiPageFactory {

    /**
     * @brief 账单查询应用服务（Find Statement Application Service）；
     *        Application handler for statement query.
     */
    private final FindStatementHandler applicationHandler;

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
     *        Construct show-statement page factory.
     *
     * @param applicationHandler 账单查询应用服务（Application handler）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     * @param formViewSupplier 表单视图提供器（Form view supplier）。
     * @param tableViewSupplier 表格视图提供器（Table view supplier）。
     */
    public ShowStatementPageFactory(
            final FindStatementHandler applicationHandler,
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
        final ShowStatementModel model = new ShowStatementModel();
        final ShowStatementView view = new ShowStatementView(
                Objects.requireNonNull(formViewSupplier.get(), "formViewSupplier must not return null"),
                Objects.requireNonNull(tableViewSupplier.get(), "tableViewSupplier must not return null"));
        final ShowStatementController controller = new ShowStatementController(
                applicationHandler,
                guiExceptionHandler,
                model);
        view.setViewEventHandler(controller::onViewEvent);
        view.bindModel(model);
        return new GuiPage(CreditGuiPageIds.SHOW_STATEMENT, model, view, controller);
    }
}
