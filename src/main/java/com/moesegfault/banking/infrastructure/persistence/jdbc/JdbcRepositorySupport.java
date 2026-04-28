package com.moesegfault.banking.infrastructure.persistence.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * @brief JDBC 仓储通用支撑（JDBC Repository Shared Support），封装可复用的时间与查询辅助逻辑；
 *        Shared JDBC repository support that encapsulates reusable timestamp and query helpers.
 */
final class JdbcRepositorySupport {

    /**
     * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
     *        Non-instantiable utility class.
     */
    private JdbcRepositorySupport() {
    }

    /**
     * @brief 将 `Instant` 转换为 JDBC `Timestamp`（Convert Instant to JDBC Timestamp）；
     *        Convert `Instant` to JDBC `Timestamp`.
     *
     * @param instant 时间点（Instant, nullable）。
     * @return `Timestamp` 或 null（Timestamp or null）。
     */
    static Timestamp toTimestamp(final Instant instant) {
        return instant == null ? null : Timestamp.from(instant);
    }

    /**
     * @brief 从结果集读取 `Instant`（Read Instant from ResultSet）；
     *        Read `Instant` from `ResultSet`.
     *
     * @param resultSet 结果集（ResultSet）。
     * @param column 列名（Column name）。
     * @return 时间点或 null（Instant or null）。
     * @throws SQLException 读取失败时抛出（Thrown when SQL read fails）。
     */
    static Instant getInstant(final ResultSet resultSet, final String column) throws SQLException {
        final Timestamp timestamp = resultSet.getTimestamp(column);
        return timestamp == null ? null : timestamp.toInstant();
    }

    /**
     * @brief 查询单行可选值（Query Optional Single Row）；
     *        Query an optional single row.
     *
     * @param <T> 返回类型（Result type）。
     * @param jdbcTemplate JDBC 模板（JDBC template）。
     * @param sql SQL 语句（SQL statement）。
     * @param rowMapper 行映射器（Row mapper）。
     * @param args 参数列表（Arguments）。
     * @return 单行可选值（Optional single-row result）。
     */
    static <T> Optional<T> queryOptional(
            final JdbcTemplate jdbcTemplate,
            final String sql,
            final RowMapper<T> rowMapper,
            final Object... args
    ) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, args));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}
