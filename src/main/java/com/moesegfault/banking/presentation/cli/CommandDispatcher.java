package com.moesegfault.banking.presentation.cli;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
        final ParsedCommand normalizedCommand = normalizeCommand(Objects.requireNonNull(command, "command must not be null"));

        final Class<? extends CliCommandHandler> handlerType = commandRegistry
                .findHandlerType(normalizedCommand.commandPath())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown command path: " + normalizedCommand.commandPath()));

        final CliCommandHandler handlerInstance = Objects.requireNonNull(
                handlerResolver.apply(handlerType),
                "Resolved handler instance must not be null: " + handlerType.getName());

        handlerInstance.handle(normalizedCommand);
    }

    /**
     * @brief 规范化命令（Normalize Command），解析命令前缀并绑定位置参数；
     *        Normalize command by resolving command prefix and binding positional arguments.
     *
     * @param command 已解析命令（Parsed command）。
     * @return 规范化命令（Normalized command）。
     */
    private ParsedCommand normalizeCommand(final ParsedCommand command) {
        if (commandRegistry.findHandlerType(command.commandPath()).isPresent()) {
            return command;
        }

        final Optional<ResolvedCommandPath> resolvedPath = resolveRegisteredPrefix(command.commandPathSegments());
        if (resolvedPath.isEmpty()) {
            return command;
        }

        final ResolvedCommandPath path = resolvedPath.orElseThrow();
        final List<String> positionalArguments = new ArrayList<>(command.positionalArguments());
        positionalArguments.addAll(command.commandPathSegments().subList(path.segmentCount(), command.commandPathSegments().size()));
        if (positionalArguments.isEmpty()) {
            return command;
        }

        return bindPositionalArguments(command, path.segments(), path.commandPath(), positionalArguments);
    }

    /**
     * @brief 解析已注册命令前缀（Resolve Registered Command Prefix）；
     *        Resolve the longest registered command prefix from parsed path segments.
     *
     * @param pathSegments 命令路径分段（Command path segments）。
     * @return 已解析命令路径（Resolved command path）。
     */
    private Optional<ResolvedCommandPath> resolveRegisteredPrefix(final List<String> pathSegments) {
        for (int segmentCount = pathSegments.size() - 1; segmentCount >= 1; segmentCount--) {
            final List<String> candidateSegments = pathSegments.subList(0, segmentCount);
            final String candidatePath = joinCommandPath(candidateSegments);
            if (commandRegistry.findHandlerType(candidatePath).isPresent()) {
                return Optional.of(new ResolvedCommandPath(candidateSegments, candidatePath));
            }
        }
        return Optional.empty();
    }

    /**
     * @brief 绑定位置参数（Bind Positional Arguments）；
     *        Bind positional arguments to documented option names.
     *
     * @param command             原解析命令（Original parsed command）。
     * @param commandPathSegments 规范化命令路径分段（Normalized command path segments）。
     * @param commandPath         规范化命令路径（Normalized command path）。
     * @param positionalArguments 位置参数列表（Positional argument list）。
     * @return 绑定后命令（Command with bound positional arguments）。
     */
    private static ParsedCommand bindPositionalArguments(
            final ParsedCommand command,
            final List<String> commandPathSegments,
            final String commandPath,
            final List<String> positionalArguments
    ) {
        final List<String> bindableOptionNames = bindableOptionNames(commandPath);
        if (bindableOptionNames.isEmpty()) {
            throw new IllegalArgumentException(
                    "Command does not support positional arguments: " + commandPath
                            + ". Use explicit --option values; run help " + commandPath + " for details.");
        }
        if (positionalArguments.size() > bindableOptionNames.size()) {
            throw new IllegalArgumentException(
                    "Too many positional arguments for " + commandPath
                            + ": expected at most " + bindableOptionNames.size()
                            + ", got " + positionalArguments.size()
                            + ". Run help " + commandPath + " for details.");
        }

        final Map<String, String> options = new LinkedHashMap<>(command.options());
        for (int index = 0; index < positionalArguments.size(); index++) {
            final String optionName = bindableOptionNames.get(index);
            if (options.containsKey(optionName)) {
                throw new IllegalArgumentException(
                        "Positional argument conflicts with explicit option --" + optionName);
            }
            options.put(optionName, positionalArguments.get(index));
        }

        return new ParsedCommand(command.rawInput(), commandPathSegments, positionalArguments, options);
    }

    /**
     * @brief 获取可绑定参数名（Get Bindable Option Names）；
     *        Get documented option names that can safely receive positional arguments.
     *
     * @param commandPath 命令路径（Command path）。
     * @return 可绑定参数名列表（Bindable option name list）。
     */
    private static List<String> bindableOptionNames(final String commandPath) {
        return CliHelpCatalog.find(commandPath)
                .map(CliHelpCatalog.CommandHelp::positionalOptionNames)
                .orElseGet(List::of);
    }

    /**
     * @brief 拼接命令路径（Join Command Path）；
     *        Join command-path segments into a normalized command path.
     *
     * @param segments 命令路径分段（Command path segments）。
     * @return 命令路径（Command path）。
     */
    private static String joinCommandPath(final List<String> segments) {
        return segments.stream()
                .reduce((left, right) -> left + " " + right)
                .orElseThrow(() -> new IllegalArgumentException("segments must not be empty"));
    }

    /**
     * @brief 已解析命令路径（Resolved Command Path）；
     *        Resolved command path from the longest registered prefix.
     *
     * @param segments    命令路径分段（Command path segments）。
     * @param commandPath 命令路径（Command path）。
     */
    private record ResolvedCommandPath(List<String> segments, String commandPath) {

        /**
         * @brief 构造已解析命令路径（Construct Resolved Command Path）；
         *        Construct resolved command path.
         */
        private ResolvedCommandPath {
            segments = List.copyOf(Objects.requireNonNull(segments, "segments must not be null"));
            commandPath = Objects.requireNonNull(commandPath, "commandPath must not be null");
        }

        /**
         * @brief 获取分段数量（Get Segment Count）；
         *        Get segment count.
         *
         * @return 分段数量（Segment count）。
         */
        private int segmentCount() {
            return segments.size();
        }
    }
}
