package com.moesegfault.banking.infrastructure.gui.swing.view;

import com.moesegfault.banking.presentation.gui.view.MainMenuView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
     * @brief 菜单组件映射（Menu Component Map）；
     *        Menu components keyed by group label.
     */
    private final Map<String, JMenu> menus = new LinkedHashMap<>();

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
        menuBar.removeAll();
        menus.clear();
        for (String itemId : itemIds) {
            final String normalizedItemId = Objects.requireNonNull(itemId, "itemId must not be null");
            final MenuItemParts parts = MenuItemParts.from(normalizedItemId);
            final JMenu menu = menus.computeIfAbsent(parts.groupLabel(), this::createMenu);
            final JMenuItem menuItem = new JMenuItem(parts.itemLabel());
            menuItem.addActionListener(event -> selectionListener.accept(normalizedItemId));
            menu.add(menuItem);
        }
        menuBar.revalidate();
        menuBar.repaint();
    }

    /**
     * @brief 创建菜单组（Create Menu Group）；
     *        Create and attach one menu group.
     *
     * @param groupLabel 菜单组标签（Menu group label）。
     * @return 菜单组（Menu group）。
     */
    private JMenu createMenu(final String groupLabel) {
        final JMenu menu = new JMenu(groupLabel);
        menuBar.add(menu);
        return menu;
    }

    /**
     * @brief 菜单项拆分结果（Menu Item Parts）；
     *        Parsed menu item group and item labels.
     *
     * @param groupLabel 菜单组标签（Menu group label）。
     * @param itemLabel 菜单项标签（Menu item label）。
     */
    private record MenuItemParts(String groupLabel, String itemLabel) {

        /**
         * @brief 从规范菜单项解析（Parse Canonical Menu Item）；
         *        Parse a canonical menu item label.
         *
         * @param itemId 菜单项 ID（Menu item id）。
         * @return 拆分结果（Parsed parts）。
         */
        private static MenuItemParts from(final String itemId) {
            final int separator = itemId.indexOf('/');
            if (separator <= 0 || separator >= itemId.length() - 1) {
                return new MenuItemParts("Navigate", itemId);
            }
            return new MenuItemParts(itemId.substring(0, separator), itemId.substring(separator + 1));
        }
    }
}
