package com.moesegfault.banking.presentation.cli;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief CLI 应用对象（CLI Application），复用解析器、注册表与分发器执行命令；
 *        CLI application object that reuses parser, registry, and dispatcher to execute commands.
 */
public final class CliApplication {

    /**
     * @brief 命令解析器（Command Parser）；
     *        Command parser.
     */
    private final CommandParser commandParser;

    /**
     * @brief 命令注册表（Command Registry）；
     *        Command registry.
     */
    private final CommandRegistry commandRegistry;

    /**
     * @brief 命令分发器（Command Dispatcher）；
     *        Command dispatcher.
     */
    private final CommandDispatcher commandDispatcher;

    /**
     * @brief 构造 CLI 应用（Construct CLI Application）；
     *        Construct CLI application.
     *
     * @param commandParser     命令解析器（Command parser）。
     * @param commandRegistry   命令注册表（Command registry）。
     * @param commandDispatcher 命令分发器（Command dispatcher）。
     */
    public CliApplication(
            final CommandParser commandParser,
            final CommandRegistry commandRegistry,
            final CommandDispatcher commandDispatcher
    ) {
        this.commandParser = Objects.requireNonNull(commandParser, "commandParser must not be null");
        this.commandRegistry = Objects.requireNonNull(commandRegistry, "commandRegistry must not be null");
        this.commandDispatcher = Objects.requireNonNull(commandDispatcher, "commandDispatcher must not be null");
    }

    /**
     * @brief 执行一行命令（Execute One Command Line）；
     *        Execute one command line.
     *
     * @param rawInput 原始命令输入（Raw command input）。
     */
    public void execute(final String rawInput) {
        commandDispatcher.dispatch(commandParser.parse(rawInput));
    }

    /**
     * @brief 打印用法（Print Usage）；
     *        Print CLI usage and registered commands.
     *
     * @param output 输出流（Output stream）。
     */
    public void printUsage(final PrintStream output) {
        final PrintStream normalizedOutput = Objects.requireNonNull(output, "output must not be null");
        normalizedOutput.println("Usage: java -jar mini-core-bank.jar <command> [--option value]");
        normalizedOutput.println("       java -jar mini-core-bank.jar shell");
        normalizedOutput.println("       help <command>");
        normalizedOutput.println();
        normalizedOutput.println("General:");
        normalizedOutput.println("  Use single quotes around values that contain spaces.");
        normalizedOutput.println("  Option aliases with underscores are usually accepted, for example --customer_id.");
        normalizedOutput.println();
        normalizedOutput.println("Commands:");
        commandRegistry.registeredCommandPaths().forEach(commandPath -> {
            final CliHelpCatalog.CommandHelp commandHelp = CliHelpCatalog.find(commandPath)
                    .orElseGet(() -> new CliHelpCatalog.CommandHelp(
                            commandPath,
                            "No detailed help has been documented yet.",
                            List.of(),
                            List.of(),
                            commandPath));
            printCommandHelp(normalizedOutput, commandHelp);
        });
        normalizedOutput.println();
        normalizedOutput.println("Shell commands:");
        normalizedOutput.println("  help                 Show this detailed command reference.");
        normalizedOutput.println("  help <command>       Show help for one command, for example: help customer register.");
        normalizedOutput.println("  exit | quit          Leave shell mode and close runtime resources.");
    }

    /**
     * @brief 打印单个命令帮助（Print Single Command Help）；
     *        Print help for one command path.
     *
     * @param output      输出流（Output stream）。
     * @param commandPath 命令路径（Command path）。
     * @return true 表示找到命令帮助（true when command help was found）。
     */
    public boolean printCommandHelp(final PrintStream output, final String commandPath) {
        final PrintStream normalizedOutput = Objects.requireNonNull(output, "output must not be null");
        final String normalizedCommandPath = Objects.requireNonNull(commandPath, "commandPath must not be null").trim();
        final Optional<CliHelpCatalog.CommandHelp> commandHelp = CliHelpCatalog.find(normalizedCommandPath);
        commandHelp.ifPresent(help -> printCommandHelp(normalizedOutput, help));
        return commandHelp.isPresent();
    }

    /**
     * @brief 打印命令帮助条目（Print Command Help Entry）；
     *        Print one command help entry.
     *
     * @param output      输出流（Output stream）。
     * @param commandHelp 命令帮助条目（Command help entry）。
     */
    private static void printCommandHelp(
            final PrintStream output,
            final CliHelpCatalog.CommandHelp commandHelp
    ) {
        output.println("  " + commandHelp.commandPath());
        output.println("    " + commandHelp.summary());
        printOptionSection(output, "Required", commandHelp.requiredOptions());
        printOptionSection(output, "Optional", commandHelp.optionalOptions());
        output.println("    Example: " + commandHelp.example());
    }

    /**
     * @brief 打印参数段落（Print Option Section）；
     *        Print one option section when it has entries.
     *
     * @param output  输出流（Output stream）。
     * @param label   段落标签（Section label）。
     * @param options 参数列表（Option list）。
     */
    private static void printOptionSection(
            final PrintStream output,
            final String label,
            final List<String> options
    ) {
        if (options.isEmpty()) {
            return;
        }
        output.println("    " + label + ": " + String.join(", ", options));
    }

    /**
     * @brief 获取已注册命令路径（Get Registered Command Paths）；
     *        Get registered command paths.
     *
     * @return 已注册命令路径列表（Registered command path list）。
     */
    public List<String> registeredCommandPaths() {
        return List.copyOf(commandRegistry.registeredCommandPaths());
    }
}
