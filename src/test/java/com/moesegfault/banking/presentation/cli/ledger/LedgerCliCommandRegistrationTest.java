package com.moesegfault.banking.presentation.cli.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.presentation.cli.CommandRegistry;
import org.junit.jupiter.api.Test;

/**
 * @brief Ledger CLI 命令注册测试（Ledger CLI Command Registration Test），验证命令路径映射；
 *        Ledger CLI command-registration test verifying command-path mappings.
 */
class LedgerCliCommandRegistrationTest {

    /**
     * @brief 验证 ledger 命令可注册到对应 handler；
     *        Verify ledger commands are registered to corresponding handlers.
     */
    @Test
    void shouldRegisterLedgerCommands() {
        final CommandRegistry registry = new CommandRegistry();

        LedgerCliCommandRegistration.register(registry);

        assertTrue(registry.findHandlerType("ledger balance").isPresent());
        assertEquals(ShowBalanceCliHandler.class, registry.findHandlerType("ledger balance").orElseThrow());

        assertTrue(registry.findHandlerType("ledger entries").isPresent());
        assertEquals(ShowEntriesCliHandler.class, registry.findHandlerType("ledger entries").orElseThrow());
    }
}
