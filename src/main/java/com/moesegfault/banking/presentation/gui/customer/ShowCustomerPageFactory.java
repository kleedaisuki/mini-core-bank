package com.moesegfault.banking.presentation.gui.customer;

import com.moesegfault.banking.application.customer.query.FindCustomerHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.view.FormView;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @brief 客户详情页面工厂（Show Customer Page Factory），创建客户详情页 MVC 三件套；
 *        Show-customer page factory creating MVC bundle for customer-detail page.
 */
public final class ShowCustomerPageFactory implements GuiPageFactory {

    /**
     * @brief 查询客户服务（Find Customer Service）；
     *        Find-customer application handler.
     */
    private final FindCustomerHandler findCustomerHandler;

    /**
     * @brief 查询表单提供器（Query Form Supplier）；
     *        Supplier creating one query form view.
     */
    private final Supplier<FormView> queryFormSupplier;

    /**
     * @brief 异常处理器（Exception Handler）；
     *        GUI exception handler.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 构造客户详情页面工厂（Construct Show Customer Page Factory）；
     *        Construct show-customer page factory.
     *
     * @param findCustomerHandler 查询客户服务（Find customer service）。
     * @param queryFormSupplier 查询表单提供器（Query form supplier）。
     * @param guiExceptionHandler 异常处理器（Exception handler）。
     */
    public ShowCustomerPageFactory(final FindCustomerHandler findCustomerHandler,
                                   final Supplier<FormView> queryFormSupplier,
                                   final GuiExceptionHandler guiExceptionHandler) {
        this.findCustomerHandler = Objects.requireNonNull(findCustomerHandler, "findCustomerHandler must not be null");
        this.queryFormSupplier = Objects.requireNonNull(queryFormSupplier, "queryFormSupplier must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuiPage createPage(final GuiContext context) {
        final GuiContext normalizedContext = Objects.requireNonNull(context, "context must not be null");

        final ShowCustomerModel model = new ShowCustomerModel();
        final ShowCustomerView view = new ShowCustomerView(Objects.requireNonNull(
                queryFormSupplier.get(),
                "queryFormSupplier must not supply null"));
        final ShowCustomerController controller = new ShowCustomerController(
                model,
                findCustomerHandler,
                normalizedContext,
                guiExceptionHandler);

        view.bindModel(model);
        view.bindEventHandler(controller::onViewEvent);

        return new GuiPage(CustomerGuiPageIds.SHOW_CUSTOMER, model, view, controller);
    }
}
