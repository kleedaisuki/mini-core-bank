package com.moesegfault.banking.presentation.cli.customer;

import com.moesegfault.banking.presentation.cli.CommandRegistry;
import java.util.Objects;

/**
 * @brief Customer CLI 命令注册器（Customer CLI Command Registration），注册 customer 领域命令映射；
 *        Customer CLI command registration utility for customer-domain command mappings.
 */
public final class CustomerCliCommandRegistration {

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private CustomerCliCommandRegistration() {
    }

    /**
     * @brief 注册 customer 命令（Register Customer Commands）；
     *        Register customer command paths to corresponding CLI handlers.
     *
     * @param registry 命令注册表（Command registry）。
     */
    public static void register(final CommandRegistry registry) {
        final CommandRegistry normalizedRegistry = Objects.requireNonNull(registry, "registry must not be null");
        normalizedRegistry.register("customer register", RegisterCustomerCliHandler.class);
        normalizedRegistry.register("customer show", ShowCustomerCliHandler.class);
        normalizedRegistry.register("customer list", ListCustomersCliHandler.class);
    }
}
