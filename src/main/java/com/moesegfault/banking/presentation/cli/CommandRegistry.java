package com.moesegfault.banking.presentation.cli;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @brief 命令注册表（Command Registry），仅维护命令路径到 Handler 类型的映射；
 *        Command registry that only keeps command-path to handler-type mappings.
 *
 * @note 注册表不管理 handler 生命周期；
 *       The registry does not manage handler lifecycle.
 */
public final class CommandRegistry {

    /**
     * @brief 命令到处理器类型映射（Command-to-handler-type Map）；
     *        Mapping from normalized command path to handler type.
     */
    private final Map<String, Class<? extends CliCommandHandler>> commandMappings = new LinkedHashMap<>();

    /**
     * @brief 注册命令映射（Register Command Mapping）；
     *        Register one command-path to handler-type mapping.
     *
     * @param commandPath 命令路径（Command path）。
     * @param handlerType 处理器类型（Handler type）。
     */
    public void register(final String commandPath, final Class<? extends CliCommandHandler> handlerType) {
        final String normalizedPath = normalizeCommandPath(commandPath);
        final Class<? extends CliCommandHandler> normalizedHandlerType = Objects.requireNonNull(
                handlerType,
                "handlerType must not be null");

        final Class<? extends CliCommandHandler> existingType = commandMappings.get(normalizedPath);
        if (existingType != null && !existingType.equals(normalizedHandlerType)) {
            throw new IllegalStateException(
                    "Command path already registered with a different handler: " + normalizedPath);
        }

        commandMappings.putIfAbsent(normalizedPath, normalizedHandlerType);
    }

    /**
     * @brief 查找处理器类型（Find Handler Type）；
     *        Find handler type by command path.
     *
     * @param commandPath 命令路径（Command path）。
     * @return 处理器类型（Handler type），不存在则 empty。
     */
    public Optional<Class<? extends CliCommandHandler>> findHandlerType(final String commandPath) {
        final String normalizedPath = normalizeCommandPath(commandPath);
        return Optional.ofNullable(commandMappings.get(normalizedPath));
    }

    /**
     * @brief 获取所有已注册命令路径（Get Registered Command Paths）；
     *        Get all registered command paths.
     *
     * @return 命令路径集合（Command-path set）。
     */
    public Set<String> registeredCommandPaths() {
        return Collections.unmodifiableSet(commandMappings.keySet());
    }

    /**
     * @brief 获取命令映射快照（Get Command Mapping Snapshot）；
     *        Get an immutable snapshot of command mappings.
     *
     * @return 命令映射快照（Command mapping snapshot）。
     */
    public Map<String, Class<? extends CliCommandHandler>> mappingsSnapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(commandMappings));
    }

    /**
     * @brief 规范化命令路径（Normalize Command Path）；
     *        Normalize command path by trimming, collapsing spaces, and lowercasing.
     *
     * @param commandPath 命令路径（Command path）。
     * @return 规范化命令路径（Normalized command path）。
     */
    private static String normalizeCommandPath(final String commandPath) {
        final String trimmedPath = Objects.requireNonNull(commandPath, "commandPath must not be null").trim();
        if (trimmedPath.isEmpty()) {
            throw new IllegalArgumentException("commandPath must not be blank");
        }

        final String[] segments = trimmedPath.split("\\s+");
        final StringBuilder builder = new StringBuilder();
        for (int index = 0; index < segments.length; index++) {
            final String segment = segments[index].trim();
            if (segment.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(segment.toLowerCase(Locale.ROOT));
        }

        if (builder.length() == 0) {
            throw new IllegalArgumentException("commandPath must contain at least one non-blank segment");
        }
        return builder.toString();
    }
}
