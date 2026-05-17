package com.moesegfault.banking.presentation.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.moesegfault.banking.presentation.cli.builtin.BashCliHandler;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * @brief Bash CLI 处理器单元测试（Bash CLI Handler Unit Test），验证内建 `:bash` 命令执行行为；
 *        Unit tests for the built-in `:bash` command execution behavior.
 */
class BashCliHandlerTest {

    /**
     * @brief 验证可通过 Bash 执行命令并捕获输出；
     *        Verify command execution through bash and captured output.
     */
    @Test
    void shouldExecuteBashCommand() {
        final ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        final BashCliHandler handler = new BashCliHandler(new PrintStream(
                outputBytes,
                true,
                StandardCharsets.UTF_8));

        handler.handle(new ParsedCommand(":bash printf codex", List.of(":bash"), Map.of()));

        assertEquals("codex", outputBytes.toString(StandardCharsets.UTF_8));
    }

    /**
     * @brief 验证缺少 Bash 命令时会拒绝；
     *        Verify missing bash command is rejected.
     */
    @Test
    void shouldRejectMissingBashCommand() {
        final BashCliHandler handler = new BashCliHandler(new PrintStream(
                new ByteArrayOutputStream(),
                true,
                StandardCharsets.UTF_8));

        assertThrows(IllegalArgumentException.class, () -> handler.runScript(" "));
    }
}
