package com.moesegfault.banking.presentation.web.customer;

import com.moesegfault.banking.presentation.web.RouteRegistrar;
import com.moesegfault.banking.presentation.web.WebRuntime;
import java.util.Objects;

/**
 * @brief 客户路由注册器（Customer Route Registrar），集中注册 /customers 相关 REST 路由；
 *        Customer route registrar that registers /customers-related REST routes.
 */
public final class CustomerRoutes implements RouteRegistrar {

    /**
     * @brief 客户控制器（Customer Controller）；
     *        Customer web controller.
     */
    private final CustomerController customerController;

    /**
     * @brief 构造客户路由注册器（Construct Customer Route Registrar）；
     *        Construct customer route registrar.
     *
     * @param customerController 客户控制器（Customer controller）。
     */
    public CustomerRoutes(final CustomerController customerController) {
        this.customerController = Objects.requireNonNull(customerController, "customerController must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRoutes(final WebRuntime runtime) {
        final WebRuntime normalizedRuntime = Objects.requireNonNull(runtime, "runtime must not be null");
        normalizedRuntime.addRoute("POST", "/customers", customerController::registerCustomer);
        normalizedRuntime.addRoute("GET", "/customers", customerController::listCustomers);
        normalizedRuntime.addRoute("GET", "/customers/{customerId}", customerController::findCustomer);
    }
}
