package com.moesegfault.banking.presentation.gui.customer;

import com.moesegfault.banking.application.customer.command.RegisterCustomerHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiExceptionHandler;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.view.FormView;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @brief 客户注册页面工厂（Register Customer Page Factory），创建注册页面 MVC 三件套；
 *        Register-customer page factory creating MVC bundle for customer registration.
 */
public final class RegisterCustomerPageFactory implements GuiPageFactory {

    /**
     * @brief 注册客户服务（Register Customer Service）；
     *        Register-customer application handler.
     */
    private final RegisterCustomerHandler registerCustomerHandler;

    /**
     * @brief 表单视图提供器（Form View Supplier）；
     *        Supplier creating one form view instance.
     */
    private final Supplier<FormView> formViewSupplier;

    /**
     * @brief 异常处理器（Exception Handler）；
     *        GUI exception handler.
     */
    private final GuiExceptionHandler guiExceptionHandler;

    /**
     * @brief 构造客户注册页面工厂（Construct Register Customer Page Factory）；
     *        Construct register-customer page factory.
     *
     * @param registerCustomerHandler 注册客户服务（Register customer service）。
     * @param formViewSupplier 表单视图提供器（Form view supplier）。
     * @param guiExceptionHandler 异常处理器（Exception handler）。
     */
    public RegisterCustomerPageFactory(final RegisterCustomerHandler registerCustomerHandler,
                                       final Supplier<FormView> formViewSupplier,
                                       final GuiExceptionHandler guiExceptionHandler) {
        this.registerCustomerHandler = Objects.requireNonNull(
                registerCustomerHandler,
                "registerCustomerHandler must not be null");
        this.formViewSupplier = Objects.requireNonNull(formViewSupplier, "formViewSupplier must not be null");
        this.guiExceptionHandler = Objects.requireNonNull(guiExceptionHandler, "guiExceptionHandler must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuiPage createPage(final GuiContext context) {
        Objects.requireNonNull(context, "context must not be null");

        final RegisterCustomerModel model = new RegisterCustomerModel();
        final RegisterCustomerView view = new RegisterCustomerView(Objects.requireNonNull(
                formViewSupplier.get(),
                "formViewSupplier must not supply null"));
        final RegisterCustomerController controller = new RegisterCustomerController(
                model,
                registerCustomerHandler,
                guiExceptionHandler);

        view.bindModel(model);
        view.bindEventHandler(controller::onViewEvent);

        return new GuiPage(CustomerGuiPageIds.REGISTER_CUSTOMER, model, view, controller);
    }
}
