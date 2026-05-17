package com.moesegfault.banking.presentation.cli;

import com.moesegfault.banking.presentation.cli.style.CliStyle;
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
        normalizedOutput.println(CliStyle.title("Mini Core Bank CLI"));
        normalizedOutput.println(CliStyle.muted("Command-line tools for the mini core banking runtime."));
        normalizedOutput.println();
        normalizedOutput.println(CliStyle.section("Usage"));
        normalizedOutput.println("  " + CliStyle.command("java -jar mini-core-bank.jar <command> [--option value]"));
        normalizedOutput.println("  " + CliStyle.command("java -jar mini-core-bank.jar shell"));
        normalizedOutput.println("  " + CliStyle.command(":bash <command>"));
        normalizedOutput.println("  " + CliStyle.command(":exit | :quit"));
        normalizedOutput.println("  " + CliStyle.command("help <command>"));
        normalizedOutput.println();
        normalizedOutput.println(CliStyle.section("General"));
        normalizedOutput.println("  " + CliStyle.hint("Tip") + ": use single quotes around values that contain spaces.");
        normalizedOutput.println("  " + CliStyle.hint("Tip") + ": option aliases with underscores are usually accepted, for example "
                + CliStyle.option("--customer_id") + ".");
        normalizedOutput.println("  " + CliStyle.hint("Tip") + ": run "
                + CliStyle.command("help <command>")
                + " to see required options, optional options, and an example.");
        normalizedOutput.println("  " + CliStyle.hint("Tip") + ": in shell mode, simple options can be supplied positionally "
                + "in the order shown by command help.");
        normalizedOutput.println();
        normalizedOutput.println(CliStyle.section("Commands"));
        final int commandWidth = commandRegistry.registeredCommandPaths().stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);
        commandRegistry.registeredCommandPaths().forEach(commandPath -> {
            final CliHelpCatalog.CommandHelp commandHelp = CliHelpCatalog.find(commandPath)
                    .orElseGet(() -> new CliHelpCatalog.CommandHelp(
                            commandPath,
                            "No detailed help has been documented yet.",
                            List.of(),
                            List.of(),
                            commandPath));
            printCommandSummary(normalizedOutput, commandHelp, commandWidth);
        });
        normalizedOutput.println();
        normalizedOutput.println(CliStyle.section("Shell Commands"));
        normalizedOutput.println("  " + CliStyle.command("help") + paddedGap("help", 20)
                + "Show command summaries.");
        normalizedOutput.println("  " + CliStyle.command("help <command>") + paddedGap("help <command>", 20)
                + "Show detailed help, for example: " + CliStyle.command("help customer register") + ".");
        normalizedOutput.println("  " + CliStyle.command(":exit | :quit") + paddedGap(":exit | :quit", 20)
                + "Leave shell mode and close runtime resources.");
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
     * @brief 打印命令摘要条目（Print Command Summary Entry）；
     *        Print one command summary entry for the global help listing.
     *
     * @param output      输出流（Output stream）。
     * @param commandHelp 命令帮助条目（Command help entry）。
     */
    private static void printCommandSummary(
            final PrintStream output,
            final CliHelpCatalog.CommandHelp commandHelp,
            final int commandWidth
    ) {
        output.println("  "
                + CliStyle.command(commandHelp.commandPath())
                + paddedGap(commandHelp.commandPath(), commandWidth + 4)
                + commandHelp.summary());
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
        output.println(CliStyle.title(commandHelp.commandPath()));
        output.println("  " + commandHelp.summary());
        printPositionalSection(output, commandHelp);
        printOptionSection(output, "Required", commandHelp.requiredOptions());
        printOptionSection(output, "Optional", commandHelp.optionalOptions());
        output.println("  " + CliStyle.label("Example"));
        output.println("    " + CliStyle.command(commandHelp.example()));
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
        output.println("  " + CliStyle.label(label));
        options.forEach(option -> output.println("    " + CliStyle.option(option)));
    }

    /**
     * @brief 打印位置参数段落（Print Positional Argument Section）；
     *        Print positional argument order for commands that support it.
     *
     * @param output      输出流（Output stream）。
     * @param commandHelp 命令帮助条目（Command help entry）。
     */
    private static void printPositionalSection(
            final PrintStream output,
            final CliHelpCatalog.CommandHelp commandHelp
    ) {
        final List<String> positionalOptionNames = commandHelp.positionalOptionNames();
        if (positionalOptionNames.isEmpty()) {
            return;
        }

        output.println("  " + CliStyle.label("Positional"));
        output.println("    " + positionalOptionNames.stream()
                .map(optionName -> CliStyle.option("<" + optionName + ">"))
                .reduce((left, right) -> left + " " + right)
                .orElse(""));
    }

    /**
     * @brief 计算填充间距（Calculate Padding Gap）；
     *        Calculate a fixed-width visual gap for aligned help text.
     *
     * @param text  文本（Text）。
     * @param width 目标宽度（Target width）。
     * @return 空格字符串（Space string）。
     */
    private static String paddedGap(final String text, final int width) {
        final int spaces = Math.max(2, width - text.length());
        return " ".repeat(spaces);
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
