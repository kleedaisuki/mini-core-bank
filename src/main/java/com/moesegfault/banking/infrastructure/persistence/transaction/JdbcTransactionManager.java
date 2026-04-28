package com.moesegfault.banking.infrastructure.persistence.transaction;

import java.util.Objects;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @brief JDBC 事务管理实现（JDBC Transaction Manager Implementation），基于 Spring
 *        事务模板封装提交与回滚；
 *        JDBC transaction manager implementation built on Spring transaction
 *        template for commit and rollback.
 */
public final class JdbcTransactionManager implements DbTransactionManager {

    /**
     * @brief 平台事务管理器（Platform Transaction Manager）；
     *        Underlying Spring platform transaction manager.
     */
    private final PlatformTransactionManager platformTransactionManager;

    /**
     * @brief 事务模板（Transaction Template）；
     *        Spring transaction template used to execute callbacks.
     */
    private final TransactionTemplate transactionTemplate;

    /**
     * @brief 使用数据源构造事务管理器（Construct Transaction Manager with DataSource）；
     *        Construct transaction manager with datasource.
     *
     * @param dataSource 数据源（Data source）。
     */
    public JdbcTransactionManager(final DataSource dataSource) {
        this(new DataSourceTransactionManager(Objects.requireNonNull(dataSource, "dataSource must not be null")));
    }

    /**
     * @brief 使用平台事务管理器构造（Construct with PlatformTransactionManager）；
     *        Construct with Spring platform transaction manager.
     *
     * @param platformTransactionManager 平台事务管理器（Platform transaction manager）。
     */
    public JdbcTransactionManager(final PlatformTransactionManager platformTransactionManager) {
        this(platformTransactionManager, new TransactionTemplate(
                Objects.requireNonNull(platformTransactionManager, "platformTransactionManager must not be null")));
    }

    /**
     * @brief 使用事务模板构造（Construct with TransactionTemplate）；
     *        Construct with Spring transaction template.
     *
     * @param transactionTemplate 事务模板（Transaction template）。
     */
    public JdbcTransactionManager(final TransactionTemplate transactionTemplate) {
        this(Objects.requireNonNull(transactionTemplate, "transactionTemplate must not be null")
                .getTransactionManager(),
                transactionTemplate);
    }

    /**
     * @brief 使用底层组件构造（Construct with Low-level Components）；
     *        Construct with low-level Spring transaction components.
     *
     * @param platformTransactionManager 平台事务管理器（Platform transaction manager）。
     * @param transactionTemplate        事务模板（Transaction template）。
     */
    public JdbcTransactionManager(
            final PlatformTransactionManager platformTransactionManager,
            final TransactionTemplate transactionTemplate) {
        this.platformTransactionManager = Objects.requireNonNull(
                platformTransactionManager,
                "platformTransactionManager must not be null");
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate, "transactionTemplate must not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T execute(final Supplier<T> action) {
        final Supplier<T> normalizedAction = Objects.requireNonNull(action, "action must not be null");
        return transactionTemplate.execute(status -> normalizedAction.get());
    }

    /**
     * @brief 获取平台事务管理器（Get Platform Transaction Manager）；
     *        Get underlying platform transaction manager.
     *
     * @return 平台事务管理器（Platform transaction manager）。
     */
    public PlatformTransactionManager platformTransactionManager() {
        return platformTransactionManager;
    }

    /**
     * @brief 获取事务模板（Get Transaction Template）；
     *        Get underlying transaction template.
     *
     * @return 事务模板（Transaction template）。
     */
    public TransactionTemplate transactionTemplate() {
        return transactionTemplate;
    }
}
