package com.moesegfault.banking.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;
import org.junit.jupiter.api.Test;

/**
 * @brief PropertiesLoader 单元测试（Unit Test），验证 properties 与 JSON 合并逻辑；
 * Unit tests for PropertiesLoader merge behavior of properties and JSON configs.
 */
class PropertiesLoaderTest {

    /**
     * @brief 验证 JSON 配置可覆盖 properties 并支持数组拍平；
     * Verify JSON overrides .properties values and supports flattened arrays.
     */
    @Test
    void shouldMergePropertiesAndJsonWithJsonOverride() {
        final PropertiesLoader loader = new PropertiesLoader();

        final Properties properties = loader.load(
            "config-fixtures/test-application.properties",
            "config-fixtures/json"
        );

        assertEquals("fixture-app", properties.getProperty("app.name"));
        assertEquals("uuid", properties.getProperty("banking.id.strategy"));
        assertEquals("override> ", properties.getProperty("banking.cli.prompt"));
        assertEquals("L1", properties.getProperty("test.levels[0]"));
        assertEquals("L2", properties.getProperty("test.levels[1]"));
    }
}
