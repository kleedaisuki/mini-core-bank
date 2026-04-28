package com.moesegfault.banking.presentation.web.investment;

import com.moesegfault.banking.presentation.web.RouteRegistrar;
import com.moesegfault.banking.presentation.web.WebRuntime;
import java.util.Objects;

/**
 * @brief 投资路由注册器（Investment Route Registrar），集中注册投资相关 REST 路由；
 *        Investment route registrar that centralizes investment REST route registration.
 */
public final class InvestmentRoutes implements RouteRegistrar {

    /**
     * @brief 投资控制器（Investment Controller）；
     *        Investment web controller.
     */
    private final InvestmentController investmentController;

    /**
     * @brief 构造投资路由注册器（Construct Investment Routes）；
     *        Construct investment route registrar.
     *
     * @param investmentController 投资控制器（Investment controller）。
     */
    public InvestmentRoutes(final InvestmentController investmentController) {
        this.investmentController = Objects.requireNonNull(
                investmentController,
                "investmentController must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRoutes(final WebRuntime runtime) {
        final WebRuntime normalizedRuntime = Objects.requireNonNull(runtime, "runtime must not be null");
        normalizedRuntime.addRoute("POST", InvestmentWebSchema.PATH_PRODUCTS, investmentController::createProduct);
        normalizedRuntime.addRoute("POST", InvestmentWebSchema.PATH_BUY_ORDER, investmentController::buyProduct);
        normalizedRuntime.addRoute("POST", InvestmentWebSchema.PATH_SELL_ORDER, investmentController::sellProduct);
        normalizedRuntime.addRoute("GET", InvestmentWebSchema.PATH_HOLDINGS, investmentController::listHoldings);
    }
}
