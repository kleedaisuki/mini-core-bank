package com.moesegfault.banking.presentation.cli;

import com.moesegfault.banking.presentation.cli.bootstrap.CliBootstrap;
import com.moesegfault.banking.presentation.cli.bootstrap.CliRuntime;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * @brief CLI 主入口（CLI Main Entry Point），选择一次性命令模式或交互式 Shell 模式；
 *        CLI main entry point that selects one-shot command mode or interactive shell mode.
 */
public final class Main {

    /**
     * @brief 成功退出码（Success Exit Code）；
     *        Exit code for successful execution.
     */
    private static final int EXIT_SUCCESS = 0;

    /**
     * @brief 用法错误退出码（Usage Error Exit Code）；
     *        Exit code for invalid CLI usage.
     */
    private static final int EXIT_USAGE_ERROR = 2;

    /**
     * @brief 运行时错误退出码（Runtime Error Exit Code）；
     *        Exit code for runtime failures.
     */
    private static final int EXIT_RUNTIME_ERROR = 1;

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility entry point.
     */
    private Main() {
    }

    /**
     * @brief Java 进程入口（Java Process Entry Point）；
     *        Java process entry point.
     *
     * @param args 命令行参数（Command-line arguments）。
     */
    public static void main(final String[] args) {
        final int exitCode = run(
                args,
                new InputStreamReader(System.in, StandardCharsets.UTF_8),
                System.out,
                System.err);
        if (exitCode != EXIT_SUCCESS) {
            System.exit(exitCode);
        }
    }

    /**
     * @brief 执行 CLI（Run CLI）；
     *        Run CLI in one-shot or shell mode.
     *
     * @param args   命令行参数（Command-line arguments）。
     * @param output 标准输出流（Standard output stream）。
     * @param error  标准错误流（Standard error stream）。
     * @return 退出码（Exit code）。
     */
    public static int run(final String[] args, final PrintStream output, final PrintStream error) {
        return run(args, new InputStreamReader(System.in, StandardCharsets.UTF_8), output, error);
    }

    /**
     * @brief 执行 CLI（可注入输入）（Run CLI with Injectable Input）；
     *        Run CLI with injectable input for shell mode.
     *
     * @param args   命令行参数（Command-line arguments）。
     * @param input  Shell 输入读取器（Shell input reader）。
     * @param output 标准输出流（Standard output stream）。
     * @param error  标准错误流（Standard error stream）。
     * @return 退出码（Exit code）。
     */
    public static int run(
            final String[] args,
            final Reader input,
            final PrintStream output,
            final PrintStream error
    ) {
        final String[] normalizedArgs = Objects.requireNonNull(args, "args must not be null").clone();
        final Reader normalizedInput = Objects.requireNonNull(input, "input must not be null");
        final PrintStream normalizedOutput = Objects.requireNonNull(output, "output must not be null");
        final PrintStream normalizedError = Objects.requireNonNull(error, "error must not be null");

        if (normalizedArgs.length == 0 || isHelpRequest(normalizedArgs)) {
            printUsage(normalizedOutput);
            return normalizedArgs.length == 0 ? EXIT_USAGE_ERROR : EXIT_SUCCESS;
        }
        if (isCommandHelpRequest(normalizedArgs)) {
            return printCommandHelp(normalizedArgs, normalizedOutput, normalizedError);
        }

        try (CliRuntime runtime = new CliBootstrap().bootstrap()) {
            if (isShellRequest(normalizedArgs)) {
                return new CliShell(runtime.application(), normalizedInput, normalizedOutput, normalizedError).run();
            }
            runtime.application().execute(toRawInput(normalizedArgs));
            return EXIT_SUCCESS;
        } catch (IllegalArgumentException exception) {
            normalizedError.println(exception.getMessage());
            return EXIT_USAGE_ERROR;
        } catch (Exception exception) {
            normalizedError.println("CLI command failed: " + exception.getMessage());
            return EXIT_RUNTIME_ERROR;
        }
    }

    /**
     * @brief 判断是否帮助请求（Check Help Request）；
     *        Check whether arguments request usage help.
     *
     * @param args 命令行参数（Command-line arguments）。
     * @return true 表示帮助请求（true when help is requested）。
     */
    private static boolean isHelpRequest(final String[] args) {
        return args.length == 1 && ("--help".equals(args[0]) || "-h".equals(args[0]) || "help".equals(args[0]));
    }

    /**
     * @brief 判断是否单命令帮助请求（Check Command-specific Help Request）；
     *        Check whether arguments request help for one command.
     *
     * @param args 命令行参数（Command-line arguments）。
     * @return true 表示单命令帮助请求（true when command-specific help is requested）。
     */
    private static boolean isCommandHelpRequest(final String[] args) {
        return args.length > 1 && "help".equals(args[0]);
    }

    /**
     * @brief 判断是否 Shell 请求（Check Shell Request）；
     *        Check whether arguments request interactive shell mode.
     *
     * @param args 命令行参数（Command-line arguments）。
     * @return true 表示 Shell 模式（true when shell mode is requested）。
     */
    private static boolean isShellRequest(final String[] args) {
        return args.length == 1 && ("shell".equals(args[0]) || "repl".equals(args[0]) || "--shell".equals(args[0]));
    }

    /**
     * @brief 打印用法（Print Usage）；
     *        Print CLI usage without bootstrapping database resources.
     *
     * @param output 输出流（Output stream）。
     */
    private static void printUsage(final PrintStream output) {
        createHelpApplication().printUsage(output);
    }

    /**
     * @brief 打印单命令帮助（Print Command-specific Help）；
     *        Print help for one command without bootstrapping database resources.
     *
     * @param args   命令行参数（Command-line arguments）。
     * @param output 标准输出流（Standard output stream）。
     * @param error  标准错误流（Standard error stream）。
     * @return 退出码（Exit code）。
     */
    private static int printCommandHelp(
            final String[] args,
            final PrintStream output,
            final PrintStream error
    ) {
        final String commandPath = toRawInput(Arrays.copyOfRange(args, 1, args.length));
        if (createHelpApplication().printCommandHelp(output, commandPath)) {
            return EXIT_SUCCESS;
        }
        error.println("Unknown command for help: " + commandPath);
        return EXIT_USAGE_ERROR;
    }

    /**
     * @brief 创建帮助专用 CLI 应用（Create Help-only CLI Application）；
     *        Create CLI application for help rendering without executable handlers.
     *
     * @return 帮助专用 CLI 应用（Help-only CLI application）。
     */
    private static CliApplication createHelpApplication() {
        final CommandRegistry commandRegistry = CliBootstrap.createCommandRegistry();
        return new CliApplication(
                new CommandParser(),
                commandRegistry,
                new CommandDispatcher(commandRegistry, handlerType -> null));
    }

    /**
     * @brief 转换 Java 参数为解析器输入（Convert Java Args to Parser Input）；
     *        Convert Java argument array into command-parser input text.
     *
     * @param args 命令行参数（Command-line arguments）。
     * @return 命令解析器输入文本（Command-parser input text）。
     */
    private static String toRawInput(final String[] args) {
        if (args.length == 1) {
            return args[0];
        }
        return Arrays.stream(args)
                .map(Main::quoteIfNeeded)
                .reduce((left, right) -> left + " " + right)
                .orElse("");
    }

    /**
     * @brief 按需引用参数（Quote Argument if Needed）；
     *        Quote one argument when it contains parser-significant whitespace.
     *
     * @param argument 原始参数（Raw argument）。
     * @return 可安全拼接的参数文本（Safely joinable argument text）。
     */
    private static String quoteIfNeeded(final String argument) {
        final String normalizedArgument = Objects.requireNonNull(argument, "argument must not be null");
        if (normalizedArgument.chars().noneMatch(Character::isWhitespace)) {
            return normalizedArgument;
        }
        return "'" + normalizedArgument.replace("'", "\\'") + "'";
    }
}
