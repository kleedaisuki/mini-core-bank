package com.moesegfault.banking.presentation.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.presentation.gui.GuiToolkitType;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

/**
 * @brief CLI 主入口测试（CLI Main Entry Test），验证 one-shot 模式路由；
 *        Tests for CLI main entry one-shot routing.
 */
class MainTest {

    /**
     * @brief 验证 gui 命令默认启动 Swing；
     *        Verify gui command launches Swing by default.
     */
    @Test
    void shouldLaunchGuiWithDefaultSwingToolkit() {
        final RecordingGuiLauncher guiLauncher = new RecordingGuiLauncher();
        final ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        final ByteArrayOutputStream errorBytes = new ByteArrayOutputStream();

        final int exitCode = Main.run(
                new String[] {"gui"},
                new StringReader(""),
                new PrintStream(outputBytes, true, StandardCharsets.UTF_8),
                new PrintStream(errorBytes, true, StandardCharsets.UTF_8),
                guiLauncher);

        assertEquals(0, exitCode);
        assertEquals(GuiToolkitType.SWING, guiLauncher.launchedToolkit());
        assertEquals("", outputBytes.toString(StandardCharsets.UTF_8));
        assertEquals("", errorBytes.toString(StandardCharsets.UTF_8));
    }

    /**
     * @brief 验证 gui 命令支持显式 toolkit 参数；
     *        Verify gui command accepts explicit toolkit option.
     */
    @Test
    void shouldLaunchGuiWithExplicitToolkitOption() {
        final RecordingGuiLauncher guiLauncher = new RecordingGuiLauncher();

        final int exitCode = Main.run(
                new String[] {"gui", "--toolkit", "swing"},
                new StringReader(""),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
                guiLauncher);

        assertEquals(0, exitCode);
        assertEquals(GuiToolkitType.SWING, guiLauncher.launchedToolkit());
    }

    /**
     * @brief 验证 launch gui 别名可用；
     *        Verify launch gui alias is accepted.
     */
    @Test
    void shouldLaunchGuiWithLaunchAlias() {
        final RecordingGuiLauncher guiLauncher = new RecordingGuiLauncher();

        final int exitCode = Main.run(
                new String[] {"launch", "gui", "swing"},
                new StringReader(""),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
                guiLauncher);

        assertEquals(0, exitCode);
        assertEquals(GuiToolkitType.SWING, guiLauncher.launchedToolkit());
    }

    /**
     * @brief 验证重复 toolkit 参数会被拒绝；
     *        Verify duplicated toolkit arguments are rejected.
     */
    @Test
    void shouldRejectDuplicateGuiToolkit() {
        final RecordingGuiLauncher guiLauncher = new RecordingGuiLauncher();
        final ByteArrayOutputStream errorBytes = new ByteArrayOutputStream();

        final int exitCode = Main.run(
                new String[] {"gui", "swing", "--toolkit=swing"},
                new StringReader(""),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
                new PrintStream(errorBytes, true, StandardCharsets.UTF_8),
                guiLauncher);

        assertEquals(2, exitCode);
        assertNull(guiLauncher.launchedToolkit());
        assertTrue(errorBytes.toString(StandardCharsets.UTF_8).contains("GUI toolkit must be provided only once"));
    }

    /**
     * @brief 验证帮助文本包含 GUI 用法；
     *        Verify usage text includes GUI launch syntax.
     */
    @Test
    void shouldPrintGuiUsage() {
        final ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();

        final int exitCode = Main.run(
                new String[] {"--help"},
                new StringReader(""),
                new PrintStream(outputBytes, true, StandardCharsets.UTF_8),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
                toolkitType -> { });

        final String output = outputBytes.toString(StandardCharsets.UTF_8);
        assertEquals(0, exitCode);
        assertTrue(output.contains("java -jar mini-core-bank.jar gui [--toolkit swing]"));
        assertTrue(output.contains("GUI commands:"));
    }

    /**
     * @brief 记录型 GUI 启动器（Recording GUI Launcher）；
     *        Recording GUI launcher for routing verification.
     */
    private static final class RecordingGuiLauncher implements com.moesegfault.banking.presentation.cli.gui.GuiCliLauncher {

        /**
         * @brief 已启动技术栈记录（Launched Toolkit Record）；
         *        Recorded launched toolkit.
         */
        private final AtomicReference<GuiToolkitType> launchedToolkit = new AtomicReference<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public void launch(final GuiToolkitType toolkitType) {
            launchedToolkit.set(toolkitType);
        }

        /**
         * @brief 获取已启动技术栈（Get Launched Toolkit）；
         *        Get recorded launched toolkit.
         *
         * @return 已启动技术栈（Launched toolkit）。
         */
        GuiToolkitType launchedToolkit() {
            return launchedToolkit.get();
        }
    }
}
