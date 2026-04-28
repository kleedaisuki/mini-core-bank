package com.moesegfault.banking.presentation.gui.customer;

import com.moesegfault.banking.application.customer.command.RegisterCustomerHandler;
import com.moesegfault.banking.application.customer.query.FindCustomerHandler;
import com.moesegfault.banking.application.customer.query.ListCustomersHandler;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiModule;
import com.moesegfault.banking.presentation.gui.GuiPageRegistrar;
import com.moesegfault.banking.presentation.gui.view.FormView;
import com.moesegfault.banking.presentation.gui.view.TableView;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @brief 客户 GUI 模块（Customer GUI Module），集中注册 customer 域所有 GUI 页面；
 *        Customer GUI module registering all customer-domain GUI pages.
 */
public final class CustomerGuiModule implements GuiModule {

    /**
     * @brief 客户注册页面工厂（Register Page Factory）；
     *        Page factory for customer registration page.
     */
    private final RegisterCustomerPageFactory registerCustomerPageFactory;

    /**
     * @brief 客户详情页面工厂（Show Page Factory）；
     *        Page factory for customer-detail page.
     */
    private final ShowCustomerPageFactory showCustomerPageFactory;

    /**
     * @brief 客户列表页面工厂（List Page Factory）；
     *        Page factory for customer-list page.
     */
    private final ListCustomersPageFactory listCustomersPageFactory;

    /**
     * @brief 构造客户 GUI 模块（Construct Customer GUI Module）；
     *        Construct customer GUI module from customer application handlers and view suppliers.
     *
     * @param registerCustomerHandler 注册客户服务（Register-customer service）。
     * @param findCustomerHandler 客户详情查询服务（Find-customer service）。
     * @param listCustomersHandler 客户列表查询服务（List-customers service）。
     * @param formViewSupplier 表单视图提供器（Form-view supplier）。
     * @param tableViewSupplier 表格视图提供器（Table-view supplier）。
     * @param guiExceptionHandler 异常处理器（Exception handler）。
     */
    public CustomerGuiModule(final RegisterCustomerHandler registerCustomerHandler,
                             final FindCustomerHandler findCustomerHandler,
                             final ListCustomersHandler listCustomersHandler,
                             final Supplier<FormView> formViewSupplier,
                             final Supplier<TableView> tableViewSupplier,
                             final GuiExceptionHandler guiExceptionHandler) {
        final Supplier<FormView> normalizedFormViewSupplier = Objects.requireNonNull(
                formViewSupplier,
                "formViewSupplier must not be null");
        this.registerCustomerPageFactory = new RegisterCustomerPageFactory(
                Objects.requireNonNull(registerCustomerHandler, "registerCustomerHandler must not be null"),
                normalizedFormViewSupplier,
                Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null"));
        this.showCustomerPageFactory = new ShowCustomerPageFactory(
                Objects.requireNonNull(findCustomerHandler, "findCustomerHandler must not be null"),
                normalizedFormViewSupplier,
                guiExceptionHandler);
        this.listCustomersPageFactory = new ListCustomersPageFactory(
                Objects.requireNonNull(listCustomersHandler, "listCustomersHandler must not be null"),
                normalizedFormViewSupplier,
                Objects.requireNonNull(tableViewSupplier, "tableViewSupplier must not be null"),
                guiExceptionHandler);
    }

    /**
     * @brief 构造客户 GUI 模块（Construct Customer GUI Module by Factories）；
     *        Construct customer GUI module from prepared page factories.
     *
     * @param registerCustomerPageFactory 注册页面工厂（Register page factory）。
     * @param showCustomerPageFactory 详情页面工厂（Show page factory）。
     * @param listCustomersPageFactory 列表页面工厂（List page factory）。
     */
    public CustomerGuiModule(final RegisterCustomerPageFactory registerCustomerPageFactory,
                             final ShowCustomerPageFactory showCustomerPageFactory,
                             final ListCustomersPageFactory listCustomersPageFactory) {
        this.registerCustomerPageFactory = Objects.requireNonNull(
                registerCustomerPageFactory,
                "registerCustomerPageFactory must not be null");
        this.showCustomerPageFactory = Objects.requireNonNull(
                showCustomerPageFactory,
                "showCustomerPageFactory must not be null");
        this.listCustomersPageFactory = Objects.requireNonNull(
                listCustomersPageFactory,
                "listCustomersPageFactory must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerPages(final GuiPageRegistrar registrar) {
        final GuiPageRegistrar normalizedRegistrar = Objects.requireNonNull(registrar, "registrar must not be null");
        normalizedRegistrar.register(CustomerGuiPageIds.REGISTER_CUSTOMER, registerCustomerPageFactory);
        normalizedRegistrar.register(CustomerGuiPageIds.SHOW_CUSTOMER, showCustomerPageFactory);
        normalizedRegistrar.register(CustomerGuiPageIds.LIST_CUSTOMERS, listCustomersPageFactory);
    }
}
