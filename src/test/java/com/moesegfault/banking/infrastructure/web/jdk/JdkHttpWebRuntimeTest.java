package com.moesegfault.banking.infrastructure.web.jdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.presentation.web.WebResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @brief JdkHttpWebRuntime 集成测试（JdkHttpWebRuntime Integration Tests），验证路由分发与错误处理；
 *        Integration tests for JdkHttpWebRuntime route dispatching and error handling.
 */
class JdkHttpWebRuntimeTest {

    /**
     * @brief 被测运行时（Runtime Under Test）；
     *        Runtime under test.
     */
    private JdkHttpWebRuntime runtime;

    /**
     * @brief HTTP 客户端（HTTP Client）；
     *        HTTP client for test requests.
     */
    private HttpClient httpClient;

    /**
     * @brief 初始化测试资源（Initialize Test Resources）；
     *        Initialize test resources before each test.
     */
    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
    }

    /**
     * @brief 清理测试资源（Cleanup Test Resources）；
     *        Cleanup runtime after each test.
     */
    @AfterEach
    void tearDown() {
        if (runtime != null) {
            runtime.stop();
        }
    }

    /**
     * @brief 验证静态路由可返回 200；
     *        Verify static route returns HTTP 200.
     *
     * @throws Exception 请求异常（Request exception）。
     */
    @Test
    void shouldDispatchStaticRoute() throws Exception {
        runtime = new JdkHttpWebRuntime("127.0.0.1", 0, 2);
        runtime.addRoute("GET", "/health", request -> WebResponse.text(200, "ok"));
        runtime.start();

        final HttpResponse<String> response = sendGet("/health");

        assertEquals(200, response.statusCode());
        assertEquals("ok", response.body());
    }

    /**
     * @brief 验证路径参数可被路由处理器读取；
     *        Verify path parameter is available for route handler.
     *
     * @throws Exception 请求异常（Request exception）。
     */
    @Test
    void shouldResolvePathParameter() throws Exception {
        runtime = new JdkHttpWebRuntime("127.0.0.1", 0, 2);
        runtime.addRoute("GET", "/customers/{customerId}",
                request -> WebResponse.text(200, request.pathParam("customerId").orElse("missing")));
        runtime.start();

        final HttpResponse<String> response = sendGet("/customers/c-100");

        assertEquals(200, response.statusCode());
        assertEquals("c-100", response.body());
    }

    /**
     * @brief 验证未匹配路由返回 404；
     *        Verify unmatched route returns HTTP 404.
     *
     * @throws Exception 请求异常（Request exception）。
     */
    @Test
    void shouldReturnNotFoundWhenNoRouteMatched() throws Exception {
        runtime = new JdkHttpWebRuntime("127.0.0.1", 0, 2);
        runtime.start();

        final HttpResponse<String> response = sendGet("/missing");

        assertEquals(404, response.statusCode());
        assertEquals("Not Found", response.body());
    }

    /**
     * @brief 验证处理器抛错时返回 500；
     *        Verify handler exception is mapped to HTTP 500.
     *
     * @throws Exception 请求异常（Request exception）。
     */
    @Test
    void shouldReturnInternalServerErrorWhenHandlerThrows() throws Exception {
        runtime = new JdkHttpWebRuntime("127.0.0.1", 0, 2);
        runtime.addRoute("GET", "/boom", request -> {
            throw new IllegalStateException("boom");
        });
        runtime.start();

        final HttpResponse<String> response = sendGet("/boom");

        assertEquals(500, response.statusCode());
        assertTrue(response.body().contains("Internal Server Error"));
    }

    /**
     * @brief 发送 GET 请求（Send GET Request）；
     *        Send one GET request to runtime.
     *
     * @param path 请求路径（Request path）。
     * @return HTTP 响应（HTTP response）。
     * @throws IOException I/O 异常（I/O exception）。
     * @throws InterruptedException 中断异常（Interrupted exception）。
     */
    private HttpResponse<String> sendGet(final String path) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + runtime.actualPort() + path))
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
