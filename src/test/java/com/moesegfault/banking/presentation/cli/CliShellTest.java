package com.moesegfault.banking.presentation.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.presentation.cli.builtin.BuiltinCliCommands;
import com.moesegfault.banking.presentation.cli.builtin.ExitCliHandler;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * @brief CLI Shell 单元测试（CLI Shell Unit Test），验证交互循环复用同一 CLI 应用执行多条命令；
 *        Unit tests for CLI shell loop reusing one CLI application across multiple commands.
 */
class CliShellTest {

    /**
     * @brief 验证同一 Shell 会连续执行多条命令后退出；
     *        Verify one shell executes multiple commands before exit.
     */
    @Test
    void shouldExecuteMultipleCommandsBeforeExit() {
        final CommandRegistry registry = new CommandRegistry();
        registry.register("ping", CountingHandler.class);
        registry.register(BuiltinCliCommands.EXIT, ExitCliHandler.class);
        final CountingHandler handler = new CountingHandler();
        final ExitCliHandler exitHandler = new ExitCliHandler();
        final CliApplication application = new CliApplication(
                new CommandParser(),
                registry,
                new CommandDispatcher(registry, handlerType -> Map.of(
                        CountingHandler.class, handler,
                        ExitCliHandler.class, exitHandler).get(handlerType)));
        final ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        final ByteArrayOutputStream errorBytes = new ByteArrayOutputStream();
        final CliShell shell = new CliShell(
                application,
                new StringReader("ping\nping\n:exit\n"),
                new PrintStream(outputBytes, true, StandardCharsets.UTF_8),
                new PrintStream(errorBytes, true, StandardCharsets.UTF_8),
                "test> ");

        final int exitCode = shell.run();

        assertEquals(CliShell.EXIT_SUCCESS, exitCode);
        assertEquals(2, handler.invocationCount());
        assertTrue(outputBytes.toString(StandardCharsets.UTF_8).contains("Mini Core Bank CLI shell"));
        assertEquals("", errorBytes.toString(StandardCharsets.UTF_8));
    }

    /**
     * @brief 验证全局 help 仅打印命令摘要；
     *        Verify global help prints command summaries only.
     */
    @Test
    void shouldPrintSummaryOnlyGlobalHelp() {
        final CommandRegistry registry = new CommandRegistry();
        registry.register("customer register", CountingHandler.class);
        registry.register(BuiltinCliCommands.EXIT, ExitCliHandler.class);
        final CliApplication application = new CliApplication(
                new CommandParser(),
                registry,
                new CommandDispatcher(registry, handlerType -> {
                    if (ExitCliHandler.class.equals(handlerType)) {
                        return new ExitCliHandler();
                    }
                    return new CountingHandler();
                }));
        final ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        final ByteArrayOutputStream errorBytes = new ByteArrayOutputStream();
        final CliShell shell = new CliShell(
                application,
                new StringReader("help\n:exit\n"),
                new PrintStream(outputBytes, true, StandardCharsets.UTF_8),
                new PrintStream(errorBytes, true, StandardCharsets.UTF_8),
                "test> ");

        final int exitCode = shell.run();
        final String output = outputBytes.toString(StandardCharsets.UTF_8);

        assertEquals(CliShell.EXIT_SUCCESS, exitCode);
        assertTrue(output.contains("\u001B["));
        assertTrue(output.contains("Register a new customer profile"));
        assertFalse(output.contains("--id-type <type>"));
        assertFalse(output.contains("Example: customer register"));
        assertEquals("", errorBytes.toString(StandardCharsets.UTF_8));
    }

    /**
     * @brief 验证单命令 help 会打印详细命令说明；
     *        Verify command-specific help prints detailed command descriptions.
     */
    @Test
    void shouldPrintDetailedCommandHelp() {
        final CommandRegistry registry = new CommandRegistry();
        registry.register("customer register", CountingHandler.class);
        registry.register(BuiltinCliCommands.EXIT, ExitCliHandler.class);
        final CliApplication application = new CliApplication(
                new CommandParser(),
                registry,
                new CommandDispatcher(registry, handlerType -> {
                    if (ExitCliHandler.class.equals(handlerType)) {
                        return new ExitCliHandler();
                    }
                    return new CountingHandler();
                }));
        final ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        final ByteArrayOutputStream errorBytes = new ByteArrayOutputStream();
        final CliShell shell = new CliShell(
                application,
                new StringReader("help customer register\n:exit\n"),
                new PrintStream(outputBytes, true, StandardCharsets.UTF_8),
                new PrintStream(errorBytes, true, StandardCharsets.UTF_8),
                "test> ");

        final int exitCode = shell.run();
        final String output = outputBytes.toString(StandardCharsets.UTF_8);

        assertEquals(CliShell.EXIT_SUCCESS, exitCode);
        assertTrue(output.contains("Register a new customer profile"));
        assertTrue(output.contains("--id-type <type>"));
        assertTrue(output.contains("Example"));
        assertTrue(output.contains("customer register --id-type"));
        assertEquals("", errorBytes.toString(StandardCharsets.UTF_8));
    }

    /**
     * @brief 计数型 Handler（Counting Handler）；
     *        Counting handler used to verify shell command invocation.
     */
    private static final class CountingHandler implements CliCommandHandler {

        /**
         * @brief 调用计数器（Invocation Counter）；
         *        Invocation counter.
         */
        private final AtomicInteger invocationCounter = new AtomicInteger();

        /**
         * @brief 处理命令并计数（Handle Command and Count）；
         *        Handle command and increment invocation count.
         *
         * @param command 已解析命令（Parsed command）。
         */
        @Override
        public void handle(final ParsedCommand command) {
            invocationCounter.incrementAndGet();
        }

        /**
         * @brief 获取调用次数（Get Invocation Count）；
         *        Get invocation count.
         *
         * @return 调用次数（Invocation count）。
         */
        public int invocationCount() {
            return invocationCounter.get();
        }
    }
}
