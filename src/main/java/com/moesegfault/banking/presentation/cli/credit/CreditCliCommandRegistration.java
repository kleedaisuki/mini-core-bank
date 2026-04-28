package com.moesegfault.banking.presentation.cli.credit;

import com.moesegfault.banking.presentation.cli.CommandRegistry;
import java.util.Objects;

/**
 * @brief Credit CLI 命令注册器（Credit CLI Command Registration），注册 credit 领域命令映射；
 *        Credit CLI command registration utility for credit-domain command mappings.
 */
public final class CreditCliCommandRegistration {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private CreditCliCommandRegistration() {
    }

    /**
     * @brief 注册 credit 命令（Register Credit Commands）；
     *        Register credit command paths to corresponding CLI handlers.
     *
     * @param registry 命令注册表（Command registry）。
     */
    public static void register(final CommandRegistry registry) {
        final CommandRegistry normalizedRegistry = Objects.requireNonNull(registry, "registry must not be null");
        normalizedRegistry.register("credit generate-statement", GenerateStatementCliHandler.class);
        normalizedRegistry.register("credit repay", RepayCreditCardCliHandler.class);
        normalizedRegistry.register("credit statement", ShowStatementCliHandler.class);
    }
}
