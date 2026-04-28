package com.moesegfault.banking.presentation.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * @brief 命令解析器单元测试（Command Parser Unit Test），验证 CLI 输入解析行为；
 *        Unit tests for CLI command parser behavior.
 */
class CommandParserTest {

    /**
     * @brief 验证可解析命令路径与 `--k v` 参数；
     *        Verify command path and `--k v` option parsing.
     */
    @Test
    void shouldParseCommandPathAndSpaceSeparatedOptions() {
        final CommandParser parser = new CommandParser();

        final ParsedCommand parsed = parser.parse("customer register --phone +8613800000000 --country CN");

        assertEquals("customer register", parsed.commandPath());
        assertEquals("+8613800000000", parsed.requiredOption("phone"));
        assertEquals("CN", parsed.requiredOption("country"));
    }

    /**
     * @brief 验证可解析引号与 `--k=v` 参数；
     *        Verify quoted tokens and `--k=v` option parsing.
     */
    @Test
    void shouldParseQuotedTokensAndEqualsStyleOption() {
        final CommandParser parser = new CommandParser();

        final ParsedCommand parsed = parser.parse("customer register --name=\"Alice Zhang\" --city 'Shang Hai'");

        assertEquals("customer register", parsed.commandPath());
        assertEquals("Alice Zhang", parsed.requiredOption("name"));
        assertEquals("Shang Hai", parsed.requiredOption("city"));
    }

    /**
     * @brief 验证无值参数按布尔 flag 处理；
     *        Verify no-value option is treated as boolean flag.
     */
    @Test
    void shouldTreatNoValueOptionAsBooleanFlag() {
        final CommandParser parser = new CommandParser();

        final ParsedCommand parsed = parser.parse("ledger entries --desc");

        assertEquals("true", parsed.requiredOption("desc"));
    }

    /**
     * @brief 验证空输入抛出异常；
     *        Verify blank input throws exception.
     */
    @Test
    void shouldRejectBlankInput() {
        final CommandParser parser = new CommandParser();

        assertThrows(IllegalArgumentException.class, () -> parser.parse("   "));
    }

    /**
     * @brief 验证重复参数抛出异常；
     *        Verify duplicate options throw exception.
     */
    @Test
    void shouldRejectDuplicateOption() {
        final CommandParser parser = new CommandParser();

        assertThrows(IllegalArgumentException.class, () -> parser.parse("customer list --phone 1 --phone 2"));
    }

    /**
     * @brief 验证引号未闭合抛出异常；
     *        Verify unclosed quote throws exception.
     */
    @Test
    void shouldRejectUnclosedQuote() {
        final CommandParser parser = new CommandParser();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse("customer register --name \"Alice"));

        assertTrue(exception.getMessage().contains("Unclosed quote"));
    }

    /**
     * @brief 验证 option 段后位置参数不被接受；
     *        Verify positional token after option section is rejected.
     */
    @Test
    void shouldRejectPositionalTokenAfterOptions() {
        final CommandParser parser = new CommandParser();

        assertThrows(IllegalArgumentException.class, () -> parser.parse("customer --id 1 show"));
    }
}
