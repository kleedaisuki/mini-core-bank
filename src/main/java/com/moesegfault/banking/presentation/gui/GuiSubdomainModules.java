package com.moesegfault.banking.presentation.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @brief GUI 子领域模块组合器（GUI Subdomain Module Composer），用于把多个子领域模块合并为一个注册入口；
 *        GUI subdomain module composer that merges multiple domain modules into one registration entry.
 */
public final class GuiSubdomainModules implements GuiModule {

    /**
     * @brief 子领域模块列表（Subdomain Module List）；
     *        Immutable subdomain module list.
     */
    private final List<GuiModule> subdomainModules;

    /**
     * @brief 构造子领域模块组合器（Construct Subdomain Module Composer）；
     *        Construct subdomain module composer by module array.
     *
     * @param subdomainModules 子领域模块数组（Subdomain module array）。
     */
    public GuiSubdomainModules(final GuiModule... subdomainModules) {
        this.subdomainModules = copyModules(subdomainModules);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerPages(final GuiPageRegistrar registrar) {
        final GuiPageRegistrar normalizedRegistrar = Objects.requireNonNull(registrar, "registrar must not be null");
        for (GuiModule subdomainModule : subdomainModules) {
            subdomainModule.registerPages(normalizedRegistrar);
        }
    }

    /**
     * @brief 获取模块名称（Get Module Name）；
     *        Get aggregate module name for diagnostics.
     *
     * @return 模块名称（Module name）。
     */
    @Override
    public String moduleName() {
        return "GuiSubdomainModules";
    }

    /**
     * @brief 获取子领域模块快照（Get Subdomain Module Snapshot）；
     *        Get immutable snapshot of subdomain modules.
     *
     * @return 子领域模块列表（Subdomain module list）。
     */
    public List<GuiModule> modules() {
        return subdomainModules;
    }

    /**
     * @brief 复制并校验模块数组（Copy And Validate Module Array）；
     *        Copy and validate module array into immutable list.
     *
     * @param modules 模块数组（Module array）。
     * @return 不可变模块列表（Immutable module list）。
     */
    private static List<GuiModule> copyModules(final GuiModule... modules) {
        Objects.requireNonNull(modules, "subdomainModules must not be null");
        final List<GuiModule> copiedModules = new ArrayList<>();
        for (GuiModule module : modules) {
            copiedModules.add(Objects.requireNonNull(module, "subdomainModules contains null module"));
        }
        return List.copyOf(copiedModules);
    }
}
