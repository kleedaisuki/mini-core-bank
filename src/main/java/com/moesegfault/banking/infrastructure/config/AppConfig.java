package com.moesegfault.banking.infrastructure.config;

import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.id.UuidIdGenerator;
import java.util.Objects;
import java.util.Properties;
import javax.sql.DataSource;

/**
 * @brief 应用组装配置（Application Composition Configuration），集中初始化基础设施组件；
 * Application composition configuration that initializes infrastructure components.
 */
public final class AppConfig {

    /**
     * @brief 应用级配置集合；
     * Application-level merged properties.
     */
    private final Properties properties;

    /**
     * @brief 数据源组件（DataSource）；
     * Configured JDBC DataSource.
     */
    private final DataSource dataSource;

    /**
     * @brief ID 生成器组件（IdGenerator）；
     * Configured identifier generator.
     */
    private final IdGenerator idGenerator;

    /**
     * @brief 使用默认实现创建 AppConfig；
     * Create AppConfig with default component implementations.
     *
     * @return 组装后的 AppConfig；Composed AppConfig.
     */
    public static AppConfig createDefault() {
        return new AppConfig(new PropertiesLoader(), new DatabaseConfig(), new UuidIdGenerator());
    }

    /**
     * @brief 使用注入组件创建 AppConfig；
     * Create AppConfig with injected collaborators.
     *
     * @param propertiesLoader 配置加载器；Properties loader.
     * @param databaseConfig 数据库配置器；Database config builder.
     * @param idGenerator ID 生成器；Identifier generator.
     */
    public AppConfig(final PropertiesLoader propertiesLoader,
                     final DatabaseConfig databaseConfig,
                     final IdGenerator idGenerator) {
        Objects.requireNonNull(propertiesLoader, "propertiesLoader must not be null");
        Objects.requireNonNull(databaseConfig, "databaseConfig must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");

        this.properties = propertiesLoader.load();
        this.dataSource = databaseConfig.createDataSource(this.properties);
    }

    /**
     * @brief 获取合并后的应用配置；
     * Get merged application properties.
     *
     * @return 合并后的 Properties；Merged properties.
     */
    public Properties properties() {
        return properties;
    }

    /**
     * @brief 获取数据源；
     * Get configured DataSource.
     *
     * @return DataSource 实例；DataSource instance.
     */
    public DataSource dataSource() {
        return dataSource;
    }

    /**
     * @brief 获取 ID 生成器；
     * Get configured IdGenerator.
     *
     * @return IdGenerator 实例；IdGenerator instance.
     */
    public IdGenerator idGenerator() {
        return idGenerator;
    }
}
