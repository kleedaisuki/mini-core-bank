package com.moesegfault.banking.presentation.cli.ledger;

import com.moesegfault.banking.presentation.cli.CommandRegistry;
import java.util.Objects;

/**
 * @brief Ledger CLI 命令注册器（Ledger CLI Command Registration），注册 ledger 领域命令映射；
 *        Ledger CLI command registration utility for ledger-domain command mappings.
 */
public final class LedgerCliCommandRegistration {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private LedgerCliCommandRegistration() {
    }

    /**
     * @brief 注册 ledger 命令（Register Ledger Commands）；
     *        Register ledger command paths to corresponding CLI handlers.
     *
     * @param registry 命令注册表（Command registry）。
     */
    public static void register(final CommandRegistry registry) {
        final CommandRegistry normalizedRegistry = Objects.requireNonNull(registry, "registry must not be null");
        normalizedRegistry.register("ledger balance", ShowBalanceCliHandler.class);
        normalizedRegistry.register("ledger entries", ShowEntriesCliHandler.class);
    }
}
