package com.moesegfault.banking.presentation.web;

import java.util.List;
import java.util.Optional;

/**
 * @brief Web 请求抽象（Web Request Abstraction），屏蔽底层 HTTP 引擎差异；
 *        Web request abstraction that hides HTTP-engine specific details.
 */
public interface WebRequest {

    /**
     * @brief 获取 HTTP 方法（Get HTTP Method）；
     *        Get request HTTP method.
     *
     * @return HTTP 方法（HTTP method）。
     */
    String method();

    /**
     * @brief 获取请求路径（Get Request Path）；
     *        Get normalized request path.
     *
     * @return 请求路径（Request path）。
     */
    String path();

    /**
     * @brief 按名称获取路径参数（Get Path Parameter by Name）；
     *        Get one path parameter by name.
     *
     * @param name 参数名（Parameter name）。
     * @return 参数值（Parameter value），缺失则 empty。
     */
    Optional<String> pathParam(String name);

    /**
     * @brief 按名称获取查询参数（Get Query Parameter by Name）；
     *        Get one query parameter by name.
     *
     * @param name 参数名（Parameter name）。
     * @return 参数值（Parameter value），缺失则 empty。
     */
    Optional<String> queryParam(String name);

    /**
     * @brief 按名称获取请求头第一值（Get First Header Value by Name）；
     *        Get first request header value by name.
     *
     * @param name Header 名称（Header name）。
     * @return Header 值（Header value），缺失则 empty。
     */
    Optional<String> header(String name);

    /**
     * @brief 按名称获取请求头所有值（Get All Header Values by Name）；
     *        Get all request header values by name.
     *
     * @param name Header 名称（Header name）。
     * @return Header 值列表（Header values），不存在返回空列表。
     */
    List<String> headers(String name);

    /**
     * @brief 获取请求体字节（Get Request Body Bytes）；
     *        Get request body as bytes.
     *
     * @return 请求体字节（Request body bytes）。
     */
    byte[] bodyBytes();

    /**
     * @brief 获取 UTF-8 请求体文本（Get UTF-8 Request Body Text）；
     *        Get request body text decoded by UTF-8.
     *
     * @return 请求体文本（Request body text）。
     */
    String bodyText();
}
