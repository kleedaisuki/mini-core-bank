package com.moesegfault.banking.presentation.web.ledger;

import com.moesegfault.banking.presentation.web.RouteRegistrar;
import com.moesegfault.banking.presentation.web.WebRuntime;
import java.util.Objects;

/**
 * @brief Ledger 路由注册器（Ledger Route Registrar），集中注册账务 REST API 路由；
 *        Ledger route registrar that centralizes registration for ledger REST API routes.
 */
public final class LedgerRoutes implements RouteRegistrar {

    /**
     * @brief 查询余额路由（Find Balance Route）；
     *        Route pattern for querying one account-currency balance.
     */
    public static final String ROUTE_FIND_BALANCE = "/ledger/accounts/{accountId}/balances/{currencyCode}";

    /**
     * @brief 查询分录列表路由（List Ledger Entries Route）；
     *        Route pattern for querying account ledger entries.
     */
    public static final String ROUTE_LIST_ENTRIES = "/ledger/accounts/{accountId}/entries";

    /**
     * @brief Ledger 控制器（Ledger Controller）；
     *        Ledger web controller.
     */
    private final LedgerController ledgerController;

    /**
     * @brief 构造 Ledger 路由注册器（Construct Ledger Route Registrar）；
     *        Construct ledger route registrar with ledger controller.
     *
     * @param ledgerController Ledger 控制器（Ledger controller）。
     */
    public LedgerRoutes(final LedgerController ledgerController) {
        this.ledgerController = Objects.requireNonNull(ledgerController, "ledgerController must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRoutes(final WebRuntime runtime) {
        final WebRuntime normalizedRuntime = Objects.requireNonNull(runtime, "runtime must not be null");
        normalizedRuntime.addRoute("GET", ROUTE_FIND_BALANCE, ledgerController::findBalance);
        normalizedRuntime.addRoute("GET", ROUTE_LIST_ENTRIES, ledgerController::listLedgerEntries);
    }
}
