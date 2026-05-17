package com.moesegfault.banking.presentation.cli;

import com.moesegfault.banking.presentation.cli.builtin.BuiltinExitRequested;
import com.moesegfault.banking.presentation.cli.style.CliStyle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Locale;
import java.util.Objects;

/**
 * @brief CLI 交互式 Shell（Interactive CLI Shell），在同一应用上下文中循环执行多条命令；
 *        Interactive CLI shell that executes multiple commands in one application context.
 */
public final class CliShell {

    /**
     * @brief 成功退出码（Success Exit Code）；
     *        Exit code for successful shell termination.
     */
    public static final int EXIT_SUCCESS = 0;

    /**
     * @brief 运行时错误退出码（Runtime Error Exit Code）；
     *        Exit code for shell runtime failure.
     */
    public static final int EXIT_RUNTIME_ERROR = 1;

    /**
     * @brief 默认提示符（Default Prompt）；
     *        Default prompt.
     */
    private static final String DEFAULT_PROMPT = "bank> ";

    /**
     * @brief CLI 应用对象（CLI Application）；
     *        CLI application.
     */
    private final CliApplication application;

    /**
     * @brief 输入读取器（Input Reader）；
     *        Input reader.
     */
    private final BufferedReader input;

    /**
     * @brief 标准输出流（Standard Output Stream）；
     *        Standard output stream.
     */
    private final PrintStream output;

    /**
     * @brief 标准错误流（Standard Error Stream）；
     *        Standard error stream.
     */
    private final PrintStream error;

    /**
     * @brief 命令提示符（Command Prompt）；
     *        Command prompt.
     */
    private final String prompt;

    /**
     * @brief 构造交互式 Shell（Construct Interactive Shell）；
     *        Construct interactive shell.
     *
     * @param application CLI 应用对象（CLI application）。
     * @param input       输入读取器（Input reader）。
     * @param output      标准输出流（Standard output stream）。
     * @param error       标准错误流（Standard error stream）。
     */
    public CliShell(
            final CliApplication application,
            final Reader input,
            final PrintStream output,
            final PrintStream error
    ) {
        this(application, input, output, error, DEFAULT_PROMPT);
    }

    /**
     * @brief 构造交互式 Shell（完整配置）（Construct Interactive Shell with Full Configuration）；
     *        Construct interactive shell with full configuration.
     *
     * @param application CLI 应用对象（CLI application）。
     * @param input       输入读取器（Input reader）。
     * @param output      标准输出流（Standard output stream）。
     * @param error       标准错误流（Standard error stream）。
     * @param prompt      命令提示符（Command prompt）。
     */
    public CliShell(
            final CliApplication application,
            final Reader input,
            final PrintStream output,
            final PrintStream error,
            final String prompt
    ) {
        this.application = Objects.requireNonNull(application, "application must not be null");
        this.input = new BufferedReader(Objects.requireNonNull(input, "input must not be null"));
        this.output = Objects.requireNonNull(output, "output must not be null");
        this.error = Objects.requireNonNull(error, "error must not be null");
        this.prompt = Objects.requireNonNull(prompt, "prompt must not be null");
    }

    /**
     * @brief 运行 Shell 循环（Run Shell Loop）；
     *        Run the shell loop until exit command or end-of-input.
     *
     * @return 退出码（Exit code）。
     */
    public int run() {
        output.println(CliStyle.title("Mini Core Bank CLI shell"));
        output.println(CliStyle.muted("Type help for command summaries, help <command> for details, :exit to quit."));
        try {
            String line = readLine();
            while (line != null) {
                final String commandLine = line.trim();
                if (!commandLine.isEmpty()) {
                    final boolean shouldContinue = handleLine(commandLine);
                    if (!shouldContinue) {
                        return EXIT_SUCCESS;
                    }
                }
                line = readLine();
            }
            return EXIT_SUCCESS;
        } catch (IOException exception) {
            printRuntimeError("Shell failed: " + exception.getMessage());
            return EXIT_RUNTIME_ERROR;
        }
    }

    /**
     * @brief 读取一行输入（Read One Input Line）；
     *        Read one input line after printing prompt.
     *
     * @return 输入行（Input line），EOF 时返回 null。
     * @throws IOException 当底层读取失败时抛出（Thrown when underlying read fails）。
     */
    private String readLine() throws IOException {
        output.print(CliStyle.prompt(prompt));
        output.flush();
        return input.readLine();
    }

    /**
     * @brief 处理一行命令（Handle One Command Line）；
     *        Handle one command line while keeping the shell alive after command failures.
     *
     * @param commandLine 命令行（Command line）。
     * @return true 表示继续 Shell 循环（true when shell loop should continue）。
     */
    private boolean handleLine(final String commandLine) {
        if (shouldPrintHelp(commandLine)) {
            printHelp(commandLine);
            return true;
        }
        try {
            application.execute(commandLine);
        } catch (BuiltinExitRequested exception) {
            return false;
        } catch (IllegalArgumentException exception) {
            printUsageError(commandLine, exception.getMessage());
        } catch (Exception exception) {
            printRuntimeError("CLI command failed: " + exception.getMessage());
        }
        return true;
    }

    /**
     * @brief 判断是否帮助命令（Check Help Command）；
     *        Check whether command requests help.
     *
     * @param commandLine 命令行（Command line）。
     * @return true 表示打印帮助（true when help should be printed）。
     */
    private static boolean shouldPrintHelp(final String commandLine) {
        final String normalized = commandLine.toLowerCase(Locale.ROOT);
        return "help".equals(normalized)
                || normalized.startsWith("help ")
                || "--help".equals(normalized)
                || "-h".equals(normalized);
    }

    /**
     * @brief 打印帮助（Print Help）；
     *        Print full help or command-specific help.
     *
     * @param commandLine 命令行（Command line）。
     */
    private void printHelp(final String commandLine) {
        if (!commandLine.toLowerCase(Locale.ROOT).startsWith("help ")) {
            application.printUsage(output);
            return;
        }
        final String commandPath = commandLine.substring("help ".length()).trim();
        if (commandPath.isEmpty()) {
            application.printUsage(output);
            return;
        }
        if (!application.printCommandHelp(output, commandPath)) {
            printUsageError(commandLine, "Unknown command for help: " + commandPath);
        }
    }

    /**
     * @brief 打印用法错误（Print Usage Error）；
     *        Print a user-actionable usage error with hints.
     *
     * @param commandLine 命令行（Command line）。
     * @param message     错误消息（Error message）。
     */
    private void printUsageError(final String commandLine, final String message) {
        error.println(CliStyle.error("Error") + ": " + message);
        error.println(CliStyle.hint("Hint") + ": run "
                + CliStyle.command("help")
                + " for command summaries, or "
                + CliStyle.command("help <command>")
                + " for required options and examples.");
        error.println(CliStyle.muted("Input: " + commandLine));
    }

    /**
     * @brief 打印运行时错误（Print Runtime Error）；
     *        Print a runtime error with a clear label.
     *
     * @param message 错误消息（Error message）。
     */
    private void printRuntimeError(final String message) {
        error.println(CliStyle.error("Runtime error") + ": " + message);
    }
}
