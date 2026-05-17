package com.moesegfault.banking.presentation.cli.investment;

import com.moesegfault.banking.presentation.cli.CommandRegistry;
import java.util.Objects;

/**
 * @brief Investment CLI 命令注册器（Investment CLI Command Registration），注册 investment 领域命令映射；
 *        Investment CLI command registration utility for investment-domain command mappings.
 */
public final class InvestmentCliCommandRegistration {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private InvestmentCliCommandRegistration() {
    }

    /**
     * @brief 注册 investment 命令（Register Investment Commands）；
     *        Register investment command paths to corresponding CLI handlers.
     *
     * @param registry 命令注册表（Command registry）。
     */
    public static void register(final CommandRegistry registry) {
        final CommandRegistry normalizedRegistry = Objects.requireNonNull(registry, "registry must not be null");
        normalizedRegistry.register("investment product-create", CreateProductCliHandler.class);
        normalizedRegistry.register("investment buy", BuyProductCliHandler.class);
        normalizedRegistry.register("investment sell", SellProductCliHandler.class);
        normalizedRegistry.register("investment holdings", ShowHoldingCliHandler.class);
    }
}
