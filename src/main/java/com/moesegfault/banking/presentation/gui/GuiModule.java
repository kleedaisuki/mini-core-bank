package com.moesegfault.banking.presentation.gui;

/**
 * @brief GUI 模块接口（GUI Module Interface），用于按业务域拆分页面注册逻辑；
 *        GUI module contract used to split page registration by business domain.
 */
public interface GuiModule {

    /**
     * @brief 注册模块页面（Register Module Pages）；
     *        Register module pages into registrar.
     *
     * @param registrar 页面注册器（Page registrar）。
     */
    void registerPages(GuiPageRegistrar registrar);

    /**
     * @brief 获取模块名称（Get Module Name）；
     *        Get module name for diagnostics.
     *
     * @return 模块名称（Module name）。
     */
    default String moduleName() {
        return getClass().getSimpleName();
    }

    /**
     * @brief 组合多个子领域模块（Compose Subdomain Modules）；
     *        Compose multiple subdomain modules into one module for bulk registration.
     *
     * @param subdomainModules 子领域模块数组（Subdomain module array）。
     * @return 组合后的模块（Composed module）。
     */
    static GuiModule subdomains(final GuiModule... subdomainModules) {
        return new GuiSubdomainModules(subdomainModules);
    }
}
