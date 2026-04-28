package com.moesegfault.banking.presentation.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @brief Web 应用对象（Web Application），负责路由注册、请求上下文治理和运行时生命周期；
 *        Web application object that manages route registration, request-context governance, and runtime lifecycle.
 */
public final class WebApplication {

    /**
     * @brief Web 运行时（Web Runtime）；
     *        Web runtime.
     */
    private final WebRuntime runtime;

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
     *        Immutable list of route registrars.
     */
    private final List<RouteRegistrar> routeRegistrars;

    /**
     * @brief 已启动标记（Started Flag）；
     *        Indicates whether application is started.
     */
    private boolean started;

    /**
     * @brief 已注册路由标记（Routes Registered Flag）；
     *        Indicates whether routes were already registered.
     */
    private boolean routesRegistered;

    /**
     * @brief 构造 Web 应用对象（Construct Web Application）；
     *        Construct web application with runtime, config, mapper and route registrars.
     *
     * @param runtime Web 运行时（Web runtime）。
     * @param webConfig Web 配置（Web configuration）。
     * @param webExceptionMapper 异常映射器（Exception mapper）。
     * @param routeRegistrars 路由注册器数组（Route registrar array）。
     */
    public WebApplication(final WebRuntime runtime,
                          final WebConfig webConfig,
                          final WebExceptionMapper webExceptionMapper,
                          final RouteRegistrar... routeRegistrars) {
        this.runtime = Objects.requireNonNull(runtime, "runtime must not be null");
        this.webConfig = Objects.requireNonNull(webConfig, "webConfig must not be null");
        this.webExceptionMapper = Objects.requireNonNull(webExceptionMapper, "webExceptionMapper must not be null");
        this.routeRegistrars = copyRegistrars(routeRegistrars);
    }

    /**
     * @brief 启动 Web 应用（Start Web Application）；
     *        Start web application by registering routes then starting runtime.
     */
    public void start() {
        if (started) {
            return;
        }
        registerRoutesIfNeeded();
        runtime.start();
        started = true;
    }

    /**
     * @brief 停止 Web 应用（Stop Web Application）；
     *        Stop web runtime.
     */
    public void stop() {
        if (!started) {
            return;
        }
        runtime.stop();
        started = false;
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
     * @brief 按需注册路由（Register Routes if Needed）；
     *        Register routes once using guarded runtime.
     */
    private void registerRoutesIfNeeded() {
        if (routesRegistered) {
            return;
        }
        final WebRuntime guardedRuntime = createGuardedRuntime(runtime);
        for (RouteRegistrar routeRegistrar : routeRegistrars) {
            routeRegistrar.registerRoutes(guardedRuntime);
        }
        routesRegistered = true;
    }

    /**
     * @brief 创建受保护运行时（Create Guarded Runtime）；
     *        Create guarded runtime that standardizes exception mapping and tracing headers.
     *
     * @param delegate 被代理运行时（Delegate runtime）。
     * @return 受保护运行时（Guarded runtime）。
     */
    private WebRuntime createGuardedRuntime(final WebRuntime delegate) {
        return new WebRuntime() {
            @Override
            public void addRoute(final String method, final String pathPattern, final WebRouteHandler handler) {
                final WebRouteHandler normalizedHandler = Objects.requireNonNull(handler, "handler must not be null");
                delegate.addRoute(method, pathPattern, request -> {
                    final WebContext context = WebContext.fromRequest(request, webConfig);
                    try {
                        final WebResponse response = normalizedHandler.handle(request);
                        if (response == null) {
                            throw new IllegalStateException("Route handler must not return null response");
                        }
                        return appendTraceHeaders(response, context);
                    } catch (Exception exception) {
                        final WebResponse mappedResponse = webExceptionMapper.toResponse(exception, context);
                        return appendTraceHeaders(mappedResponse, context);
                    }
                });
            }

            @Override
            public void start() {
                delegate.start();
            }

            @Override
            public void stop() {
                delegate.stop();
            }
        };
    }

    /**
     * @brief 添加链路追踪头（Append Tracing Headers）；
     *        Append request and trace identifiers into response headers.
     *
     * @param response 原始响应（Original response）。
     * @param context 请求上下文（Request context）。
     * @return 新响应对象（New response object）。
     */
    private WebResponse appendTraceHeaders(final WebResponse response, final WebContext context) {
        return response
                .withHeader(webConfig.requestIdHeaderName(), context.requestId())
                .withHeader(webConfig.traceIdHeaderName(), context.traceId());
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

