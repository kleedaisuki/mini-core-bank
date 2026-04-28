package com.moesegfault.banking.presentation.gui.credit;

import com.moesegfault.banking.application.credit.command.GenerateStatementHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @brief 生成信用卡账单页面工厂（Generate Statement Page Factory），负责创建页面 MVC 组合；
 *        Page factory for generate-statement page creating MVC bundle.
 */
public final class GenerateStatementPageFactory implements GuiPageFactory {

    /**
     * @brief 生成账单应用服务（Generate Statement Application Service）；
     *        Application handler for statement generation.
     */
    private final GenerateStatementHandler applicationHandler;

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
     *        Construct generate-statement page factory.
     *
     * @param applicationHandler 生成账单应用服务（Application handler）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     * @param formViewSupplier 表单视图提供器（Form view supplier）。
     * @param tableViewSupplier 表格视图提供器（Table view supplier）。
     */
    public GenerateStatementPageFactory(
            final GenerateStatementHandler applicationHandler,
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
        final GenerateStatementModel model = new GenerateStatementModel();
        final GenerateStatementView view = new GenerateStatementView(
                Objects.requireNonNull(formViewSupplier.get(), "formViewSupplier must not return null"),
                Objects.requireNonNull(tableViewSupplier.get(), "tableViewSupplier must not return null"));
        final GenerateStatementController controller = new GenerateStatementController(
                applicationHandler,
                guiExceptionHandler,
                model);
        view.setViewEventHandler(controller::onViewEvent);
        view.bindModel(model);
        return new GuiPage(CreditGuiPageIds.GENERATE_STATEMENT, model, view, controller);
    }
}
