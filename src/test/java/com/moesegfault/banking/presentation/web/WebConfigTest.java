package com.moesegfault.banking.presentation.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Properties;
import org.junit.jupiter.api.Test;

/**
 * @brief Web 配置测试（Web Configuration Test），验证默认值、属性绑定与参数校验；
 *        Web configuration tests verifying defaults, property binding, and argument validation.
 */
class WebConfigTest {

    /**
     * @brief 验证默认配置可用；
     *        Verify default configuration values.
     */
    @Test
    void shouldBuildDefaults() {
        final WebConfig config = WebConfig.defaults();

        assertEquals("127.0.0.1", config.host());
        assertEquals(8080, config.port());
        assertEquals("X-Request-Id", config.requestIdHeaderName());
        assertEquals("X-Trace-Id", config.traceIdHeaderName());
    }

    /**
     * @brief 验证可从 Properties 读取配置；
     *        Verify properties are loaded into web config.
     */
    @Test
    void shouldLoadFromProperties() {
        final Properties properties = new Properties();
        properties.setProperty(WebConfig.KEY_WEB_HOST, "0.0.0.0");
        properties.setProperty(WebConfig.KEY_WEB_PORT, "18080");
        properties.setProperty(WebConfig.KEY_WEB_WORKER_THREADS, "12");
        properties.setProperty(WebConfig.KEY_WEB_REQUEST_ID_HEADER, "X-Correlation-Id");
        properties.setProperty(WebConfig.KEY_WEB_TRACE_ID_HEADER, "X-Trace");

        final WebConfig config = WebConfig.fromProperties(properties);

        assertEquals("0.0.0.0", config.host());
        assertEquals(18080, config.port());
        assertEquals(12, config.workerThreads());
        assertEquals("X-Correlation-Id", config.requestIdHeaderName());
        assertEquals("X-Trace", config.traceIdHeaderName());
    }

    /**
     * @brief 验证非法端口会被拒绝；
     *        Verify invalid port is rejected.
     */
    @Test
    void shouldRejectInvalidPort() {
        assertThrows(IllegalArgumentException.class, () -> new WebConfig("127.0.0.1", 0, 2));
    }

    /**
     * @brief 验证非法线程数会被拒绝；
     *        Verify invalid worker thread count is rejected.
     */
    @Test
    void shouldRejectInvalidWorkerThreadCount() {
        assertThrows(IllegalArgumentException.class, () -> new WebConfig("127.0.0.1", 8080, 0));
    }
}

