package com.moesegfault.banking.presentation.cli.bootstrap;

import com.moesegfault.banking.presentation.cli.CliApplication;
import java.util.Objects;

/**
 * @brief CLI 运行时上下文（CLI Runtime Context），持有可复用 CLI 应用与底层资源释放动作；
 *        CLI runtime context that owns the reusable CLI application and low-level resource cleanup.
 */
public final class CliRuntime implements AutoCloseable {

    /**
     * @brief CLI 应用对象（CLI Application）；
     *        CLI application.
     */
    private final CliApplication application;

    /**
     * @brief 资源释放动作（Resource Cleanup Action）；
     *        Resource cleanup action.
     */
    private final AutoCloseable cleanup;

    /**
     * @brief 构造 CLI 运行时上下文（Construct CLI Runtime Context）；
     *        Construct CLI runtime context.
     *
     * @param application CLI 应用对象（CLI application）。
     * @param cleanup     资源释放动作（Resource cleanup action）。
     */
    public CliRuntime(final CliApplication application, final AutoCloseable cleanup) {
        this.application = Objects.requireNonNull(application, "application must not be null");
        this.cleanup = Objects.requireNonNull(cleanup, "cleanup must not be null");
    }

    /**
     * @brief 获取 CLI 应用对象（Get CLI Application）；
     *        Get CLI application.
     *
     * @return CLI 应用对象（CLI application）。
     */
    public CliApplication application() {
        return application;
    }

    /**
     * @brief 关闭运行时资源（Close Runtime Resources）；
     *        Close runtime resources.
     *
     * @throws Exception 当底层资源关闭失败时抛出（Thrown when underlying cleanup fails）。
     */
    @Override
    public void close() throws Exception {
        cleanup.close();
    }
}
