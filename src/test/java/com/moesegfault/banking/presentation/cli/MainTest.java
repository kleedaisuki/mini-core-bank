package com.moesegfault.banking.presentation.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/**
 * @brief CLI 主入口测试（CLI Main Entry Test），验证 one-shot 模式路由；
 *        Tests for CLI main entry one-shot routing.
 */
class MainTest {

    /**
     * @brief 验证已移除的 GUI 命令会被快速拒绝（Verify Removed GUI Command Is Rejected Quickly）；
     *        Verify removed GUI command is rejected before runtime bootstrap.
     */
    @Test
    void shouldRejectRemovedGuiCommand() {
        final ByteArrayOutputStream errorBytes = new ByteArrayOutputStream();

        final int exitCode = Main.run(
                new String[] {"gui"},
                new StringReader(""),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
                new PrintStream(errorBytes, true, StandardCharsets.UTF_8));

        assertEquals(2, exitCode);
        assertTrue(errorBytes.toString(StandardCharsets.UTF_8).contains("GUI support has been removed"));
    }

    /**
     * @brief 验证帮助文本不再包含 GUI 用法（Verify Usage Text Excludes GUI Syntax）；
     *        Verify usage text no longer includes GUI launch syntax.
     */
    @Test
    void shouldPrintCliOnlyUsage() {
        final ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();

        final int exitCode = Main.run(
                new String[] {"--help"},
                new StringReader(""),
                new PrintStream(outputBytes, true, StandardCharsets.UTF_8),
                new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));

        final String output = outputBytes.toString(StandardCharsets.UTF_8);
        assertEquals(0, exitCode);
        assertTrue(output.contains("java -jar mini-core-bank.jar shell"));
        assertFalse(output.contains("java -jar mini-core-bank.jar gui"));
        assertFalse(output.contains("GUI commands:"));
    }
}
