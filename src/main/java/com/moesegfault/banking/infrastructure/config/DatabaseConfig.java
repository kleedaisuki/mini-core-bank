package com.moesegfault.banking.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.DataSource;

/**
 * @brief 数据库配置（Database Configuration），根据 Properties 构建 JDBC DataSource；
 * Database configuration that builds JDBC DataSource from properties.
 */
public final class DatabaseConfig {

    /**
     * @brief 占位符匹配模式（Placeholder Pattern），用于解析 `${KEY}` 或 `${KEY:default}`；
     * Placeholder regex for `${KEY}` or `${KEY:default}` expressions.
     */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}:]+)(?::([^}]*))?}");

    /**
     * @brief JDBC URL 配置键；
     * Property key for JDBC URL.
     */
    public static final String KEY_DATASOURCE_URL = "spring.datasource.url";

    /**
     * @brief 用户名配置键；
     * Property key for database username.
     */
    public static final String KEY_DATASOURCE_USERNAME = "spring.datasource.username";

    /**
     * @brief 密码配置键；
     * Property key for database password.
     */
    public static final String KEY_DATASOURCE_PASSWORD = "spring.datasource.password";

    /**
     * @brief 驱动类配置键；
     * Property key for JDBC driver class name.
     */
    public static final String KEY_DATASOURCE_DRIVER_CLASS = "spring.datasource.driver-class-name";

    /**
     * @brief 最小空闲连接配置键；
     * Property key for Hikari minimum idle.
     */
    public static final String KEY_HIKARI_MIN_IDLE = "spring.datasource.hikari.minimum-idle";

    /**
     * @brief 最大连接池大小配置键；
     * Property key for Hikari maximum pool size.
     */
    public static final String KEY_HIKARI_MAX_POOL_SIZE = "spring.datasource.hikari.maximum-pool-size";

    /**
     * @brief 连接池名称配置键；
     * Property key for Hikari pool name.
     */
    public static final String KEY_HIKARI_POOL_NAME = "spring.datasource.hikari.pool-name";

    /**
     * @brief 默认最小空闲连接数；
     * Default minimum idle connection count.
     */
    public static final int DEFAULT_MIN_IDLE = 2;

    /**
     * @brief 默认最大连接池大小；
     * Default maximum pool size.
     */
    public static final int DEFAULT_MAX_POOL_SIZE = 10;

    /**
     * @brief 默认连接池名称；
     * Default connection pool name.
     */
    public static final String DEFAULT_POOL_NAME = "MiniCoreBankHikariPool";

    /**
     * @brief 创建默认 DataSource；
     * Create a DataSource using default PropertiesLoader.
     *
     * @return 已初始化的 DataSource；Initialized DataSource.
     */
    public DataSource createDataSource() {
        final Properties properties = new PropertiesLoader().load();
        return createDataSource(properties);
    }

    /**
     * @brief 使用指定配置创建 DataSource；
     * Create a DataSource using provided properties.
     *
     * @param properties 配置集合；Configuration properties.
     * @return 已初始化的 DataSource；Initialized DataSource.
     */
    public DataSource createDataSource(final Properties properties) {
        Objects.requireNonNull(properties, "properties must not be null");

        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(requireText(properties, KEY_DATASOURCE_URL));
        hikariConfig.setUsername(requireText(properties, KEY_DATASOURCE_USERNAME));
        hikariConfig.setPassword(requireText(properties, KEY_DATASOURCE_PASSWORD));

        final String driverClassName = properties.getProperty(KEY_DATASOURCE_DRIVER_CLASS);
        if (driverClassName != null && !driverClassName.trim().isEmpty()) {
            hikariConfig.setDriverClassName(driverClassName.trim());
        }

        hikariConfig.setMinimumIdle(parseInt(properties, KEY_HIKARI_MIN_IDLE, DEFAULT_MIN_IDLE));
        hikariConfig.setMaximumPoolSize(parseInt(properties, KEY_HIKARI_MAX_POOL_SIZE, DEFAULT_MAX_POOL_SIZE));
        hikariConfig.setPoolName(resolvePlaceholders(properties.getProperty(KEY_HIKARI_POOL_NAME, DEFAULT_POOL_NAME)));
        hikariConfig.setInitializationFailTimeout(-1);

        return new HikariDataSource(hikariConfig);
    }

    /**
     * @brief 读取并校验非空白字符串配置；
     * Read and validate a non-blank text property.
     *
     * @param properties 配置集合；Configuration properties.
     * @param key 配置键；Property key.
     * @return 非空白配置值；Non-blank property value.
     */
    private String requireText(final Properties properties, final String key) {
        final String rawValue = properties.getProperty(key);
        if (rawValue == null || rawValue.trim().isEmpty()) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        final String resolvedValue = resolvePlaceholders(rawValue).trim();
        if (resolvedValue.isEmpty()) {
            throw new IllegalStateException("Resolved property is blank: " + key);
        }
        return resolvedValue;
    }

    /**
     * @brief 读取整数配置，缺失时返回默认值；
     * Read integer property with default fallback.
     *
     * @param properties 配置集合；Configuration properties.
     * @param key 配置键；Property key.
     * @param defaultValue 默认值；Default value.
     * @return 解析后的整数值；Parsed integer value.
     */
    private int parseInt(final Properties properties, final String key, final int defaultValue) {
        final String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        final String resolvedValue = resolvePlaceholders(value).trim();
        try {
            return Integer.parseInt(resolvedValue);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Invalid integer property: " + key + " = " + resolvedValue, exception);
        }
    }

    /**
     * @brief 解析 `${ENV}` 与 `${ENV:default}` 占位符；
     * Resolve `${ENV}` and `${ENV:default}` placeholders.
     *
     * @param rawValue 原始配置值；Raw property value.
     * @return 解析后的字符串；Resolved string.
     */
    private String resolvePlaceholders(final String rawValue) {
        if (rawValue == null || rawValue.indexOf('$') < 0) {
            return rawValue;
        }

        final Matcher matcher = PLACEHOLDER_PATTERN.matcher(rawValue);
        final StringBuffer resolved = new StringBuffer();
        while (matcher.find()) {
            final String key = matcher.group(1);
            final String defaultValue = matcher.group(2);
            final String replacement = lookupPropertyValue(key, defaultValue);
            matcher.appendReplacement(resolved, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(resolved);
        return resolved.toString();
    }

    /**
     * @brief 查找占位符值，优先环境变量，再看 JVM 系统属性，最后回退默认值；
     * Resolve placeholder from environment, then JVM property, then default fallback.
     *
     * @param key 占位符键；Placeholder key.
     * @param defaultValue 默认值；Default value.
     * @return 解析值；Resolved value.
     */
    private String lookupPropertyValue(final String key, final String defaultValue) {
        final String environmentValue = System.getenv(key);
        if (environmentValue != null) {
            return environmentValue;
        }

        final String systemValue = System.getProperty(key);
        if (systemValue != null) {
            return systemValue;
        }

        if (defaultValue != null) {
            return defaultValue;
        }

        throw new IllegalStateException("Missing value for placeholder: " + key);
    }
}
