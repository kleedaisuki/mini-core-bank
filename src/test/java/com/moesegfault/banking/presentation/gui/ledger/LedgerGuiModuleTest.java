package com.moesegfault.banking.presentation.gui.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.moesegfault.banking.presentation.gui.GuiPageFactory;
import com.moesegfault.banking.presentation.gui.GuiPageRegistrar;
import com.moesegfault.banking.presentation.gui.GuiPageRegistry;
import org.junit.jupiter.api.Test;

/**
 * @brief Ledger GUI 模块测试（Ledger GUI Module Test），验证页面注册映射；
 *        Tests for ledger GUI module page-registration mappings.
 */
class LedgerGuiModuleTest {

    /**
     * @brief 验证模块会注册 ledger.balance 与 ledger.entries 页面；
     *        Verify module registers ledger.balance and ledger.entries pages.
     */
    @Test
    void shouldRegisterLedgerPages() {
        final GuiPageRegistry pageRegistry = new GuiPageRegistry();
        final GuiPageRegistrar registrar = new GuiPageRegistrar(pageRegistry);
        final GuiPageFactory balanceFactory = context -> null;
        final GuiPageFactory entriesFactory = context -> null;

        final LedgerGuiModule module = new LedgerGuiModule(balanceFactory, entriesFactory);
        module.registerPages(registrar);

        assertEquals(balanceFactory, pageRegistry.findPageFactory(LedgerGuiPageIds.SHOW_BALANCE).orElseThrow());
        assertEquals(entriesFactory, pageRegistry.findPageFactory(LedgerGuiPageIds.SHOW_ENTRIES).orElseThrow());
    }
}
