package com.moesegfault.banking.presentation.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * @brief 页面注册器测试（GUI Page Registrar Test），验证链式与批量注册能力；
 *        Tests for fluent and bulk registration behavior of GUI page registrar.
 */
class GuiPageRegistrarTest {

    /**
     * @brief 验证字符串标识注册生效；
     *        Verify registration by string page id works.
     */
    @Test
    void shouldRegisterByStringPageId() {
        final GuiPageRegistry registry = new GuiPageRegistry();
        final GuiPageRegistrar registrar = new GuiPageRegistrar(registry);
        final GuiPageFactory pageFactory = context -> null;

        registrar.register("customer.register", pageFactory);

        assertEquals(pageFactory, registry.findPageFactory(GuiPageId.of("customer.register")).orElseThrow());
    }

    /**
     * @brief 验证链式注册可连续执行；
     *        Verify fluent registration can register multiple pages continuously.
     */
    @Test
    void shouldSupportFluentRegistration() {
        final GuiPageRegistry registry = new GuiPageRegistry();
        final GuiPageRegistrar registrar = new GuiPageRegistrar(registry);

        registrar.register("p1", context -> null)
                .register("p2", context -> null);

        assertEquals(2, registry.registeredPageIds().size());
    }

    /**
     * @brief 验证重复注册不同工厂会失败；
     *        Verify duplicate page id with different factory fails.
     */
    @Test
    void shouldRejectDuplicateDifferentFactory() {
        final GuiPageRegistry registry = new GuiPageRegistry();
        final GuiPageRegistrar registrar = new GuiPageRegistrar(registry);
        registrar.register("same", context -> null);

        assertThrows(IllegalStateException.class, () -> registrar.register("same", context -> null));
    }
}
