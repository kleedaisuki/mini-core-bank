package com.moesegfault.banking.presentation.web;

/**
 * @brief 路由注册器接口（Route Registrar Interface），用于按子领域集中注册 REST 路由；
 *        Route-registrar interface used by each subdomain to register REST routes in one place.
 */
@FunctionalInterface
public interface RouteRegistrar {

    /**
     * @brief 注册子领域路由（Register Subdomain Routes）；
     *        Register subdomain routes into the target runtime.
     *
     * @param runtime Web 运行时（Web runtime）。
     */
    void registerRoutes(WebRuntime runtime);
}

