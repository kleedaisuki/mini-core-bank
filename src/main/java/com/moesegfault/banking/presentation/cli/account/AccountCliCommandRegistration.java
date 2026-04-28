package com.moesegfault.banking.presentation.cli.account;

import com.moesegfault.banking.presentation.cli.CommandRegistry;
import java.util.Objects;

/**
 * @brief Account CLI 命令注册器（Account CLI Command Registration），注册 account 领域命令映射；
 *        Account CLI command registration utility for account-domain command mappings.
 */
public final class AccountCliCommandRegistration {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private AccountCliCommandRegistration() {
    }

    /**
     * @brief 注册 account 命令（Register Account Commands）；
     *        Register account command paths to corresponding CLI handlers.
     *
     * @param registry 命令注册表（Command registry）。
     */
    public static void register(final CommandRegistry registry) {
        final CommandRegistry normalizedRegistry = Objects.requireNonNull(registry, "registry must not be null");
        normalizedRegistry.register("account open-savings", OpenSavingsAccountCliHandler.class);
        normalizedRegistry.register("account open-fx", OpenFxAccountCliHandler.class);
        normalizedRegistry.register("account open-investment", OpenInvestmentAccountCliHandler.class);
        normalizedRegistry.register("account show", ShowAccountCliHandler.class);
        normalizedRegistry.register("account list", ListAccountsCliHandler.class);
        normalizedRegistry.register("account freeze", FreezeAccountCliHandler.class);
    }
}
