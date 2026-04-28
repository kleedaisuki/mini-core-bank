package com.moesegfault.banking.presentation.cli;

import java.util.Objects;
import java.util.function.Function;

/**
 * @brief 命令分发器（Command Dispatcher），按命令路径查表并调用对应 handler；
 *        Command dispatcher that looks up command-path mapping and invokes corresponding handler.
 *
 * @note 分发器依赖外部 resolver 获取 handler 实例，因此不负责对象生命周期；
 *       Dispatcher relies on external resolver for handler instances and does not own lifecycle.
 */
public final class CommandDispatcher {

    /**
     * @brief 命令注册表（Command Registry）；
     *        Command registry.
     */
    private final CommandRegistry commandRegistry;

    /**
     * @brief Handler 解析器（Handler Resolver）；
     *        External resolver for mapping handler type to handler instance.
     */
    private final Function<Class<? extends CliCommandHandler>, CliCommandHandler> handlerResolver;

    /**
     * @brief 构造命令分发器（Construct Command Dispatcher）；
     *        Construct command dispatcher.
     *
     * @param commandRegistry 命令注册表（Command registry）。
     * @param handlerResolver handler 解析器（Handler resolver）。
     */
    public CommandDispatcher(final CommandRegistry commandRegistry,
                             final Function<Class<? extends CliCommandHandler>, CliCommandHandler> handlerResolver) {
        this.commandRegistry = Objects.requireNonNull(commandRegistry, "commandRegistry must not be null");
        this.handlerResolver = Objects.requireNonNull(handlerResolver, "handlerResolver must not be null");
    }

    /**
     * @brief 分发命令（Dispatch Parsed Command）；
     *        Dispatch one parsed command to the mapped handler.
     *
     * @param command 已解析命令（Parsed command）。
     */
    public void dispatch(final ParsedCommand command) {
        final ParsedCommand normalizedCommand = Objects.requireNonNull(command, "command must not be null");

        final Class<? extends CliCommandHandler> handlerType = commandRegistry
                .findHandlerType(normalizedCommand.commandPath())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown command path: " + normalizedCommand.commandPath()));

        final CliCommandHandler handlerInstance = Objects.requireNonNull(
                handlerResolver.apply(handlerType),
                "Resolved handler instance must not be null: " + handlerType.getName());

        handlerInstance.handle(normalizedCommand);
    }
}
