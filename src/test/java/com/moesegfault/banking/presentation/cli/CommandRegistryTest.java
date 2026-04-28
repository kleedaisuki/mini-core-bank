package com.moesegfault.banking.presentation.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * @brief 命令注册表单元测试（Command Registry Unit Test），验证映射注册与归一化行为；
 *        Unit tests for command-registry mapping registration and normalization behavior.
 */
class CommandRegistryTest {

    /**
     * @brief 验证命令路径会被规范化并可查找；
     *        Verify command path normalization and lookup.
     */
    @Test
    void shouldNormalizeCommandPathOnRegisterAndLookup() {
        final CommandRegistry registry = new CommandRegistry();

        registry.register("  Customer    Register  ", NoopHandler.class);

        assertTrue(registry.findHandlerType("customer register").isPresent());
        assertEquals(NoopHandler.class, registry.findHandlerType("CUSTOMER REGISTER").orElseThrow());
    }

    /**
     * @brief 验证同命令不同 handler 重复注册会失败；
     *        Verify duplicate registration with different handler fails.
     */
    @Test
    void shouldRejectDifferentHandlerForSameCommandPath() {
        final CommandRegistry registry = new CommandRegistry();
        registry.register("customer register", NoopHandler.class);

        assertThrows(IllegalStateException.class, () -> registry.register("customer register", AnotherHandler.class));
    }

    /**
     * @brief 验证同命令同 handler 重复注册幂等；
     *        Verify idempotent duplicate registration for same command and handler.
     */
    @Test
    void shouldAllowIdempotentRegistrationForSameHandler() {
        final CommandRegistry registry = new CommandRegistry();

        registry.register("customer register", NoopHandler.class);
        registry.register("customer register", NoopHandler.class);

        assertEquals(1, registry.registeredCommandPaths().size());
    }

    /**
     * @brief 空实现 handler（No-op Handler）；
     *        No-op handler for registry tests.
     */
    private static final class NoopHandler implements CliCommandHandler {

        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(final ParsedCommand command) {
            // no-op
        }
    }

    /**
     * @brief 另一个空实现 handler（Another No-op Handler）；
     *        Another no-op handler for duplicate-registration tests.
     */
    private static final class AnotherHandler implements CliCommandHandler {

        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(final ParsedCommand command) {
            // no-op
        }
    }
}
