package com.moesegfault.banking.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import org.junit.jupiter.api.Test;

/**
 * @brief DatabaseConfig 单元测试（Unit Test），验证 DataSource 构建规则；
 * Unit tests for DatabaseConfig DataSource construction rules.
 */
class DatabaseConfigTest {

    /**
     * @brief 验证配置可正确映射到 HikariDataSource；
     * Verify properties are correctly mapped to HikariDataSource.
     */
    @Test
    void shouldCreateHikariDataSourceFromProperties() {
        final Properties properties = new Properties();
        properties.setProperty(DatabaseConfig.KEY_DATASOURCE_URL, "jdbc:postgresql://localhost:5432/unit_test");
        properties.setProperty(DatabaseConfig.KEY_DATASOURCE_USERNAME, "unit_user");
        properties.setProperty(DatabaseConfig.KEY_DATASOURCE_PASSWORD, "unit_password");
        properties.setProperty(DatabaseConfig.KEY_HIKARI_MIN_IDLE, "3");
        properties.setProperty(DatabaseConfig.KEY_HIKARI_MAX_POOL_SIZE, "9");
        properties.setProperty(DatabaseConfig.KEY_HIKARI_POOL_NAME, "UnitPool");

        final DatabaseConfig databaseConfig = new DatabaseConfig();
        final HikariDataSource dataSource = (HikariDataSource) databaseConfig.createDataSource(properties);

        try {
            assertEquals("jdbc:postgresql://localhost:5432/unit_test", dataSource.getJdbcUrl());
            assertEquals("unit_user", dataSource.getUsername());
            assertEquals("unit_password", dataSource.getPassword());
            assertEquals(3, dataSource.getMinimumIdle());
            assertEquals(9, dataSource.getMaximumPoolSize());
            assertEquals("UnitPool", dataSource.getPoolName());
        } finally {
            dataSource.close();
        }
    }

    /**
     * @brief 验证缺失必需配置时抛出异常；
     * Verify missing required properties raise an exception.
     */
    @Test
    void shouldFailWhenRequiredPropertyIsMissing() {
        final Properties properties = new Properties();
        properties.setProperty(DatabaseConfig.KEY_DATASOURCE_USERNAME, "unit_user");
        properties.setProperty(DatabaseConfig.KEY_DATASOURCE_PASSWORD, "unit_password");

        final DatabaseConfig databaseConfig = new DatabaseConfig();

        assertThrows(IllegalStateException.class, () -> databaseConfig.createDataSource(properties));
    }
}
