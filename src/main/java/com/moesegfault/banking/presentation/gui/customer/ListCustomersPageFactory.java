package com.moesegfault.banking.presentation.gui.customer;

import com.moesegfault.banking.application.customer.query.ListCustomersHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @brief 客户列表页面工厂（List Customers Page Factory），创建客户列表页 MVC 三件套；
 *        Customer-list page factory creating MVC bundle for customer-list page.
 */
public final class ListCustomersPageFactory implements GuiPageFactory {

    /**
     * @brief 列表查询服务（List Query Service）；
     *        List-customers application handler.
     */
    private final ListCustomersHandler listCustomersHandler;

    /**
     * @brief 筛选表单提供器（Filter Form Supplier）；
     *        Supplier creating filter form view instance.
     */
    private final Supplier<FormView> filterFormSupplier;

    /**
     * @brief 客户表格提供器（Customer Table Supplier）；
     *        Supplier creating customer table view instance.
     */
    private final Supplier<TableView> customerTableSupplier;

    /**
     * @brief 异常处理器（Exception Handler）；
     *        GUI exception handler.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 构造客户列表页面工厂（Construct List Customers Page Factory）；
     *        Construct customer-list page factory.
     *
     * @param listCustomersHandler 列表查询服务（List query service）。
     * @param filterFormSupplier 筛选表单提供器（Filter form supplier）。
     * @param customerTableSupplier 客户表格提供器（Customer table supplier）。
     * @param guiExceptionHandler 异常处理器（Exception handler）。
     */
    public ListCustomersPageFactory(final ListCustomersHandler listCustomersHandler,
                                    final Supplier<FormView> filterFormSupplier,
                                    final Supplier<TableView> customerTableSupplier,
                                    final GuiExceptionHandler guiExceptionHandler) {
        this.listCustomersHandler = Objects.requireNonNull(listCustomersHandler, "listCustomersHandler must not be null");
        this.filterFormSupplier = Objects.requireNonNull(filterFormSupplier, "filterFormSupplier must not be null");
        this.customerTableSupplier = Objects.requireNonNull(
                customerTableSupplier,
                "customerTableSupplier must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuiPage createPage(final GuiContext context) {
        final GuiContext normalizedContext = Objects.requireNonNull(context, "context must not be null");

        final ListCustomersModel model = new ListCustomersModel();
        final ListCustomersView view = new ListCustomersView(
                Objects.requireNonNull(filterFormSupplier.get(), "filterFormSupplier must not supply null"),
                Objects.requireNonNull(customerTableSupplier.get(), "customerTableSupplier must not supply null"));
        final ListCustomersController controller = new ListCustomersController(
                model,
                listCustomersHandler,
                normalizedContext,
                guiExceptionHandler);

        view.bindModel(model);
        view.bindEventHandler(controller::onViewEvent);

        return new GuiPage(CustomerGuiPageIds.LIST_CUSTOMERS, model, view, controller);
    }
}
