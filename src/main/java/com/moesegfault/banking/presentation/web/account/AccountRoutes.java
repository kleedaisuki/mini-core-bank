package com.moesegfault.banking.presentation.web.account;

import com.moesegfault.banking.presentation.web.RouteRegistrar;
import com.moesegfault.banking.presentation.web.WebRuntime;
import java.util.Objects;

/**
 * @brief 账户路由注册器（Account Route Registrar），集中注册 account 子域 REST 路由；
 *        Account route registrar centralizing REST-route registration for account subdomain.
 */
public final class AccountRoutes implements RouteRegistrar {

    /**
     * @brief 账户控制器（Account Controller）；
     *        Account web controller.
     */
    private final AccountController accountController;

    /**
     * @brief 构造账户路由注册器（Construct Account Route Registrar）；
     *        Construct account-route registrar.
     *
     * @param accountController 账户控制器（Account controller）。
     */
    public AccountRoutes(final AccountController accountController) {
        this.accountController = Objects.requireNonNull(accountController, "accountController must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRoutes(final WebRuntime runtime) {
        final WebRuntime normalizedRuntime = Objects.requireNonNull(runtime, "runtime must not be null");
        normalizedRuntime.addRoute("POST", AccountWebSchema.PATH_OPEN_SAVINGS, accountController::openSavingsAccount);
        normalizedRuntime.addRoute("POST", AccountWebSchema.PATH_OPEN_FX, accountController::openFxAccount);
        normalizedRuntime.addRoute("POST", AccountWebSchema.PATH_OPEN_INVESTMENT, accountController::openInvestmentAccount);
        normalizedRuntime.addRoute("GET", AccountWebSchema.PATH_ACCOUNT_DETAIL, accountController::findAccountById);
        normalizedRuntime.addRoute("GET", AccountWebSchema.PATH_ACCOUNT_BY_NO, accountController::findAccountByAccountNo);
        normalizedRuntime.addRoute("GET", AccountWebSchema.PATH_ACCOUNTS, accountController::listCustomerAccounts);
        normalizedRuntime.addRoute("PATCH", AccountWebSchema.PATH_ACCOUNT_DETAIL, accountController::freezeAccount);
    }
}
