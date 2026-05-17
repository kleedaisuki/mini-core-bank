package com.moesegfault.banking.infrastructure.gui.swing.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JMenuBar;
import org.junit.jupiter.api.Test;

/**
 * @brief Swing 主菜单视图测试（Swing Main Menu View Test），验证分组导航菜单；
 *        Tests grouped navigation menu rendering for the Swing main-menu view.
 */
class SwingMainMenuViewTest {

    /**
     * @brief 验证菜单项按斜杠前缀分组；
     *        Verify slash-prefixed item IDs are grouped into top-level menus.
     */
    @Test
    void shouldGroupNavigationItemsByPrefix() {
        final SwingMainMenuView view = new SwingMainMenuView();
        final AtomicReference<String> selectedItem = new AtomicReference<>();

        view.setItems(List.of(
                "Customers/List Customers",
                "Customers/Register Customer",
                "Ledger/Show Balance"));
        view.onItemSelected(selectedItem::set);

        final JMenuBar menuBar = (JMenuBar) view.component();
        assertEquals(2, menuBar.getMenuCount());
        assertEquals("Customers", menuBar.getMenu(0).getText());
        assertEquals("List Customers", menuBar.getMenu(0).getItem(0).getText());

        menuBar.getMenu(0).getItem(1).doClick();
        assertEquals("Customers/Register Customer", selectedItem.get());
    }
}
