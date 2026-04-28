package com.moesegfault.banking.presentation.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * @brief GUI 页面注册表测试（GUI Page Registry Test），验证注册与查找行为；
 *        Tests for GUI page registry registration and lookup behavior.
 */
class GuiPageRegistryTest {

    /**
     * @brief 验证已注册页面可被查找；
     *        Verify registered page can be found.
     */
    @Test
    void shouldFindRegisteredPageFactory() {
        final GuiPageRegistry registry = new GuiPageRegistry();
        final GuiPageId pageId = GuiPageId.of("customer.register");
        final GuiPageFactory pageFactory = context -> null;

        registry.register(pageId, pageFactory);

        assertEquals(pageFactory, registry.findPageFactory(pageId).orElseThrow());
    }

    /**
     * @brief 验证重复注册不同工厂会被拒绝；
     *        Verify duplicate registration with different factory is rejected.
     */
    @Test
    void shouldRejectDifferentFactoryForSamePageId() {
        final GuiPageRegistry registry = new GuiPageRegistry();
        final GuiPageId pageId = GuiPageId.of("customer.list");
        registry.register(pageId, context -> null);

        assertThrows(IllegalStateException.class, () -> registry.register(pageId, context -> null));
    }

    /**
     * @brief 验证页面标识规范化为小写；
     *        Verify page identifier is normalized to lower case.
     */
    @Test
    void shouldNormalizePageIdToLowerCase() {
        assertTrue(GuiPageId.of("Customer.Show").equals(GuiPageId.of("customer.show")));
    }
}
