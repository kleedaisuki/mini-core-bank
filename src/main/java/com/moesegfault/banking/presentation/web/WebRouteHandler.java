package com.moesegfault.banking.presentation.web;

/**
 * @brief Web 路由处理器接口（Web Route Handler Interface），定义单个 HTTP 路由处理入口；
 *        Web route handler abstraction defining one HTTP route handling entrypoint.
 */
@FunctionalInterface
public interface WebRouteHandler {

    /**
     * @brief 处理 Web 请求并返回响应（Handle Web Request and Return Response）；
     *        Handle one web request and return response.
     *
     * @param request Web 请求（Web request）。
     * @return Web 响应（Web response）。
     * @throws Exception 处理异常（Handling exception）。
     */
    WebResponse handle(WebRequest request) throws Exception;
}
