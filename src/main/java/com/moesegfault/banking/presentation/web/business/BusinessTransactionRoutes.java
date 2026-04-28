package com.moesegfault.banking.presentation.web.business;

import com.moesegfault.banking.presentation.web.RouteRegistrar;
import com.moesegfault.banking.presentation.web.WebRuntime;
import java.util.Objects;

/**
 * @brief 业务流水路由注册器（Business Transaction Route Registrar），集中注册 `/business-transactions` 相关路由；
 *        Business-transaction route registrar centralizing `/business-transactions` related route registration.
 */
public final class BusinessTransactionRoutes implements RouteRegistrar {

    /**
     * @brief 业务流水根路径（Business Transaction Base Path）；
     *        Base path for business-transaction APIs.
     */
    public static final String PATH_BASE = "/business-transactions";

    /**
     * @brief 按交易 ID 查询路径（Path for Querying by Transaction ID）；
     *        Detail path for querying by transaction id.
     */
    public static final String PATH_BY_TRANSACTION_ID = "/business-transactions/{transactionId}";

    /**
     * @brief 按参考号查询路径（Path for Querying by Reference Number）；
     *        Detail path for querying by reference number.
     */
    public static final String PATH_BY_REFERENCE_NO = "/business-transactions/by-reference/{referenceNo}";

    /**
     * @brief 控制器（Controller）；
     *        Business-transaction web controller.
     */
    private final BusinessTransactionController controller;

    /**
     * @brief 构造路由注册器（Construct Route Registrar）；
     *        Construct route registrar with controller dependency.
     *
     * @param controller 业务流水控制器（Business-transaction controller）。
     */
    public BusinessTransactionRoutes(final BusinessTransactionController controller) {
        this.controller = Objects.requireNonNull(controller, "controller must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRoutes(final WebRuntime runtime) {
        final WebRuntime normalizedRuntime = Objects.requireNonNull(runtime, "runtime must not be null");
        normalizedRuntime.addRoute("GET", PATH_BASE, controller::listBusinessTransactions);
        normalizedRuntime.addRoute("GET", PATH_BY_TRANSACTION_ID, controller::getBusinessTransactionById);
        normalizedRuntime.addRoute("GET", PATH_BY_REFERENCE_NO, controller::getBusinessTransactionByReferenceNo);
    }
}

