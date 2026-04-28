package com.moesegfault.banking.presentation.web.credit;

import com.moesegfault.banking.presentation.web.RouteRegistrar;
import com.moesegfault.banking.presentation.web.WebRuntime;
import java.util.Objects;

/**
 * @brief 信用领域路由注册器（Credit Route Registrar），集中注册 `/credit` 相关 RESTful 路由；
 *        Credit route registrar that registers RESTful routes under `/credit`.
 */
public final class CreditRoutes implements RouteRegistrar {

    /**
     * @brief 信用 Web 控制器（Credit Web Controller）；
     *        Credit web controller.
     */
    private final CreditController creditController;

    /**
     * @brief 构造信用路由注册器（Construct Credit Route Registrar）；
     *        Construct credit route registrar.
     *
     * @param creditController 信用 Web 控制器（Credit web controller）。
     */
    public CreditRoutes(final CreditController creditController) {
        this.creditController = Objects.requireNonNull(creditController, "creditController must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRoutes(final WebRuntime runtime) {
        final WebRuntime normalizedRuntime = Objects.requireNonNull(runtime, "runtime must not be null");
        normalizedRuntime.addRoute("POST", "/credit/statements", creditController::generateStatement);
        normalizedRuntime.addRoute("GET", "/credit/statements", creditController::findStatementByPeriod);
        normalizedRuntime.addRoute("GET", "/credit/statements/{statementId}", creditController::findStatementById);
        normalizedRuntime.addRoute("POST", "/credit/repayments", creditController::repayCreditCard);
    }
}
