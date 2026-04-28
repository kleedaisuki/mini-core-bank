package com.moesegfault.banking.presentation.gui;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @brief GUI 装配器（GUI Bootstrap），负责装配上下文、页面注册、运行时并做技术栈选择；
 *        GUI bootstrap that assembles context, page registry, runtime and performs toolkit selection.
 */
public final class GuiBootstrap {

    /**
     * @brief 运行时解析器（Runtime Resolver）；
     *        Resolver mapping toolkit type to runtime instance.
     */
    private final Function<GuiToolkitType, GuiRuntime> runtimeResolver;

    /**
     * @brief 上下文提供器（Context Supplier）；
     *        Supplier creating session context.
     */
    private final Supplier<GuiContext> contextSupplier;

    /**
     * @brief GUI 模块集合（GUI Module List）；
     *        GUI module list for bulk page registration.
     */
    private final List<GuiModule> guiModules;

    /**
     * @brief 首页面标识（Home Page Identifier）；
     *        Home page identifier.
     */
    private final GuiPageId homePageId;

    /**
     * @brief 构造 GUI 装配器（Construct GUI Bootstrap）；
     *        Construct GUI bootstrap.
     *
     * @param runtimeResolver 运行时解析器（Runtime resolver）。
     * @param contextSupplier 上下文提供器（Context supplier）。
     * @param homePageId 首页面标识（Home page identifier）。
     * @param guiModules GUI 模块数组（GUI module array）。
     */
    public GuiBootstrap(final Function<GuiToolkitType, GuiRuntime> runtimeResolver,
                        final Supplier<GuiContext> contextSupplier,
                        final GuiPageId homePageId,
                        final GuiModule... guiModules) {
        this.runtimeResolver = Objects.requireNonNull(runtimeResolver, "runtimeResolver must not be null");
        this.contextSupplier = Objects.requireNonNull(contextSupplier, "contextSupplier must not be null");
        this.guiModules = copyModules(guiModules);
        this.homePageId = Objects.requireNonNull(homePageId, "homePageId must not be null");
    }

    /**
     * @brief 按技术栈装配 GUI 应用（Bootstrap GUI Application By Toolkit）；
     *        Bootstrap GUI application by toolkit type.
     *
     * @param toolkitType GUI 技术栈（GUI toolkit type）。
     * @return GUI 应用对象（GUI application object）。
     */
    public GuiApplication bootstrap(final GuiToolkitType toolkitType) {
        final GuiToolkitType normalizedToolkitType = Objects.requireNonNull(toolkitType, "toolkitType must not be null");
        final GuiRuntime runtime = Objects.requireNonNull(
                runtimeResolver.apply(normalizedToolkitType),
                "runtimeResolver must not return null for toolkit: " + normalizedToolkitType);
        final GuiContext context = Objects.requireNonNull(contextSupplier.get(), "contextSupplier must not return null");
        final GuiPageRegistry pageRegistry = new GuiPageRegistry();
        final GuiPageRegistrar pageRegistrar = new GuiPageRegistrar(pageRegistry);
        for (GuiModule guiModule : guiModules) {
            final GuiModule normalizedModule = Objects.requireNonNull(guiModule, "guiModules contains null module");
            normalizedModule.registerPages(pageRegistrar);
        }
        final GuiNavigator navigator = new GuiNavigator(context, pageRegistry, runtime);
        return new GuiApplication(context, navigator, runtime, homePageId);
    }

    /**
     * @brief 复制模块集合（Copy GUI Module Collection）；
     *        Copy GUI module array into immutable list.
     *
     * @param modules 模块数组（Module array）。
     * @return 不可变模块列表（Immutable module list）。
     */
    private static List<GuiModule> copyModules(final GuiModule... modules) {
        Objects.requireNonNull(modules, "guiModules must not be null");
        final List<GuiModule> copiedModules = new ArrayList<>();
        for (GuiModule module : modules) {
            copiedModules.add(Objects.requireNonNull(module, "guiModules contains null module"));
        }
        return List.copyOf(copiedModules);
    }
}
