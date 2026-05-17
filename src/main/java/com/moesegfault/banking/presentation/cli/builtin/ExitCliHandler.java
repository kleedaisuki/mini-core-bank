package com.moesegfault.banking.presentation.cli.builtin;

import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.util.Objects;

/**
 * @brief 内建退出 CLI 处理器（Built-in Exit CLI Handler），处理 `:exit` 与 `:quit`；
 *        Built-in exit CLI handler for `:exit` and `:quit`.
 */
public final class ExitCliHandler implements CliCommandHandler {

    /**
     * @brief 处理退出命令（Handle Exit Command）；
     *        Handle the exit command by raising a normal exit signal.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        throw new BuiltinExitRequested();
    }
}
