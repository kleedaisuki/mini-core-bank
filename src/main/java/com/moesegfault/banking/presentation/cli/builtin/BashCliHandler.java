package com.moesegfault.banking.presentation.cli.builtin;

import com.moesegfault.banking.presentation.cli.CliCommandHandler;
import com.moesegfault.banking.presentation.cli.ParsedCommand;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Objects;

/**
 * @brief 内建 Bash CLI 处理器（Built-in Bash CLI Handler），通过 `bash -lc` 执行宿主机命令；
 *        Built-in bash CLI handler that executes host commands through `bash -lc`.
 *
 * @note 该命令属于内建命名空间（Built-in namespace），命令路径固定为 `:bash`。
 *       This command belongs to the built-in namespace and uses the fixed `:bash` path.
 */
public final class BashCliHandler implements CliCommandHandler {

    /**
     * @brief Bash 可执行文件名（Bash Executable Name）；
     *        Bash executable name resolved from the process environment.
     */
    private static final String BASH_EXECUTABLE = "bash";

    /**
     * @brief Bash 登录命令参数（Bash Login Command Option）；
     *        Bash option that executes a command string.
     */
    private static final String BASH_COMMAND_OPTION = "-lc";

    /**
     * @brief 输出流（Output Stream）；
     *        Output stream receiving merged bash stdout and stderr.
     */
    private final PrintStream output;

    /**
     * @brief 构造处理器并默认输出到标准输出（Construct Handler with Standard Output）；
     *        Construct handler using standard output stream.
     */
    public BashCliHandler() {
        this(System.out);
    }

    /**
     * @brief 构造处理器（Construct Handler）；
     *        Construct handler with explicit output stream.
     *
     * @param output 输出流（Output stream）。
     */
    public BashCliHandler(final PrintStream output) {
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    /**
     * @brief 处理 `:bash` 命令（Handle `:bash` Command）；
     *        Handle the `:bash` command.
     *
     * @param command 已解析命令（Parsed command）。
     */
    @Override
    public void handle(final ParsedCommand command) {
        final ParsedCommand normalizedCommand = Objects.requireNonNull(command, "command must not be null");
        runScript(scriptFromRawInput(normalizedCommand.rawInput()));
    }

    /**
     * @brief 执行 Bash 脚本文本（Run Bash Script Text）；
     *        Execute one bash script text through `bash -lc`.
     *
     * @param script Bash 脚本文本（Bash script text）。
     */
    public void runScript(final String script) {
        final String normalizedScript = Objects.requireNonNull(script, "script must not be null").trim();
        if (normalizedScript.isEmpty()) {
            throw new IllegalArgumentException("Missing bash command. Usage: :bash <command>");
        }

        final Process process = startProcess(normalizedScript);
        copyProcessOutput(process);
        waitForSuccess(process);
    }

    /**
     * @brief 从原始输入提取脚本文本（Extract Script Text from Raw Input）；
     *        Extract bash script text from raw CLI input.
     *
     * @param rawInput 原始 CLI 输入（Raw CLI input）。
     * @return Bash 脚本文本（Bash script text）。
     */
    public static String scriptFromRawInput(final String rawInput) {
        final String normalizedInput = Objects.requireNonNull(rawInput, "rawInput must not be null").trim();
        if (!startsWithCommandPath(normalizedInput)) {
            throw new IllegalArgumentException("Expected " + BuiltinCliCommands.BASH + " command");
        }
        if (normalizedInput.length() == BuiltinCliCommands.BASH.length()) {
            return "";
        }
        return normalizedInput.substring(BuiltinCliCommands.BASH.length()).trim();
    }

    /**
     * @brief 判断输入是否以命令路径开头（Check Input Starts with Command Path）；
     *        Check whether raw input starts with the built-in command path.
     *
     * @param input 原始输入（Raw input）。
     * @return true 表示以命令路径开头（true when input starts with command path）。
     */
    private static boolean startsWithCommandPath(final String input) {
        if (!input.regionMatches(true, 0, BuiltinCliCommands.BASH, 0, BuiltinCliCommands.BASH.length())) {
            return false;
        }
        return input.length() == BuiltinCliCommands.BASH.length()
                || Character.isWhitespace(input.charAt(BuiltinCliCommands.BASH.length()));
    }

    /**
     * @brief 启动 Bash 进程（Start Bash Process）；
     *        Start the bash process.
     *
     * @param script Bash 脚本文本（Bash script text）。
     * @return Bash 进程（Bash process）。
     */
    private static Process startProcess(final String script) {
        try {
            return new ProcessBuilder(BASH_EXECUTABLE, BASH_COMMAND_OPTION, script)
                    .redirectErrorStream(true)
                    .start();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to start bash command", exception);
        }
    }

    /**
     * @brief 复制进程输出（Copy Process Output）；
     *        Copy process output to the configured stream.
     *
     * @param process Bash 进程（Bash process）。
     */
    private void copyProcessOutput(final Process process) {
        try (InputStream processOutput = process.getInputStream()) {
            processOutput.transferTo(output);
            output.flush();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read bash command output", exception);
        }
    }

    /**
     * @brief 等待进程成功退出（Wait for Successful Process Exit）；
     *        Wait for process completion and reject non-zero exit status.
     *
     * @param process Bash 进程（Bash process）。
     */
    private static void waitForSuccess(final Process process) {
        try {
            final int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException(String.format(
                        Locale.ROOT,
                        "bash command exited with status %d",
                        exitCode));
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for bash command", exception);
        }
    }
}
