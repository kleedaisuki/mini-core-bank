package com.moesegfault.banking.infrastructure.migration;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.sql.DataSource;
import org.flywaydb.core.api.output.MigrateResult;
import org.junit.jupiter.api.Test;

/**
 * @brief FlywayMigrationRunner 单元测试（Unit Test），验证迁移开关与设置解析行为； Unit tests for
 * FlywayMigrationRunner migration toggle and settings parsing behavior.
 */
class FlywayMigrationRunnerTest {

    /**
     * @brief 验证当迁移开关关闭时不会触发迁移； Verify migration is skipped when Flyway is
     * disabled.
     */
    @Test
    void shouldSkipMigrationWhenDisabled() {
        final AtomicInteger calledCount = new AtomicInteger(0);
        final FlywayMigrationRunner runner = new FlywayMigrationRunner(
                new com.moesegfault.banking.infrastructure.config.PropertiesLoader(),
                new com.moesegfault.banking.infrastructure.config.DatabaseConfig(),
                (dataSource, settings) -> {
                    calledCount.incrementAndGet();
                    final MigrateResult result = new MigrateResult(null, null, null);
                    result.migrationsExecuted = 99;
                    return result;
                }
        );

        final Properties properties = new Properties();
        properties.setProperty(FlywayMigrationRunner.KEY_FLYWAY_ENABLED, "false");

        final int migratedCount = runner.migrate(mock(DataSource.class), properties);

        assertEquals(0, migratedCount);
        assertEquals(0, calledCount.get());
    }

    /**
     * @brief 验证迁移设置会被正确解析并传给 Flyway 执行器； Verify migration settings are parsed
     * and passed correctly to migrator.
     */
    @Test
    void shouldParseAndPassSettingsToMigrator() {
        final AtomicReference<FlywayMigrationRunner.FlywaySettings> capturedSettings = new AtomicReference<>();
        final FlywayMigrationRunner runner = new FlywayMigrationRunner(
                new com.moesegfault.banking.infrastructure.config.PropertiesLoader(),
                new com.moesegfault.banking.infrastructure.config.DatabaseConfig(),
                (dataSource, settings) -> {
                    capturedSettings.set(settings);
                    final MigrateResult result = new MigrateResult(null, null, null);
                    result.migrationsExecuted = 3;
                    return result;
                }
        );

        final Properties properties = new Properties();
        properties.setProperty(FlywayMigrationRunner.KEY_FLYWAY_ENABLED, "true");
        properties.setProperty(FlywayMigrationRunner.KEY_FLYWAY_LOCATIONS,
                "classpath:db/migration, classpath:db/custom ,filesystem:/tmp/sql");
        properties.setProperty(FlywayMigrationRunner.KEY_FLYWAY_ENCODING, "UTF-16");
        properties.setProperty(FlywayMigrationRunner.KEY_FLYWAY_VALIDATE_ON_MIGRATE, "false");
        properties.setProperty(FlywayMigrationRunner.KEY_FLYWAY_CLEAN_DISABLED, "true");

        final int migratedCount = runner.migrate(mock(DataSource.class), properties);

        assertEquals(3, migratedCount);
        assertArrayEquals(
                new String[]{"classpath:db/migration", "classpath:db/custom", "filesystem:/tmp/sql"},
                capturedSettings.get().locations()
        );
        assertEquals("UTF-16", capturedSettings.get().encoding());
        assertFalse(capturedSettings.get().validateOnMigrate());
        assertTrue(capturedSettings.get().cleanDisabled());
    }
}
