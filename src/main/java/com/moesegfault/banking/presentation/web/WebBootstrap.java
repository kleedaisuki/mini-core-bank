package com.moesegfault.banking.presentation.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @brief Web 装配器（Web Bootstrap），负责装配 Runtime、ExceptionMapper 与子领域路由注册器；
 *        Web bootstrap that assembles runtime, exception mapper, and subdomain route registrars.
 */
public final class WebBootstrap {

    /**
     * @brief Runtime 工厂（Runtime Factory）；
     *        Factory resolving runtime from web configuration.
     */
    private final Function<WebConfig, WebRuntime> runtimeFactory;

    /**
     * @brief Web 配置（Web Configuration）；
     *        Web configuration.
     */
    private final WebConfig webConfig;

    /**
     * @brief 异常映射器（Exception Mapper）；
     *        Web exception mapper.
     */
    private final WebExceptionMapper webExceptionMapper;

    /**
     * @brief 路由注册器列表（Route Registrars）；
     *        Immutable route-registrar list.
     */
    private final List<RouteRegistrar> routeRegistrars;

    /**
     * @brief 构造 Web 装配器（Construct Web Bootstrap）；
     *        Construct web bootstrap with runtime factory, config, mapper and route registrars.
     *
     * @param runtimeFactory Runtime 工厂（Runtime factory）。
     * @param webConfig Web 配置（Web configuration）。
     * @param webExceptionMapper 异常映射器（Exception mapper）。
     * @param routeRegistrars 路由注册器数组（Route registrar array）。
     */
    public WebBootstrap(final Function<WebConfig, WebRuntime> runtimeFactory,
                        final WebConfig webConfig,
                        final WebExceptionMapper webExceptionMapper,
                        final RouteRegistrar... routeRegistrars) {
        this.runtimeFactory = Objects.requireNonNull(runtimeFactory, "runtimeFactory must not be null");
        this.webConfig = Objects.requireNonNull(webConfig, "webConfig must not be null");
        this.webExceptionMapper = Objects.requireNonNull(webExceptionMapper, "webExceptionMapper must not be null");
        this.routeRegistrars = copyRegistrars(routeRegistrars);
    }

    /**
     * @brief 执行装配（Bootstrap Web Application）；
     *        Bootstrap web application instance.
     *
     * @return Web 应用对象（Web application object）。
     */
    public WebApplication bootstrap() {
        final WebRuntime runtime = Objects.requireNonNull(
                runtimeFactory.apply(webConfig),
                "runtimeFactory must not return null runtime");

        return new WebApplication(runtime, webConfig, webExceptionMapper, routeRegistrars.toArray(RouteRegistrar[]::new));
    }

    /**
     * @brief 获取 Web 配置（Get Web Configuration）；
     *        Get web configuration.
     *
     * @return Web 配置（Web configuration）。
     */
    public WebConfig webConfig() {
        return webConfig;
    }

    /**
     * @brief 获取路由注册器列表（Get Route Registrar List）；
     *        Get immutable route registrar list.
     *
     * @return 路由注册器列表（Route registrar list）。
     */
    public List<RouteRegistrar> routeRegistrars() {
        return routeRegistrars;
    }

    /**
     * @brief 复制并校验注册器数组（Copy and Validate Registrar Array）；
     *        Copy and validate registrar array into immutable list.
     *
     * @param registrars 注册器数组（Registrar array）。
     * @return 不可变注册器列表（Immutable registrar list）。
     */
    private static List<RouteRegistrar> copyRegistrars(final RouteRegistrar... registrars) {
        Objects.requireNonNull(registrars, "routeRegistrars must not be null");
        final List<RouteRegistrar> copied = new ArrayList<>();
        for (RouteRegistrar registrar : registrars) {
            copied.add(Objects.requireNonNull(registrar, "routeRegistrars contains null registrar"));
        }
        return List.copyOf(copied);
    }
}

