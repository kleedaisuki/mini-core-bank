package com.moesegfault.banking.presentation.web;

/**
 * @brief Web 运行时抽象（Web Runtime Abstraction），负责路由注册与 HTTP 服务生命周期；
 *        Web runtime abstraction responsible for route registration and HTTP service lifecycle.
 */
public interface WebRuntime {

    /**
     * @brief 注册路由处理器（Register Route Handler）；
     *        Register one route handler by HTTP method and path pattern.
     *
     * @param method HTTP 方法（HTTP method）。
     * @param pathPattern 路径模式（Path pattern），例如 `/customers/{customerId}`。
     * @param handler 路由处理器（Route handler）。
     */
    void addRoute(String method, String pathPattern, WebRouteHandler handler);

    /**
     * @brief 启动运行时（Start Runtime）；
     *        Start web runtime and accept HTTP requests.
     */
    void start();

    /**
     * @brief 停止运行时（Stop Runtime）；
     *        Stop web runtime and release HTTP resources.
     */
    void stop();
}
