package com.moesegfault.banking.presentation.cli;

/**
 * @brief CLI 命令处理器接口（CLI Command Handler Interface），定义命令执行入口；
 *        CLI command handler interface defining one entrypoint for parsed command execution.
 */
@FunctionalInterface
public interface CliCommandHandler {

    /**
     * @brief 处理已解析命令（Handle Parsed Command）；
     *        Handle one parsed command.
     *
     * @param command 已解析命令（Parsed command）。
     */
    void handle(ParsedCommand command);
}
