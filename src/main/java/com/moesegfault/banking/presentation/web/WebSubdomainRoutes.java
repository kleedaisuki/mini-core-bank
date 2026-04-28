package com.moesegfault.banking.presentation.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @brief Web 子领域路由组合器（Web Subdomain Route Composer），把多个子领域注册器合并为一个入口；
 *        Web subdomain-route composer that merges multiple route registrars into one registration entry.
 */
public final class WebSubdomainRoutes implements RouteRegistrar {

    /**
     * @brief 子领域路由注册器列表（Subdomain Route Registrars）；
     *        Immutable list of subdomain route registrars.
     */
    private final List<RouteRegistrar> subdomainRegistrars;

    /**
     * @brief 构造路由组合器（Construct Route Composer）；
     *        Construct route composer by registrar array.
     *
     * @param subdomainRegistrars 子领域注册器数组（Subdomain registrar array）。
     */
    public WebSubdomainRoutes(final RouteRegistrar... subdomainRegistrars) {
        this.subdomainRegistrars = copyRegistrars(subdomainRegistrars);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRoutes(final WebRuntime runtime) {
        final WebRuntime normalizedRuntime = Objects.requireNonNull(runtime, "runtime must not be null");
        for (RouteRegistrar registrar : subdomainRegistrars) {
            registrar.registerRoutes(normalizedRuntime);
        }
    }

    /**
     * @brief 获取子领域注册器快照（Get Subdomain Registrar Snapshot）；
     *        Get immutable snapshot of subdomain registrars.
     *
     * @return 子领域注册器列表（Subdomain registrar list）。
     */
    public List<RouteRegistrar> registrars() {
        return subdomainRegistrars;
    }

    /**
     * @brief 复制并校验注册器数组（Copy and Validate Registrar Array）；
     *        Copy and validate registrar array into immutable list.
     *
     * @param registrars 注册器数组（Registrar array）。
     * @return 不可变注册器列表（Immutable registrar list）。
     */
    private static List<RouteRegistrar> copyRegistrars(final RouteRegistrar... registrars) {
        Objects.requireNonNull(registrars, "subdomainRegistrars must not be null");
        final List<RouteRegistrar> copied = new ArrayList<>();
        for (RouteRegistrar registrar : registrars) {
            copied.add(Objects.requireNonNull(registrar, "subdomainRegistrars contains null registrar"));
        }
        return List.copyOf(copied);
    }
}

