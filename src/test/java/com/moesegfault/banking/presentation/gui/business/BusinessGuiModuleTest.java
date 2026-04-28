package com.moesegfault.banking.presentation.gui.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.moesegfault.banking.application.business.query.FindBusinessTransactionHandler;
import com.moesegfault.banking.application.business.query.ListBusinessTransactionsHandler;
import com.moesegfault.banking.presentation.gui.GuiContext;
import com.moesegfault.banking.presentation.gui.GuiPage;
import com.moesegfault.banking.presentation.gui.GuiPageRegistrar;
import com.moesegfault.banking.presentation.gui.GuiPageRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @brief 业务流水 GUI 模块测试（Business GUI Module Test），验证页面注册和工厂创建类型；
 *        Tests for business GUI module page registration and factory output types.
 */
class BusinessGuiModuleTest {

    /**
     * @brief 验证模块会注册详情页和列表页；
     *        Verify module registers both detail and list pages.
     */
    @Test
    void shouldRegisterBusinessPages() {
        final FindBusinessTransactionHandler findHandler = Mockito.mock(FindBusinessTransactionHandler.class);
        final ListBusinessTransactionsHandler listHandler = Mockito.mock(ListBusinessTransactionsHandler.class);
        final BusinessGuiModule module = new BusinessGuiModule(findHandler, listHandler);

        final GuiPageRegistry registry = new GuiPageRegistry();
        final GuiPageRegistrar registrar = new GuiPageRegistrar(registry);
        module.registerPages(registrar);

        final GuiPage showPage = registry.findPageFactory(ShowBusinessTransactionPageFactory.PAGE_ID)
                .orElseThrow()
                .createPage(new GuiContext());
        final GuiPage listPage = registry.findPageFactory(ListBusinessTransactionsPageFactory.PAGE_ID)
                .orElseThrow()
                .createPage(new GuiContext());

        assertEquals(ShowBusinessTransactionPageFactory.PAGE_ID, showPage.pageId());
        assertEquals(ListBusinessTransactionsPageFactory.PAGE_ID, listPage.pageId());
        assertInstanceOf(ShowBusinessTransactionModel.class, showPage.model());
        assertInstanceOf(ListBusinessTransactionsModel.class, listPage.model());
    }
}
