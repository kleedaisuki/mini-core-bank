package com.moesegfault.banking.presentation.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * @brief GUI 子领域模块组合器测试（GUI Subdomain Module Composer Test），验证子领域模块会被统一注册；
 *        Tests for GUI subdomain module composer to ensure subdomain modules are registered in one entry.
 */
class GuiSubdomainModulesTest {

    /**
     * @brief 验证组合模块会注册全部子领域页面（Verify Composer Registers All Subdomain Pages）；
     *        Verify composed module delegates and registers all subdomain pages.
     */
    @Test
    void shouldRegisterPagesFromAllSubdomainModules() {
        final GuiModule customerModule = registrar -> registrar.register("customer.register", context -> null);
        final GuiModule accountModule = registrar -> registrar.register("account.open-savings", context -> null);
        final GuiModule composedModule = GuiModule.subdomains(customerModule, accountModule);

        final GuiPageRegistry pageRegistry = new GuiPageRegistry();
        composedModule.registerPages(new GuiPageRegistrar(pageRegistry));

        assertEquals(2, pageRegistry.registeredPageIds().size());
    }

    /**
     * @brief 验证空模块会被拒绝（Verify Null Module Is Rejected）；
     *        Verify null subdomain module is rejected.
     */
    @Test
    void shouldRejectNullSubdomainModule() {
        final GuiModule customerModule = registrar -> registrar.register("customer.register", context -> null);

        assertThrows(NullPointerException.class, () -> GuiModule.subdomains(customerModule, null));
    }
}
