package com.moesegfault.banking.infrastructure.gui.swing.view;

import com.moesegfault.banking.presentation.gui.view.MainMenuView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * @brief Swing 主菜单视图（Swing Main Menu View），把菜单项映射为 JMenuItem；
 *        Swing adapter for main menu using JMenuBar and JMenuItem.
 */
public final class SwingMainMenuView implements MainMenuView {

    /**
     * @brief 菜单栏组件（Menu Bar）；
     *        Swing menu bar component.
     */
    private final JMenuBar menuBar = new JMenuBar();

    /**
     * @brief 主菜单组件（Root Menu）；
     *        Root menu containing provided items.
     */
    private final JMenu rootMenu = new JMenu("Menu");

    /**
     * @brief 菜单项 id 列表（Menu Item Ids）；
     *        Cached menu item identifiers.
     */
    private final List<String> itemIds = new ArrayList<>();

    /**
     * @brief 选择事件回调（Selection Listener）；
     *        Callback executed when user selects one item.
     */
    private Consumer<String> selectionListener = ignored -> {
    };

    /**
     * @brief 构造主菜单；
     *        Construct Swing menu view.
     */
    public SwingMainMenuView() {
        menuBar.add(rootMenu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setItems(final List<String> itemIds) {
        final List<String> nonNullItems = List.copyOf(Objects.requireNonNull(itemIds, "itemIds must not be null"));
        this.itemIds.clear();
        this.itemIds.addAll(nonNullItems);
        rebuildMenuItems();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemSelected(final Consumer<String> listener) {
        this.selectionListener = Objects.requireNonNull(listener, "listener must not be null");
        rebuildMenuItems();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object component() {
        return menuBar;
    }

    /**
     * @brief 重建菜单项（Rebuild Menu Items）；
     *        Rebuild Swing menu items from current ids.
     */
    private void rebuildMenuItems() {
        rootMenu.removeAll();
        for (String itemId : itemIds) {
            final String normalizedItemId = Objects.requireNonNull(itemId, "itemId must not be null");
            final JMenuItem menuItem = new JMenuItem(normalizedItemId);
            menuItem.addActionListener(event -> selectionListener.accept(normalizedItemId));
            rootMenu.add(menuItem);
        }
        rootMenu.revalidate();
        rootMenu.repaint();
    }
}
