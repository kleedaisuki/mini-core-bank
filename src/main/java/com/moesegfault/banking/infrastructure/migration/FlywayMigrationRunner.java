package com.moesegfault.banking.infrastructure.migration;

import com.moesegfault.banking.infrastructure.config.AppConfig;
import com.moesegfault.banking.infrastructure.config.DatabaseConfig;
import com.moesegfault.banking.infrastructure.config.PropertiesLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.output.MigrateResult;

/**
 * @brief Flyway 迁移执行器（Flyway Migration Runner），负责在启动阶段执行数据库 schema 迁移； Flyway
 * migration runner responsible for executing database schema migrations during
 * startup.
 */
public final class FlywayMigrationRunner {

    /**
     * @brief Flyway 启用开关配置键； Property key for enabling Flyway migration.
     */
    public static final String KEY_FLYWAY_ENABLED = "spring.flyway.enabled";

    /**
     * @brief Flyway 迁移脚本位置配置键； Property key for Flyway script locations.
     */
    public static final String KEY_FLYWAY_LOCATIONS = "spring.flyway.locations";

    /**
     * @brief Flyway 编码配置键； Property key for Flyway encoding.
     */
    public static final String KEY_FLYWAY_ENCODING = "spring.flyway.encoding";

    /**
     * @brief Flyway 迁移前校验开关配置键； Property key for Flyway validate-on-migrate.
     */
    public static final String KEY_FLYWAY_VALIDATE_ON_MIGRATE = "spring.flyway.validate-on-migrate";

    /**
     * @brief Flyway clean 禁用开关配置键； Property key for Flyway clean-disabled.
     */
    public static final String KEY_FLYWAY_CLEAN_DISABLED = "spring.flyway.clean-disabled";

    /**
     * @brief 默认迁移启用值； Default value for migration enabled flag.
     */
    public static final boolean DEFAULT_ENABLED = true;

    /**
     * @brief 默认迁移脚本位置； Default migration script location.
     */
    public static final String DEFAULT_LOCATIONS = "classpath:db/migration";

    /**
     * @brief 默认脚本编码； Default script encoding.
     */
    public static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * @brief 默认迁移前校验开关； Default validate-on-migrate flag.
     */
    public static final boolean DEFAULT_VALIDATE_ON_MIGRATE = true;

    /**
     * @brief 默认 clean 禁用开关； Default clean-disabled flag.
     */
    public static final boolean DEFAULT_CLEAN_DISABLED = true;

    /**
     * @brief Flyway 执行函数（Migrator），用于隔离可测试性； Flyway migrator function isolated
     * for testability.
     */
    private final FlywayMigrator flywayMigrator;

    /**
     * @brief 配置加载器； Configuration loader.
     */
    private final PropertiesLoader propertiesLoader;

    /**
     * @brief 数据源配置器； Database configuration builder.
     */
    private final DatabaseConfig databaseConfig;

    /**
     * @brief 使用默认组件构建迁移执行器； Construct migration runner with default
     * collaborators.
     */
    public FlywayMigrationRunner() {
        this(new PropertiesLoader(), new DatabaseConfig(), FlywayMigrationRunner::migrateWithFlyway);
    }

    /**
     * @brief 使用注入组件构建迁移执行器； Construct migration runner with injected
     * collaborators.
     *
     * @param propertiesLoader 配置加载器；Properties loader.
     * @param databaseConfig 数据库配置器；Database config builder.
     * @param flywayMigrator Flyway 执行函数；Flyway migrator function.
     */
    public FlywayMigrationRunner(final PropertiesLoader propertiesLoader,
            final DatabaseConfig databaseConfig,
            final FlywayMigrator flywayMigrator) {
        this.propertiesLoader = Objects.requireNonNull(propertiesLoader, "propertiesLoader must not be null");
        this.databaseConfig = Objects.requireNonNull(databaseConfig, "databaseConfig must not be null");
        this.flywayMigrator = Objects.requireNonNull(flywayMigrator, "flywayMigrator must not be null");
    }

    /**
     * @brief 执行迁移（读取默认配置并创建数据源）； Execute migration by loading default
     * properties and creating data source.
     *
     * @return 本次执行的迁移条数；Number of migrations executed in this run.
     */
    public int migrate() {
        final Properties properties = propertiesLoader.load();
        final DataSource dataSource = databaseConfig.createDataSource(properties);

        try {
            return migrate(dataSource, properties);
        } finally {
            closeIfPossible(dataSource);
        }
    }

    /**
     * @brief 执行迁移（使用外部组装的 AppConfig）； Execute migration using externally
     * assembled AppConfig.
     *
     * @param appConfig 应用配置；Application config.
     * @return 本次执行的迁移条数；Number of migrations executed in this run.
     */
    public int migrate(final AppConfig appConfig) {
        Objects.requireNonNull(appConfig, "appConfig must not be null");
        return migrate(appConfig.dataSource(), appConfig.properties());
    }

    /**
     * @brief 执行迁移（使用指定数据源和配置）； Execute migration using the provided data source
     * and properties.
     *
     * @param dataSource 数据源；Data source.
     * @param properties 配置集合；Configuration properties.
     * @return 本次执行的迁移条数；Number of migrations executed in this run.
     */
    public int migrate(final DataSource dataSource, final Properties properties) {
        Objects.requireNonNull(dataSource, "dataSource must not be null");
        Objects.requireNonNull(properties, "properties must not be null");

        final FlywaySettings settings = parseSettings(properties);
        if (!settings.enabled()) {
            return 0;
        }

        final MigrateResult result = flywayMigrator.migrate(dataSource, settings);
        if (result == null) {
            throw new IllegalStateException("Flyway migrator returned null result");
        }
        return result.migrationsExecuted;
    }

    /**
     * @brief 默认 Flyway 执行逻辑； Default Flyway migration execution logic.
     *
     * @param dataSource 数据源；Data source.
     * @param settings Flyway 设置；Flyway settings.
     * @return Flyway 迁移结果；Flyway migration result.
     */
    private static MigrateResult migrateWithFlyway(final DataSource dataSource, final FlywaySettings settings) {
        final FluentConfiguration configuration = Flyway.configure()
                .dataSource(dataSource)
                .locations(settings.locations())
                .encoding(settings.encoding())
                .validateOnMigrate(settings.validateOnMigrate())
                .cleanDisabled(settings.cleanDisabled());

        return configuration.load().migrate();
    }

    /**
     * @brief 解析 Flyway 设置； Parse Flyway settings from properties.
     *
     * @param properties 配置集合；Configuration properties.
     * @return 解析后的 Flyway 设置；Parsed Flyway settings.
     */
    private FlywaySettings parseSettings(final Properties properties) {
        final boolean enabled = parseBoolean(properties, KEY_FLYWAY_ENABLED, DEFAULT_ENABLED);
        final String encoding = properties.getProperty(KEY_FLYWAY_ENCODING, DEFAULT_ENCODING).trim();
        final boolean validateOnMigrate = parseBoolean(properties,
                KEY_FLYWAY_VALIDATE_ON_MIGRATE,
                DEFAULT_VALIDATE_ON_MIGRATE);
        final boolean cleanDisabled = parseBoolean(properties,
                KEY_FLYWAY_CLEAN_DISABLED,
                DEFAULT_CLEAN_DISABLED);
        final String[] locations = parseLocations(properties.getProperty(KEY_FLYWAY_LOCATIONS, DEFAULT_LOCATIONS));

        return new FlywaySettings(enabled, locations, encoding, validateOnMigrate, cleanDisabled);
    }

    /**
     * @brief 解析布尔配置； Parse a boolean configuration value.
     *
     * @param properties 配置集合；Configuration properties.
     * @param key 配置键；Property key.
     * @param defaultValue 默认值；Default value.
     * @return 解析后的布尔值；Parsed boolean value.
     */
    private boolean parseBoolean(final Properties properties, final String key, final boolean defaultValue) {
        final String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    /**
     * @brief 解析迁移脚本位置列表； Parse migration location list.
     *
     * @param rawLocations 原始 locations 字符串；Raw locations string.
     * @return 位置数组；Location array.
     */
    private String[] parseLocations(final String rawLocations) {
        if (rawLocations == null || rawLocations.trim().isEmpty()) {
            return new String[]{DEFAULT_LOCATIONS};
        }

        final String[] candidates = rawLocations.split(",");
        final List<String> resolvedLocations = new ArrayList<>(candidates.length);
        for (String candidate : candidates) {
            final String trimmed = candidate.trim();
            if (!trimmed.isEmpty()) {
                resolvedLocations.add(trimmed);
            }
        }

        if (resolvedLocations.isEmpty()) {
            return new String[]{DEFAULT_LOCATIONS};
        }
        return resolvedLocations.toArray(String[]::new);
    }

    /**
     * @brief 尝试关闭可关闭的数据源； Try to close the data source when it is closeable.
     *
     * @param dataSource 数据源；Data source.
     */
    private void closeIfPossible(final DataSource dataSource) {
        if (dataSource instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception ignored) {
                // Intentionally ignore close failures to keep shutdown path resilient.
            }
        }
    }

    /**
     * @brief Flyway 执行函数接口（Functional Interface）； Functional interface
     * representing Flyway migration execution.
     */
    @FunctionalInterface
    public interface FlywayMigrator {

        /**
         * @brief 执行迁移并返回结果； Execute migrations and return result.
         *
         * @param dataSource 数据源；Data source.
         * @param settings Flyway 设置；Flyway settings.
         * @return 迁移结果；Migration result.
         */
        MigrateResult migrate(DataSource dataSource, FlywaySettings settings);
    }

    /**
     * @brief Flyway 设置值对象（Value Object）； Value object representing Flyway
     * runtime settings.
     *
     * @param enabled 是否启用迁移；Whether migration is enabled.
     * @param locations 迁移脚本位置列表；Migration script locations.
     * @param encoding 脚本编码；Script encoding.
     * @param validateOnMigrate 迁移前是否校验；Whether to validate before migration.
     * @param cleanDisabled 是否禁用 clean；Whether clean is disabled.
     */
    public record FlywaySettings(boolean enabled,
            String[] locations,
            String encoding,
            boolean validateOnMigrate,
            boolean cleanDisabled) {

        /**
         * @brief 规范化并校验 Flyway 设置； Normalize and validate Flyway settings.
         */
        public FlywaySettings {
            Objects.requireNonNull(locations, "locations must not be null");
            Objects.requireNonNull(encoding, "encoding must not be null");
        }
    }
}
