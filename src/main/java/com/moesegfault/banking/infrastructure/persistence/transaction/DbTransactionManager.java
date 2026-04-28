package com.moesegfault.banking.infrastructure.persistence.transaction;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @brief 数据库事务管理接口（Database Transaction Manager Interface），用于 application 层声明事务边界；
 *        Database transaction manager interface for declaring transaction boundaries in application layer.
 */
public interface DbTransactionManager {

    /**
     * @brief 在事务内执行并返回结果（Execute in Transaction and Return Result）；
     *        Execute action inside a transaction and return its result.
     *
     * @param <T> 返回类型（Result type）。
     * @param action 事务动作（Transactional action）。
     * @return 执行结果（Execution result）。
     */
    <T> T execute(Supplier<T> action);

    /**
     * @brief 在事务内执行无返回动作（Execute in Transaction Without Result）；
     *        Execute side-effect action inside a transaction.
     *
     * @param action 事务动作（Transactional action）。
     */
    default void execute(final Runnable action) {
        Objects.requireNonNull(action, "action must not be null");
        execute(() -> {
            action.run();
            return null;
        });
    }
}
