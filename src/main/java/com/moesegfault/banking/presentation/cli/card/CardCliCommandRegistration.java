package com.moesegfault.banking.presentation.cli.card;

import com.moesegfault.banking.presentation.cli.CommandRegistry;
import java.util.Objects;

/**
 * @brief Card CLI 命令注册器（Card CLI Command Registration），注册 card 领域命令映射；
 *        Card CLI command registration utility for card-domain command mappings.
 */
public final class CardCliCommandRegistration {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private CardCliCommandRegistration() {
    }

    /**
     * @brief 注册 card 命令（Register Card Commands）；
     *        Register card command paths to corresponding CLI handlers.
     *
     * @param registry 命令注册表（Command registry）。
     */
    public static void register(final CommandRegistry registry) {
        final CommandRegistry normalizedRegistry = Objects.requireNonNull(registry, "registry must not be null");
        normalizedRegistry.register("card issue-debit", IssueDebitCardCliHandler.class);
        normalizedRegistry.register("card issue-supplementary-debit", IssueSupplementaryDebitCardCliHandler.class);
        normalizedRegistry.register("card issue-credit", IssueCreditCardCliHandler.class);
        normalizedRegistry.register("card issue-supplementary-credit", IssueSupplementaryCreditCardCliHandler.class);
        normalizedRegistry.register("card show", ShowCardCliHandler.class);
    }
}
