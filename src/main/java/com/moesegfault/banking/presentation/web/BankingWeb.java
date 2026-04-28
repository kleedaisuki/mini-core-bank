package com.moesegfault.banking.presentation.web;

import java.util.Objects;

/**
 * @brief Web 启动入口（Web Entry Point），负责触发装配并启动 REST 应用；
 *        Web entry point responsible for bootstrapping and starting REST application.
 */
public final class BankingWeb {

    /**
     * @brief Web 装配器（Web Bootstrap）；
     *        Web bootstrap.
     */
    private final WebBootstrap webBootstrap;

    /**
     * @brief 构造 Web 启动器（Construct Web Launcher）；
     *        Construct web launcher.
     *
     * @param webBootstrap Web 装配器（Web bootstrap）。
     */
    public BankingWeb(final WebBootstrap webBootstrap) {
        this.webBootstrap = Objects.requireNonNull(webBootstrap, "webBootstrap must not be null");
    }

    /**
     * @brief 启动 Web 应用（Launch Web Application）；
     *        Launch web application.
     *
     * @return 已启动应用对象（Started application object）。
     */
    public WebApplication launch() {
        final WebApplication application = webBootstrap.bootstrap();
        application.start();
        return application;
    }
}

