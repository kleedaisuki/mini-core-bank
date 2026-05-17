package com.moesegfault.banking.presentation.cli.builtin;

import com.moesegfault.banking.presentation.cli.CommandRegistry;
import java.util.Objects;

/**
 * @brief 内建 CLI 命令注册器（Built-in CLI Command Registration），注册冒号命名空间命令；
 *        Built-in CLI command registration for colon-namespaced commands.
 */
public final class BuiltinCliCommandRegistration {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for registration utility.
     */
    private BuiltinCliCommandRegistration() {
    }

    /**
     * @brief 注册内建命令（Register Built-in Commands）；
     *        Register built-in commands.
     *
     * @param registry 命令注册表（Command registry）。
     */
    public static void register(final CommandRegistry registry) {
        final CommandRegistry normalizedRegistry = Objects.requireNonNull(registry, "registry must not be null");
        normalizedRegistry.register(BuiltinCliCommands.BASH, BashCliHandler.class);
        normalizedRegistry.register(BuiltinCliCommands.EXIT, ExitCliHandler.class);
        normalizedRegistry.register(BuiltinCliCommands.QUIT, ExitCliHandler.class);
    }
}
