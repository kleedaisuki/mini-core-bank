package com.moesegfault.banking.presentation.gui.ledger;

import com.moesegfault.banking.application.ledger.query.ListLedgerEntriesHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.GuiRuntime;
import com.moesegfault.banking.presentation.gui.view.EmptyStateView;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @brief 分录查询页面工厂（Show Entries Page Factory），创建分录查询页面 MVC 三件套；
 *        Ledger-entry query page factory creating model/view/controller bundle.
 */
public final class ShowEntriesPageFactory implements GuiPageFactory {

    /**
     * @brief 应用层分录查询服务（Application Ledger-entry Query Service）；
     *        Application-layer ledger-entry query handler.
     */
    private final ListLedgerEntriesHandler applicationHandler;

    /**
     * @brief GUI 运行时（GUI Runtime）；
     *        GUI runtime for page view mounting.
     */
    private final GuiRuntime guiRuntime;

    /**
     * @brief GUI 异常处理器（GUI Exception Handler）；
     *        GUI exception mapper.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 表单视图提供器（Form View Supplier）；
     *        Supplier creating one form view per page instance.
     */
    private final Supplier<FormView> formViewSupplier;

    /**
     * @brief 表格视图提供器（Table View Supplier）；
     *        Supplier creating one table view per page instance.
     */
    private final Supplier<TableView> tableViewSupplier;

    /**
     * @brief 空态视图提供器（Empty-state View Supplier）；
     *        Supplier creating one empty-state view per page instance.
     */
    private final Supplier<EmptyStateView> emptyStateViewSupplier;

    /**
     * @brief 构造分录查询页面工厂（Construct Show Entries Page Factory）；
     *        Construct ledger-entry query page factory.
     *
     * @param applicationHandler 应用层分录查询服务（Application ledger-entry query handler）。
     * @param guiRuntime GUI 运行时（GUI runtime）。
     * @param guiExceptionHandler GUI 异常处理器（GUI exception handler）。
     * @param formViewSupplier 表单视图提供器（Form view supplier）。
     * @param tableViewSupplier 表格视图提供器（Table view supplier）。
     * @param emptyStateViewSupplier 空态视图提供器（Empty-state view supplier）。
     */
    public ShowEntriesPageFactory(final ListLedgerEntriesHandler applicationHandler,
                                  final GuiRuntime guiRuntime,
                                  final GuiExceptionHandler guiExceptionHandler,
                                  final Supplier<FormView> formViewSupplier,
                                  final Supplier<TableView> tableViewSupplier,
                                  final Supplier<EmptyStateView> emptyStateViewSupplier) {
        this.applicationHandler = Objects.requireNonNull(applicationHandler, "applicationHandler must not be null");
        this.guiRuntime = Objects.requireNonNull(guiRuntime, "guiRuntime must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
        this.formViewSupplier = Objects.requireNonNull(formViewSupplier, "formViewSupplier must not be null");
        this.tableViewSupplier = Objects.requireNonNull(tableViewSupplier, "tableViewSupplier must not be null");
        this.emptyStateViewSupplier = Objects.requireNonNull(emptyStateViewSupplier, "emptyStateViewSupplier must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuiPage createPage(final GuiContext context) {
        Objects.requireNonNull(context, "context must not be null");

        final ShowEntriesModel model = new ShowEntriesModel();
        final ShowEntriesController controller = new ShowEntriesController(model, applicationHandler, guiExceptionHandler);
        final ShowEntriesView view = new ShowEntriesView(
                guiRuntime,
                controller,
                requireComponent(formViewSupplier, "formViewSupplier"),
                requireComponent(tableViewSupplier, "tableViewSupplier"),
                requireComponent(emptyStateViewSupplier, "emptyStateViewSupplier"));
        view.bindModel(model);
        return new GuiPage(LedgerGuiPageIds.SHOW_ENTRIES, model, view, controller);
    }

    /**
     * @brief 安全创建组件（Create Component Safely）；
     *        Create page sub-view component with null-safety checks.
     *
     * @param supplier 组件提供器（Component supplier）。
     * @param label 组件标签（Component label）。
     * @param <T> 组件类型（Component type）。
     * @return 创建出的组件（Created component）。
     */
    private static <T> T requireComponent(final Supplier<T> supplier, final String label) {
        final T component = supplier.get();
        return Objects.requireNonNull(component, label + " must not supply null");
    }
}
