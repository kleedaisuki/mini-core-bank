package com.moesegfault.banking.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

/**
 * @brief AppConfig 单元测试（Unit Test），验证默认组装可用；
 * Unit tests for AppConfig default composition.
 */
class AppConfigTest {

    /**
     * @brief 验证默认配置可创建核心组件；
     * Verify default configuration can construct core components.
     */
    @Test
    void shouldCreateDefaultAppConfig() {
        final AppConfig appConfig = AppConfig.createDefault();

        assertNotNull(appConfig.properties());
        assertNotNull(appConfig.dataSource());
        assertNotNull(appConfig.idGenerator());

        if (appConfig.dataSource() instanceof HikariDataSource hikariDataSource) {
            hikariDataSource.close();
        }
    }
}
